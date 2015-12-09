package com.yassirh.digitalocean.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.DropletDao;
import com.yassirh.digitalocean.data.DropletTable;
import com.yassirh.digitalocean.data.ImageDao;
import com.yassirh.digitalocean.data.NetworkDao;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.model.Network;
import com.yassirh.digitalocean.model.Region;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.utils.ApiHelper;
import com.yassirh.digitalocean.utils.MyApplication;
import com.yassirh.digitalocean.utils.PreferencesHelper;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import cz.msebera.android.httpclient.HttpEntity;

public class DropletService {

	private Context context;
	private boolean isRefreshing;

    public enum DropletActions{
		REBOOT, POWER_CYCLE, SHUTDOWN, POWER_OFF, POWER_ON,
		PASSWORD_RESET, RESIZE, SNAPSHOT, RESTORE, REBUILD,
		ENABLE_BACKUPS, DISABLE_BACKUPS, RENAME 
	}
		
	public DropletService(Context context) {
		this.context = context;
	}
	
	
	public void setRequiresRefresh(Boolean requireRefresh){
		SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("droplet_require_refresh", requireRefresh);
		editor.commit();
	}
	
	public Boolean requiresRefresh(){
		SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
		return settings.getBoolean("droplet_require_refresh", true);
	}
	
	public void executeAction(final long dropletId, final DropletActions dropletAction, HashMap<String, String> params){
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		String action = getAction(dropletAction);
		String url = String.format(Locale.US,"%s/droplets/%d/actions", ApiHelper.API_URL, dropletId);
		
		HashMap<String,Object> options = new HashMap<>();
		options.put("type", action);
		for (Entry<String, String> param : params.entrySet()) {
			options.put(param.getKey(), param.getValue());
		}
		
		JSONObject jsonObject = new JSONObject(options);
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
		HttpEntity entity;
		try {
			entity = new cz.msebera.android.httpclient.entity.ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
			client.post(context, url, entity, "application/json", new AsyncHttpResponseHandler() {



                @Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                }

                @Override
				public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
					if(statusCode == 401){
						ApiHelper.showAccessDenied();
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
		case RESIZE:
			action = "resize";
			break;
		case SNAPSHOT:
			action = "snapshot";
			break;
		case RESTORE:
			action = "restore";
			break;
        case RENAME:
            action = "rename";
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
	
	public void getAllDropletsFromAPI(final boolean showProgress, boolean synchronous){
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		isRefreshing = true;
		String url = String.format("%s/droplets?per_page=%d", ApiHelper.API_URL, Integer.MAX_VALUE);
		AsyncHttpClient client;
        client  = synchronous ? new AsyncHttpClient() : new SyncHttpClient();
		client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
        client.get(url, new AsyncHttpResponseHandler() {
			NotificationManager notifyManager;
			NotificationCompat.Builder builder;
			
			@Override
			public void onStart() {
				if(showProgress){
					notifyManager =
					        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					builder = new NotificationCompat.Builder(context);
					builder.setContentTitle(context.getResources().getString(R.string.synchronising))
					    .setContentText(context.getResources().getString(R.string.synchronising_droplets))
					    .setSmallIcon(R.drawable.ic_launcher);
					builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
					Notification note = builder.build();
					note.flags |= Notification.FLAG_ONGOING_EVENT;
					notifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_DROPLETS, note);
				}
			}
			
			@Override
			public void onFinish() {
				isRefreshing = false;
				if(showProgress){
					notifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_DROPLETS);
				}
                if(PreferencesHelper.isAutoRestartingDropetsEnabled(context)){
                    startTurnedOffDroplets();
                }
			}
			
			@Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 401){
					ApiHelper.showAccessDenied();
				}
				Toast.makeText(context, "An error occurred - check your internet connection?", Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onProgress(long bytesWritten, long totalSize) {
				if(showProgress){
					builder.setProgress(100, (int) (100*bytesWritten/totalSize), false);
					notifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_DROPLETS, builder.build());
				}
			}

            @Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    List<Droplet> droplets = new ArrayList<>();
                    JSONArray dropletsJSONArray = jsonObject.getJSONArray("droplets");
                    for(int i = 0; i < dropletsJSONArray.length(); i++){
                        JSONObject dropletJSONObject = dropletsJSONArray.getJSONObject(i);
                        Droplet droplet = jsonObjectToDroplet(dropletJSONObject);
                        droplets.add(droplet);
                    }
                    DropletService.this.deleteAll();
                    DropletService.this.saveAll(droplets);
                    DropletService.this.setRequiresRefresh(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
	}
	
	public void deleteAll() {
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(context));
		dropletDao.deleteAll();
		NetworkDao networkDao = new NetworkDao(DatabaseHelper.getInstance(context));
		networkDao.deleteAll();
	}

	
	public static Network jsonObjectToNetwork(JSONObject networkJSONObject) throws JSONException {
		Network network = new Network();
		if(!networkJSONObject.isNull("cidr")){
			network.setCidr(networkJSONObject.getString("cidr"));
		}
		if(!networkJSONObject.isNull("netmask")){
			network.setNetmask(networkJSONObject.getString("netmask"));
		}
		network.setGateway(networkJSONObject.getString("gateway"));
		network.setIpAddress(networkJSONObject.getString("ip_address"));
		network.setType(networkJSONObject.getString("type"));
		return network;
	}
	
	public static Droplet jsonObjectToDroplet(JSONObject dropletJSONObject) throws JSONException {
		Droplet droplet = new Droplet();
		Image image = ImageService.jsonObjectToImage(dropletJSONObject.getJSONObject("image"));
        ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(MyApplication.getAppContext()));
        if(imageDao.findById(image.getId()) == null){
            image.setInUse(false);
            imageDao.create(image);
        }
		Region region = RegionService.jsonObjectToRegion(dropletJSONObject.getJSONObject("region"));
		Size size = new Size();
        size.setSlug(dropletJSONObject.getString("size_slug"));
		droplet.setId(dropletJSONObject.getLong("id"));
		droplet.setName(dropletJSONObject.getString("name"));
		droplet.setMemory(dropletJSONObject.getInt("memory"));
		droplet.setCpu(dropletJSONObject.getInt("vcpus"));
		droplet.setDisk(dropletJSONObject.getInt("disk"));
		droplet.setImage(image);
		droplet.setRegion(region);
		droplet.setSize(size);
		droplet.setLocked(dropletJSONObject.getBoolean("locked"));
		droplet.setStatus(dropletJSONObject.getString("status"));
		
		List<Network> networks = new ArrayList<>();
		JSONObject networksJSONObject = dropletJSONObject.getJSONObject("networks");
		
		JSONArray v4JSONArray = networksJSONObject.getJSONArray("v4");
		JSONArray v6JSONArray = networksJSONObject.getJSONArray("v6");
		
		for (int i = 0; i < v4JSONArray.length(); i++) {
			JSONObject networkJSONObject = v4JSONArray.getJSONObject(i);
			Network network = jsonObjectToNetwork(networkJSONObject);
			networks.add(network);
		}
		
		for (int i = 0; i < v6JSONArray.length(); i++) {
			JSONObject networkJSONObject = v6JSONArray.getJSONObject(i);
			Network network = jsonObjectToNetwork(networkJSONObject);
			networks.add(network);
		}
		
		JSONArray featuresJSONArray = dropletJSONObject.getJSONArray("features");
		for (int i = 0; i < featuresJSONArray.length(); i++) {
			if("ipv6".equals(featuresJSONArray.getString(i))){
				droplet.setIpv6Enabled(true);
			}else if("virtio".equals(featuresJSONArray.getString(i))){
				droplet.setVirtIoEnabled(true);
			}else if("private_networking".equals(featuresJSONArray.getString(i))){
				droplet.setPrivateNetworkingEnabled(true);
			}else if("backups".equals(featuresJSONArray.getString(i))){
				droplet.setBackupsEnabled(true);
			}
		}
		
		droplet.setNetworks(networks);
		
		try {
			droplet.setCreatedAt(ApiHelper.iso8601Format.parse(dropletJSONObject.getString("created_at").replace("Z", "")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return droplet;
	}

	protected void saveAll(List<Droplet> droplets) {
		NetworkDao networkDao = new NetworkDao(DatabaseHelper.getInstance(context));
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(context));
		networkDao.deleteAll();
		for (Droplet droplet : droplets) {
			long id = dropletDao.createOrUpdate(droplet);
			droplet.setId(id);
			for (Network network : droplet.getNetworks()) {
				network.setDroplet(droplet);
				networkDao.createOrUpdate(network);
			}
		}
		DropletService.this.setRequiresRefresh(true);
	}
	
	public List<Droplet> getAllDroplets(){
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(context));
		NetworkDao networkDao = new NetworkDao(DatabaseHelper.getInstance(context));
		List<Droplet> droplets = dropletDao.getAll(null);
		for (Droplet droplet : droplets) {
			droplet.setNetworks(networkDao.findByDropletId(droplet.getId()));
		}
		return droplets;
	}

	public Droplet findById(long id) {
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(context));
		NetworkDao networkDao = new NetworkDao(DatabaseHelper.getInstance(context));
		Droplet droplet = dropletDao.findById(id);
		List<Network> networks = networkDao.findByDropletId(id);
		droplet.setNetworks(networks);
		return droplet;
	}

	public void createDroplet(String hostname, Long imageId, String regionSlug, String sizeSlug,
			boolean privateNetworking, boolean enableBackups, boolean enableIPv6, String userData, List<Long> selectedSSHKeysIds) {
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		
		String url = String.format("%s/droplets", ApiHelper.API_URL);
				
		HashMap<String,Object> options = new HashMap<>();
		options.put("name", hostname);
		options.put("region", regionSlug);
		options.put("size", sizeSlug);
		options.put("image", imageId);
        if(selectedSSHKeysIds.size() > 0) {
            List<String> keys = new ArrayList<>();
            for (Long key: selectedSSHKeysIds) {
                keys.add(key.toString());
            }
            options.put("ssh_keys", keys);
        }
		options.put("backups", enableBackups);
		options.put("ipv6", enableIPv6);
		options.put("private_networking", privateNetworking);
		if(!userData.equals("")){
			options.put("user_data", userData);
		}
		JSONObject jsonObject = new JSONObject(options);

		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
		try {
            HttpEntity entity = new cz.msebera.android.httpclient.entity.ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
			client.post(context, url, entity, "application/json", new AsyncHttpResponseHandler() {

                @Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
					if(statusCode == 401){
						ApiHelper.showAccessDenied();
					} else if(statusCode == 422){
                        try {
                            JSONObject jsonObject = new JSONObject(new String(responseBody));
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
				}
			    
			});
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        ActionService.trackActions(context);
	}

	/*public void getDropletFromAPI(long dropletId, final boolean showProgress) {
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		String url = String.format(Locale.US, "%s/droplets/%d", ApiHelper.API_URL, dropletId);
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
		client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    JSONObject dropletJSONObject = jsonObject.getJSONObject("droplet");
                    Droplet droplet = jsonObjectToDroplet(dropletJSONObject);
                    DropletService.this.update(droplet);
                    DropletService.this.setRequiresRefresh(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					ApiHelper.showAccessDenied();
				}
			}
		});
	}*/

	protected void update(Droplet droplet) {
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(context));
		dropletDao.createOrUpdate(droplet);
	}
	
	public boolean isRefreshing() {
		return isRefreshing;
	}

	public void destroyDroplet(long dropletId) {
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		
		String url = String.format(Locale.US,"%s/droplets/%d", ApiHelper.API_URL, dropletId);		
				
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
		client.delete(url, new AsyncHttpResponseHandler() {

            @Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                requiresRefresh();
            }

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					ApiHelper.showAccessDenied();
				}
			}
		    
		});	
	}

    public void startTurnedOffDroplets() {
        DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(context));
        List<Droplet> droplets = dropletDao.getAllByProperty(DropletTable.STATUS, "off");
        for (Droplet droplet : droplets) {
            executeAction(droplet.getId(), DropletActions.POWER_ON, new HashMap<String, String>());
        }
    }
}
