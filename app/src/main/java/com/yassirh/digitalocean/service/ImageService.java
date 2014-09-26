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
import com.yassirh.digitalocean.data.ImageDao;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.utils.ApiHelper;

public class ImageService {

	private Context mContext;
	private boolean mIsRefreshing;
	
	public ImageService(Context context) {
		this.mContext = context;
	}
	
	public void getAllImagesFromAPI(final boolean showProgress){
		Account currentAccount = ApiHelper.getCurrentAccount(mContext);
		if(currentAccount == null){
			return;
		}
		mIsRefreshing = true;
		//String url = "https://api.digitalocean.com/images/?client_id=" + currentAccount.getClientId() + "&api_key=" + currentAccount.getApiKey(); 
		String url = String.format("%s/images?per_page=%d", ApiHelper.API_URL, Integer.MAX_VALUE);
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
					    .setContentText(mContext.getResources().getString(R.string.synchronising_images))
					    .setSmallIcon(R.drawable.ic_launcher);
					mBuilder.setContentIntent(PendingIntent.getActivity(mContext,0,new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_IMAGES, mBuilder.build());
				}
			}
			
			@Override
			public void onFinish() {
				mIsRefreshing = false;
				if(showProgress){
					mNotifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_IMAGES);
				}
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				if(showProgress){
					mBuilder.setProgress(100, (int)100*bytesWritten/totalSize, false);
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_IMAGES, mBuilder.build());
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
					List<Image> images = new ArrayList<Image>();
					JSONArray imageJSONArray = jsonObject.getJSONArray("images");
					for(int i = 0; i < imageJSONArray.length(); i++){
						JSONObject imageJSONObject = imageJSONArray.getJSONObject(i);
						Image image = jsonObjectToImage(imageJSONObject);
						images.add(image);
					}
					ImageService.this.deleteAll();
					ImageService.this.saveAll(images);
					ImageService.this.setRequiresRefresh(true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    }
		});
	}


	public static Image jsonObjectToImage(JSONObject imageJSONObject)
			throws JSONException {
		Image image = new Image();
		image.setId(imageJSONObject.getLong("id"));
		image.setName(imageJSONObject.getString("name"));
		image.setDistribution(imageJSONObject.getString("distribution"));
		if(imageJSONObject.getString("slug").equals("null"))
			image.setSlug("");
		else
			image.setSlug(imageJSONObject.getString("slug"));
		image.setPublic(imageJSONObject.getBoolean("public"));
		return image;
	}
	
	protected void saveAll(List<Image> images) {
		ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(mContext));
		for (Image image : images) {
			imageDao.create(image);
		}
	}
	
	public List<Image> getAllImages(){
		ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(mContext));
		List<Image> images = imageDao.getAll(null);
		return images;
	}

	public void deleteAll() {
		ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(mContext));
		imageDao.deleteAll();	
	}

	public void setRequiresRefresh(Boolean requireRefresh){
		SharedPreferences settings = mContext.getSharedPreferences("prefrences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("image_require_refresh", requireRefresh);
		editor.commit();
	}
	
	public Boolean requiresRefresh(){
		SharedPreferences settings = mContext.getSharedPreferences("prefrences", 0);
		return settings.getBoolean("image_require_refresh", true);
	}

	public List<Image> getSnapshotsOnly() {
		ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(mContext));
		List<Image> images = imageDao.getSnapshotsOnly();
		return images;
	}
	
	public List<Image> getImagesOnly() {
		ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(mContext));
		List<Image> images = imageDao.getImagesOnly();
		return images;
	}

	public boolean isRefreshing() {
		return mIsRefreshing;
	}
}
