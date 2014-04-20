package com.yassirh.digitalocean.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.DropletDao;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.model.Region;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.utils.ApiHelper;

public class DropletService {

	private Context mContext;
	
	public enum DropletActions{
		REBOOT, POWER_CYCLE, SHUTDOWN, POWER_OFF, POWER_ON,
		PASSWORD_RESET, RESIZE, SNAPSHOT, RESTORE, REBUILD, 
		ENABLE_BACKUPS, DISABLE_BACKUPS, RENAME, DESTROY
	}
		
	public DropletService(Context context) {
		mContext = context;
	}
	
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private boolean isRefreshing;
	
	
	public void setRequiresRefresh(Boolean requireRefresh){
		SharedPreferences settings = mContext.getSharedPreferences("prefrences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("droplet_require_refresh", requireRefresh);
		editor.commit();
	}
	public Boolean requiresRefresh(){
		SharedPreferences settings = mContext.getSharedPreferences("prefrences", 0);
		return settings.getBoolean("droplet_require_refresh", true);
	}
	
	public void ExecuteAction(final long dropletId,final DropletActions dropletAction, HashMap<String,String> params){
		
		String action = getAction(dropletAction);
		String url = getActionUrl(dropletId,action,params);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if(ApiHelper.API_STATUS_OK.equals(status)){
						Long eventId = jsonObject.getLong("event_id");
						new EventService(mContext).trackEvent(eventId, DropletService.this.findById(dropletId).getName(),getActionName(dropletAction));
					}
					else{
						// TODO handle error Access Denied/Not Found
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    }
		    
		    @Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					Toast.makeText(mContext, R.string.access_denied_message, Toast.LENGTH_SHORT).show();
				}
			}
		    
		    @Override
		    public void onFinish() {
		    	getDropletFromAPI(dropletId, false);
		    }
		});
	}
	
	private String getAction(DropletActions dropletAction) {
		String action = "";
		switch (dropletAction) {
		case REBOOT:
			action = "reboot";
			break;
		case POWER_CYCLE:
			action = "power_cycle";
			break;
		case SHUTDOWN:
			action = "shutdown";
			break;
		case POWER_OFF:
			action = "power_off";
			break;
		case POWER_ON:
			action = "power_on";
			break;
		case PASSWORD_RESET:
			action = "password_reset";
			break;
		case DESTROY:
			action = "destroy";
			break;
		case RESIZE:
			action = "resize";
			break;
		case SNAPSHOT:
			action = "snapshot";
			break;
		case RESTORE:
			action = "restore";
			break;
		case REBUILD:
			action = "rebuild";
			break;
		case ENABLE_BACKUPS:
			action = "enable_backups";
			break;
		case DISABLE_BACKUPS:
			action = "disable_backups";
			break;
		default:
			break;
		}
		return action;
	}
	
	private String getActionName(DropletActions dropletAction) {
		String action = "";
		switch (dropletAction) {
		case REBOOT:
			action = mContext.getResources().getString(R.string.reboot);
			break;
		case POWER_CYCLE:
			action = mContext.getResources().getString(R.string.power_cycle);
			break;
		case SHUTDOWN:
			action = mContext.getResources().getString(R.string.shut_down);
			break;
		case POWER_OFF:
			action = mContext.getResources().getString(R.string.power_off);
			break;
		case POWER_ON:
			action = mContext.getResources().getString(R.string.power_on);
			break;
		case PASSWORD_RESET:
			action = mContext.getResources().getString(R.string.password_reset);
			break;
		case DESTROY:
			action = mContext.getResources().getString(R.string.destroy);
			break;
		case RESIZE:
			action = mContext.getResources().getString(R.string.resize);
			break;
		case SNAPSHOT:
			action = mContext.getResources().getString(R.string.snapshot);
			break;
		case RESTORE:
			action = mContext.getResources().getString(R.string.restore);
			break;
		case REBUILD:
			action = mContext.getResources().getString(R.string.rebuild);
			break;
		case ENABLE_BACKUPS:
			action = mContext.getResources().getString(R.string.enable_backups);
			break;
		case DISABLE_BACKUPS:
			action = mContext.getResources().getString(R.string.disable_backups);
			break;
		default:
			break;
		}
		return action;
	}

	private String getActionUrl(long dropletId,String action, HashMap<String,String> params) {
		String url  = "https://api.digitalocean.com/droplets/" + dropletId + "/" + action + "/?client_id=" + ApiHelper.getClientId(mContext) + "&api_key=" + ApiHelper.getAPIKey(mContext);
		Iterator<Entry<String, String>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> pairs = it.next();
			url += "&" + pairs.getKey() + "=" + pairs.getValue();
		}
		return url;
	}

	public void getAllDropletsFromAPI(final boolean showProgress){
		isRefreshing = true;
		String url = "https://api.digitalocean.com/droplets/?client_id=" + ApiHelper.getClientId(mContext) + "&api_key=" + ApiHelper.getAPIKey(mContext); 
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
			NotificationManager mNotifyManager;
			NotificationCompat.Builder mBuilder;
			
			@Override
			public void onStart() {
				if(showProgress){
					mNotifyManager =
					        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
					mBuilder = new NotificationCompat.Builder(mContext);
					mBuilder.setContentTitle(mContext.getResources().getString(R.string.synchronising))
					    .setContentText(mContext.getResources().getString(R.string.synchronising_droplets))
					    .setSmallIcon(R.drawable.ic_launcher);
					mBuilder.setContentIntent(PendingIntent.getActivity(mContext,0,new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_DROPLETS, mBuilder.build());
				}
			}
			
			@Override
			public void onFinish() {
				isRefreshing = false;
				if(showProgress){
					mNotifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_DROPLETS);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					Toast.makeText(mContext, R.string.access_denied_message, Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {	
				if(showProgress){
					mBuilder.setProgress(100, (int)100*bytesWritten/totalSize, false);
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_DROPLETS, mBuilder.build());
				}
			}
			
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					List<Droplet> droplets = new ArrayList<Droplet>();
					if(ApiHelper.API_STATUS_OK.equals(status)){
						JSONArray dropletsJSONArray = jsonObject.getJSONArray("droplets");
						for(int i = 0; i < dropletsJSONArray.length(); i++){
							JSONObject dropletJSONObject = dropletsJSONArray.getJSONObject(i);
							Droplet droplet = jsonObjectToDroplet(dropletJSONObject);							
							droplets.add(droplet);
						}
						DropletService.this.deleteAll();
						DropletService.this.saveAll(droplets);
						DropletService.this.setRequiresRefresh(true);
					}
					else{
						// TODO handle error Access Denied/Not Found
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}  
		    }			
		});
	}
	
	public void deleteAll() {
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(mContext));
		dropletDao.deleteAll();
	}

	private Droplet jsonObjectToDroplet(JSONObject dropletJSONObject) throws JSONException {
		Droplet droplet = new Droplet();
		Image image = new Image();
		image.setId(dropletJSONObject.getLong("image_id"));
		Region region = new Region();
		region.setId(dropletJSONObject.getLong("region_id"));
		Size size = new Size();
		size.setId(dropletJSONObject.getLong("size_id"));
		
		droplet.setId(dropletJSONObject.getLong("id"));
		droplet.setName(dropletJSONObject.getString("name"));
		droplet.setImage(image);
		droplet.setRegion(region);
		droplet.setSize(size);
		droplet.setBackupsActive(dropletJSONObject.getBoolean("backups_active"));
		droplet.setIpAddress(dropletJSONObject.getString("ip_address"));
		droplet.setPrivateIpAddress(dropletJSONObject.getString("private_ip_address"));
		droplet.setLocked(dropletJSONObject.getBoolean("locked"));
		droplet.setStatus(dropletJSONObject.getString("status"));
		try {
			droplet.setCreatedAt(iso8601Format.parse(dropletJSONObject.getString("created_at").replace("Z", "")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return droplet;
	}

	protected void saveAll(List<Droplet> droplets) {
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(mContext));
		for (Droplet droplet : droplets) {
			dropletDao.createOrUpdate(droplet);
		}
		DropletService.this.setRequiresRefresh(true);
	}
	
	public List<Droplet> getAllDroplets(){
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(mContext));
		List<Droplet> droplets = dropletDao.getAll(null);
		return droplets;
	}

	public Droplet findById(long id) {
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(mContext));
		Droplet droplet = dropletDao.findById(id);
		return droplet;
	}

	public void createDroplet(String hostname, Long imageId, Long regionId, Long sizeId,
			boolean privateNetworking, boolean enableBackups, List<Long> selectedSSHKeysIds) {
		String sshKeys = "";
		if(selectedSSHKeysIds.size() > 0){
			StringBuilder sb = new StringBuilder();
			for (Long l : selectedSSHKeysIds)
				sb.append("," + l);
			sshKeys = "&ssh_key_ids=" + sb.substring(1);
		}
		
		String url = "https://api.digitalocean.com/droplets/new?client_id=" + ApiHelper.getClientId(mContext) + 
				"&api_key=" + ApiHelper.getAPIKey(mContext) + 
				"&name=" + hostname +
				"&size_id=" + sizeId + 
				"&image_id=" + imageId + 
				"&region_id=" + regionId +
				"&private_networking" + privateNetworking +
				"&backups_enabled=" + enableBackups +
				sshKeys;
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if(ApiHelper.API_STATUS_OK.equals(status)){
						JSONObject dropletJsonObject = jsonObject.getJSONObject("droplet");
						Long eventId = dropletJsonObject.getLong("event_id");
						new EventService(mContext).trackEvent(eventId, mContext.getString(R.string.creating_droplet),"");
					}
					else{
						// TODO handle error Access Denied/Not Found
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    }
		    @Override
		    public void onFinish() {
		    }
		});
	}

	public void getDropletFromAPI(long dropletId, final boolean showProgress) {
		String url = "https://api.digitalocean.com/droplets/" + dropletId + "?client_id=" + ApiHelper.getClientId(mContext) + "&api_key=" + ApiHelper.getAPIKey(mContext); 
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
			NotificationManager mNotifyManager;
			NotificationCompat.Builder mBuilder;
			
			@Override
			public void onStart() {
				if(showProgress){
					mNotifyManager =
					        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
					mBuilder = new NotificationCompat.Builder(mContext);
					mBuilder.setContentTitle(mContext.getResources().getString(R.string.synchronising))
					    .setContentText(mContext.getResources().getString(R.string.synchronising_droplets))
					    .setSmallIcon(R.drawable.ic_launcher);

					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_DROPLET, mBuilder.build());
				}
			}
			
			@Override
			public void onFinish() {
				if(showProgress)
					mNotifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_DROPLET);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					Toast.makeText(mContext, R.string.access_denied_message, Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {	
				if(showProgress){
					mBuilder.setProgress(100, (int)100*bytesWritten/totalSize, false);
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_DROPLET, mBuilder.build());
				}
			}
			
		    @Override
		    public void onSuccess(String response) { 
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					List<Droplet> droplets = new ArrayList<Droplet>();
					if(ApiHelper.API_STATUS_OK.equals(status)){
						JSONObject dropletJSONObject = jsonObject.getJSONObject("droplet");
						Droplet droplet = jsonObjectToDroplet(dropletJSONObject);							
						droplets.add(droplet);
						DropletService.this.update(droplet);
						DropletService.this.setRequiresRefresh(true);
					}
					else{
						// TODO handle error Access Denied/Not Found
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    }			
		});
	}

	protected void update(Droplet droplet) {
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(mContext));
		dropletDao.createOrUpdate(droplet);
	}
	public boolean isRefreshing() {
		return isRefreshing;
	}	
}
