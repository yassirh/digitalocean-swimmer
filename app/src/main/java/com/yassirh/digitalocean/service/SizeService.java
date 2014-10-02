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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.SizeDao;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.utils.ApiHelper;

public class SizeService {

	private Context mContext;
	private boolean mIsRefreshing;
	
	public SizeService(Context context) {
		mContext = context;
	}

	public void getAllSizesFromAPI(final boolean showProgress){
		Account currentAccount = ApiHelper.getCurrentAccount(mContext);
		if(currentAccount == null){
			return;
		}			
		mIsRefreshing = true;
		String url = String.format("%s/sizes/", ApiHelper.API_URL);//"https://api.digitalocean.com/sizes/?client_id=" + currentAccount.getClientId() + "&api_key=" + currentAccount.getApiKey(); 
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
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
					    .setContentText(mContext.getResources().getString(R.string.synchronising_sizes))
					    .setSmallIcon(R.drawable.ic_launcher);
					mBuilder.setContentIntent(PendingIntent.getActivity(mContext,0,new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_SIZES, mBuilder.build());
				}
			}
			
			@Override
			public void onFinish() {
				mIsRefreshing = false;
				if(showProgress){
					mNotifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_SIZES);
				}
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				if(showProgress){
					mBuilder.setProgress(100, (int)100*bytesWritten/totalSize, false);
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_SIZES, mBuilder.build());
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					ApiHelper.showAccessDenied();
				}
			}
			
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					List<Size> sizes = new ArrayList<Size>();
					JSONArray sizeJSONArray = jsonObject.getJSONArray("sizes");
					for(int i = 0; i < sizeJSONArray.length(); i++){
						JSONObject sizeJSONObject = sizeJSONArray.getJSONObject(i);
						Size size = jsonObjectToSize(sizeJSONObject);
						sizes.add(size);
					}
					SizeService.this.deleteAll();
					SizeService.this.saveAll(sizes);
					SizeService.this.setRequiresRefresh(true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    }
		});
	}

	public static Size jsonObjectToSize(JSONObject sizeJSONObject)
			throws JSONException {
		Size size = new Size();
		size.setSlug(sizeJSONObject.getString("slug"));
		if(sizeJSONObject.has("memory")){
			size.setMemory(sizeJSONObject.getInt("memory"));
		}
		if(sizeJSONObject.has("vcpus")){
			size.setCpu(sizeJSONObject.getInt("vcpus"));
		}
		if(sizeJSONObject.has("disk")){
			size.setDisk(sizeJSONObject.getInt("disk"));
		}
		size.setTransfer(sizeJSONObject.getInt("transfer"));
		size.setCostPerHour(sizeJSONObject.getDouble("price_hourly"));
		size.setCostPerMonth(sizeJSONObject.getDouble("price_monthly"));
		return size;
	}
	
	protected void saveAll(List<Size> sizes) {
		SizeDao sizeDao = new SizeDao(DatabaseHelper.getInstance(mContext));
		for (Size size : sizes) {
			sizeDao.create(size);
		}
	}
	
	public List<Size> getAllSizes(String orderBy){
		SizeDao sizeDao = new SizeDao(DatabaseHelper.getInstance(mContext));
        return sizeDao.getAll(orderBy);
	}

	public void deleteAll() {
		SizeDao sizeDao = new SizeDao(DatabaseHelper.getInstance(mContext));
		sizeDao.deleteAll();
	}

	public void setRequiresRefresh(Boolean requireRefresh){
		SharedPreferences settings = mContext.getSharedPreferences("prefrences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("size_require_refresh", requireRefresh);
		editor.commit();
	}
	public Boolean requiresRefresh(){
		SharedPreferences settings = mContext.getSharedPreferences("prefrences", 0);
		return settings.getBoolean("size_require_refresh", true);
	}

	public boolean isRefreshing() {
		return mIsRefreshing;
	}
}
