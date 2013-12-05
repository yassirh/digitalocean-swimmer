package com.yassirh.digitalocean.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.SizeDao;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.utils.ApiHelper;

public class SizeService {

	private Context mContext;
		
	public SizeService(Context context) {
		mContext = context;
	}

	public void getAllSizesFromAPI(final boolean showProgress){
		String url = "https://api.digitalocean.com/sizes/?client_id=" + ApiHelper.getClientId(mContext) + "&api_key=" + ApiHelper.getAPIKey(mContext); 
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
					    .setContentText(mContext.getResources().getString(R.string.synchronising_sizes))
					    .setSmallIcon(R.drawable.ic_launcher);

					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_SIZES, mBuilder.build());
				}
			}
			
			@Override
			public void onFinish() {
				mNotifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_SIZES);
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {	
				mBuilder.setProgress(100, (int)100*bytesWritten/totalSize, false);
				mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_SIZES, mBuilder.build());
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					Toast.makeText(mContext, R.string.access_denied_message, Toast.LENGTH_SHORT).show();
				}
			}
			
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					List<Size> sizes = new ArrayList<Size>();
					if(ApiHelper.API_STATUS_OK.equals(status)){
						JSONArray sizeJSONArray = jsonObject.getJSONArray("sizes");
						for(int i = 0; i < sizeJSONArray.length(); i++){
							JSONObject regionJSONObject = sizeJSONArray.getJSONObject(i);
							Size size = new Size();
							size.setId(regionJSONObject.getLong("id"));
							size.setName(regionJSONObject.getString("name"));
							if(regionJSONObject.getString("slug").equals("null"))
								size.setSlug("");
							else
								size.setSlug(regionJSONObject.getString("slug"));
							size.setMemory(regionJSONObject.getInt("memory"));
							size.setCpu(regionJSONObject.getInt("cpu"));
							size.setDisk(regionJSONObject.getInt("disk"));
							size.setCostPerHour(regionJSONObject.getDouble("cost_per_hour"));
							size.setCostPerMonth(regionJSONObject.getDouble("cost_per_month"));
							sizes.add(size);
						}
						SizeService.this.deleteAll();
						SizeService.this.saveAll(sizes);
						SizeService.this.setRequiresRefresh(true);
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

	protected void saveAll(List<Size> sizes) {
		DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
		SizeDao sizeDao = new SizeDao(databaseHelper);
		for (Size size : sizes) {
			sizeDao.create(size);
		}
		databaseHelper.close();
	}
	
	public List<Size> getAllSizes(String orderBy){
		DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
		SizeDao sizeDao = new SizeDao(databaseHelper);
		List<Size> sizes = sizeDao.getAll(orderBy);
		databaseHelper.close();
		return sizes;
	}

	public void deleteAll() {
		DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
		SizeDao sizeDao = new SizeDao(databaseHelper);
		sizeDao.deleteAll();
		databaseHelper.close();
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
}
