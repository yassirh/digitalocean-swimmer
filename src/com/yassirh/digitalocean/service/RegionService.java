package com.yassirh.digitalocean.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.yassirh.digitalocean.data.RegionDao;
import com.yassirh.digitalocean.model.Region;
import com.yassirh.digitalocean.utils.ApiHelper;

public class RegionService {

	private Context mContext;
		
	public RegionService(Context context) {
		mContext = context;
	}

	public void getAllRegionsFromAPI(final boolean showProgress){
		String url = "https://api.digitalocean.com/regions/?client_id=" + ApiHelper.getClientId(mContext) + "&api_key=" + ApiHelper.getAPIKey(mContext); 
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
					    .setContentText(mContext.getResources().getString(R.string.synchronising_regions))
					    .setSmallIcon(R.drawable.ic_launcher);
					mBuilder.setContentIntent(PendingIntent.getActivity(mContext,0,new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_REGIONS, mBuilder.build());
				}
			}
			
			@Override
			public void onFinish() {
				if(showProgress)
					mNotifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_REGIONS);
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
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_REGIONS, mBuilder.build());
				}
			}
			
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					List<Region> regions = new ArrayList<Region>();
					if(ApiHelper.API_STATUS_OK.equals(status)){
						JSONArray regionJSONArray = jsonObject.getJSONArray("regions");
						for(int i = 0; i < regionJSONArray.length(); i++){
							JSONObject regionJSONObject = regionJSONArray.getJSONObject(i);
							Region region = new Region();
							region.setId(regionJSONObject.getLong("id"));
							region.setName(regionJSONObject.getString("name"));
							if(regionJSONObject.getString("slug").equals("null"))
								region.setSlug("");
							else
								region.setSlug(regionJSONObject.getString("slug"));
							regions.add(region);
						}
						RegionService.this.deleteAll();
						RegionService.this.saveAll(regions);
						RegionService.this.setRequiresRefresh(true);
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

	public void deleteAll() {
		RegionDao regionDao = new RegionDao(DatabaseHelper.getInstance(mContext));
		regionDao.deleteAll();
	}

	protected void saveAll(List<Region> regions) {
		RegionDao regionDao = new RegionDao(DatabaseHelper.getInstance(mContext));
		for (Region region : regions) {
			regionDao.create(region);
		}
	}
	
	public List<Region> getAllRegions(){
		RegionDao regionDao = new RegionDao(DatabaseHelper.getInstance(mContext));
		List<Region> regions = regionDao.getAll(null);
		return regions;
	}

	public List<Region> getAllRegionsOrderedByName(){
		RegionDao regionDao = new RegionDao(DatabaseHelper.getInstance(mContext));
		List<Region> regions = regionDao.getAllOrderedByName();
		return regions;
	}
	
	public void setRequiresRefresh(Boolean requireRefresh){
		SharedPreferences settings = mContext.getSharedPreferences("prefrences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("region_require_refresh", requireRefresh);
		editor.commit();
	}
	
	public Boolean requiresRefresh(){
		SharedPreferences settings = mContext.getSharedPreferences("prefrences", 0);
		return settings.getBoolean("region_require_refresh", true);
	}
}
