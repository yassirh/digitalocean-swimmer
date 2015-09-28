package com.yassirh.digitalocean.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.RegionDao;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.model.Region;
import com.yassirh.digitalocean.utils.ApiHelper;

public class RegionService {

	private Context context;
	private boolean isRefreshing;
		
	public RegionService(Context context) {
		this.context = context;
	}

	public void getAllRegionsFromAPI(final boolean showProgress){
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		isRefreshing = true;
		String url = String.format("%s/regions/", ApiHelper.API_URL);//String url = "https://api.digitalocean.com/regions/?client_id=" + currentAccount.getClientId() + "&api_key=" + currentAccount.getApiKey(); 
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
		client.get(url, new AsyncHttpResponseHandler() {
			NotificationManager mNotifyManager;
			NotificationCompat.Builder mBuilder;
			
			@Override
			public void onStart() {
				if(showProgress){
					mNotifyManager =
					        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					mBuilder = new NotificationCompat.Builder(context);
					mBuilder.setContentTitle(context.getResources().getString(R.string.synchronising))
					    .setContentText(context.getResources().getString(R.string.synchronising_regions))
					    .setSmallIcon(R.drawable.ic_launcher);
					mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
					Notification note = mBuilder.build();
					note.flags |= Notification.FLAG_ONGOING_EVENT;
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_REGIONS, note);
				}
			}
			
			@Override
			public void onFinish() {
				isRefreshing = false;
				if(showProgress){
					mNotifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_REGIONS);
				}
			}
			
			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					ApiHelper.showAccessDenied();
				}
			}
			
			@Override
			public void onProgress(long bytesWritten, long totalSize) {
				if(showProgress){
					mBuilder.setProgress(100, (int) (100*bytesWritten/totalSize), false);
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_REGIONS, mBuilder.build());
				}
			}

            @Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    List<Region> regions = new ArrayList<>();
                    JSONArray regionJSONArray = jsonObject.getJSONArray("regions");
                    for(int i = 0; i < regionJSONArray.length(); i++){
                        JSONObject regionJSONObject = regionJSONArray.getJSONObject(i);
                        Region region = jsonObjectToRegion(regionJSONObject);
                        regions.add(region);
                    }
                    RegionService.this.deleteAll();
                    RegionService.this.saveAll(regions);
                    RegionService.this.setRequiresRefresh(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
	}

	public static Region jsonObjectToRegion(JSONObject regionJSONObject)
			throws JSONException {
		Region region = new Region();
		region.setName(regionJSONObject.getString("name"));
		region.setSlug(regionJSONObject.getString("slug"));
        try {
            region.setAvailable(regionJSONObject.getBoolean("available"));
        }catch (JSONException ignored){
        }
		String features = "";
		for (int i = 0; i < regionJSONObject.getJSONArray("features").length(); i++) {
			features += ";" + regionJSONObject.getJSONArray("features").getString(i);
		}
		region.setFeatures(features.replaceFirst(";", ""));
		return region;
	}
	
	public void deleteAll() {
		RegionDao regionDao = new RegionDao(DatabaseHelper.getInstance(context));
		regionDao.deleteAll();
	}

	protected void saveAll(List<Region> regions) {
		RegionDao regionDao = new RegionDao(DatabaseHelper.getInstance(context));
		for (Region region : regions) {
			regionDao.create(region);
		}
	}
	
	public List<Region> getAllRegions(){
		RegionDao regionDao = new RegionDao(DatabaseHelper.getInstance(context));
        return regionDao.getAll(null);
	}

	public List<Region> getAllRegionsOrderedByName(){
		RegionDao regionDao = new RegionDao(DatabaseHelper.getInstance(context));
        return regionDao.getAllOrderedByName();
	}
	
	public void setRequiresRefresh(Boolean requireRefresh){
		SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("region_require_refresh", requireRefresh);
		editor.commit();
	}
	
	public Boolean requiresRefresh(){
		SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
		return settings.getBoolean("region_require_refresh", true);
	}

	public boolean isRefreshing() {
		return isRefreshing;
	}
}
