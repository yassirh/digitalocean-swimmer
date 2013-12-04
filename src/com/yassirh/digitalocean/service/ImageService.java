package com.yassirh.digitalocean.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.ImageDao;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.utils.ApiHelper;

public class ImageService {

	private Context context;
		
	public ImageService(Context context) {
		this.context = context;
	}
	
	public void getAllImagesFromAPI(final boolean showProgress){
		String url = "https://api.digitalocean.com/images/?client_id=" + ApiHelper.getClientId(context)+ "&api_key=" + ApiHelper.getAPIKey(context); 
		AsyncHttpClient client = new AsyncHttpClient();
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
					    .setContentText(context.getResources().getString(R.string.synchronising_images))
					    .setSmallIcon(R.drawable.ic_launcher);

					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_IMAGES, mBuilder.build());
				}
			}
			
			@Override
			public void onFinish() {
				if(showProgress)
					mNotifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_IMAGES);
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
					Toast.makeText(context, R.string.access_denied_message, Toast.LENGTH_SHORT).show();
				}
			}
			
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					List<Image> images = new ArrayList<Image>();
					if(ApiHelper.API_STATUS_OK.equals(status)){
						JSONArray imageJSONArray = jsonObject.getJSONArray("images");
						for(int i = 0; i < imageJSONArray.length(); i++){
							JSONObject imageJSONObject = imageJSONArray.getJSONObject(i);
							Image image = new Image();
							image.setId(imageJSONObject.getLong("id"));
							image.setName(imageJSONObject.getString("name"));
							image.setDistribution(imageJSONObject.getString("distribution"));
							if(imageJSONObject.getString("slug").equals("null"))
								image.setSlug("");
							else
								image.setSlug(imageJSONObject.getString("slug"));
							image.setPublic(imageJSONObject.getBoolean("public"));
							images.add(image);
						}
						ImageService.this.deleteAll();
						ImageService.this.saveAll(images);
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

	protected void saveAll(List<Image> images) {
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		ImageDao imageDao = new ImageDao(databaseHelper);
		for (Image image : images) {
			imageDao.create(image);
		}
		databaseHelper.close();
	}
	
	public List<Image> getAllImages(){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		ImageDao imageDao = new ImageDao(databaseHelper);
		List<Image> images = imageDao.getAll(null);
		databaseHelper.close();
		return images;
	}

	public void deleteAll() {
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		ImageDao imageDao = new ImageDao(databaseHelper);
		imageDao.deleteAll();
		databaseHelper.close();		
	}	
}
