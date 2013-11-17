package com.yassirh.digitalocean.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

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

	private Context context;
	
	public enum DropletActions{
		REBOOT, POWER_CYCLE, SHUTDOWN, POWER_OFF, POWER_ON,
		PASSWORD_RESET, RESIZE, SNAPSHOT, RESTORE, REBUILD, 
		ENABLE_BACKUPS, DISABLE_BACKUPS, RENAME, DESTROY
	}
		
	public DropletService(Context context) {
		this.context = context;
	}

	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	
	
	public void ExecuteAction(long dropletId,DropletActions dropletAction){
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
		default:
			break;
		}
		String url = getActionUrl(dropletId,action); 
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					Log.v("api", "status : " + status);
					if(ApiHelper.API_STATUS_OK.equals(status)){
						Long eventId = jsonObject.getLong("event_id");
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
	
	private String getActionUrl(long dropletId,String action) {
		return "https://api.digitalocean.com/droplets/" + dropletId + "/" + action + "/?client_id=" + ApiHelper.CLIENT_ID + "&api_key=" + ApiHelper.API_KEY;
	}

	public void getAllDropletsFromAPI(final boolean showProgress){
		String url = "https://api.digitalocean.com/droplets/?client_id=" + ApiHelper.CLIENT_ID + "&api_key=" + ApiHelper.API_KEY; 
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
			ProgressDialog mProgressDialog;
			@Override
			public void onStart() {
				if(showProgress){
					mProgressDialog = new ProgressDialog(context);
					mProgressDialog.setMax(100);
					mProgressDialog.setIndeterminate(false);
					mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					mProgressDialog.setTitle(context.getResources().getString(R.string.synchronising));
					mProgressDialog.setMessage(context.getResources().getString(R.string.synchronising_images));
					mProgressDialog.show();
					mProgressDialog.setProgress(0);
				}
			}
			@Override
			public void onFinish() {
				if(showProgress && mProgressDialog != null)
					mProgressDialog.dismiss();
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				if(mProgressDialog != null)
					mProgressDialog.setProgress((int)100*bytesWritten/totalSize);
			}
			
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					List<Droplet> droplets = new ArrayList<Droplet>();
					Log.v("api", "status : " + status);
					if(ApiHelper.API_STATUS_OK.equals(status)){
						JSONArray dropletsJSONArray = jsonObject.getJSONArray("droplets");
						for(int i = 0; i < dropletsJSONArray.length(); i++){
							JSONObject dropletJSONObject = dropletsJSONArray.getJSONObject(i);
							
							Image image = new Image();
							image.setId(dropletJSONObject.getLong("image_id"));
							Region region = new Region();
							region.setId(dropletJSONObject.getLong("region_id"));
							Size size = new Size();
							size.setId(dropletJSONObject.getLong("size_id"));
							
							Droplet droplet = new Droplet();
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
							
							
							droplets.add(droplet);
						}
						DropletService.this.saveAll(droplets);
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

	protected void saveAll(List<Droplet> droplets) {
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		DropletDao dropletDao = new DropletDao(databaseHelper);
		for (Droplet droplet : droplets) {
			dropletDao.create(droplet);
		}
		databaseHelper.close();
	}
	
	public List<Droplet> getAllDroplets(){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		DropletDao dropletDao = new DropletDao(databaseHelper);
		List<Droplet> droplets = dropletDao.getAll(null);
		databaseHelper.close();
		return droplets;
	}

	public Droplet findById(long id) {
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		DropletDao dropletDao = new DropletDao(databaseHelper);
		Droplet droplet = dropletDao.findById(id);
		databaseHelper.close();
		return droplet;
	}	
}
