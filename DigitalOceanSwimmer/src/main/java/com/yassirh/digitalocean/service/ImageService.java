package com.yassirh.digitalocean.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.ImageDao;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.utils.ApiHelper;

public class ImageService {

	private Context context;
	private boolean isRefreshing;
	
	public ImageService(Context context) {
		this.context = context;
	}
	
	public void getAllImagesFromAPI(final boolean showProgress){
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		isRefreshing = true;
		String url = String.format(Locale.US ,"%s/images?per_page=%d", ApiHelper.API_URL, Integer.MAX_VALUE);
		AsyncHttpClient client = new AsyncHttpClient();
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
					    .setContentText(context.getResources().getString(R.string.synchronising_images))
					    .setSmallIcon(R.drawable.ic_launcher);
					builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
					notifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_IMAGES, builder.build());
				}
			}
			
			@Override
			public void onFinish() {
				isRefreshing = false;
				if(showProgress){
					notifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_IMAGES);
				}
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				if(showProgress){
					builder.setProgress(100, 100 * bytesWritten / totalSize, false);
					notifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_IMAGES, builder.build());
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					ApiHelper.showAccessDenied();
				}
			}

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    List<Image> images = new ArrayList<Image>();
                    JSONArray imageJSONArray = jsonObject.getJSONArray("images");
                    for(int i = 0; i < imageJSONArray.length(); i++){
                        JSONObject imageJSONObject = imageJSONArray.getJSONObject(i);
                        Image image = jsonObjectToImage(imageJSONObject);
                        image.setInUse(true);
                        images.add(image);
                    }
                    ImageService.this.deleteAll();
                    ImageService.this.saveAll(images);
                    ImageService.this.setRequiresRefresh(true);
                } catch (JSONException e) {
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
        image.setInUse(true);
		image.setPublic(imageJSONObject.getBoolean("public"));
        String regions = "";
        for (int i = 0; i < imageJSONObject.getJSONArray("regions").length(); i++) {
            regions += ";" + imageJSONObject.getJSONArray("regions").getString(i);
        }
        image.setRegions(regions.replaceFirst(";", ""));
		return image;
	}
	
	protected void saveAll(List<Image> images) {
		ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(context));
		for (Image image : images) {
			imageDao.create(image);
		}
	}

    public Image findImageById(Long id){
        ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(context));
        return imageDao.findById(id);
    }
	
	/*public List<Image> getAllImages(){
		ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(context));
        return imageDao.getAll(null);
	}*/

	public void deleteAll() {
		ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(context));
		imageDao.deleteAll();	
	}

	public void setRequiresRefresh(Boolean requireRefresh){
		SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("image_require_refresh", requireRefresh);
		editor.commit();
	}

    public void transferImage(long imageId, String regionSlug){
        Account currentAccount = ApiHelper.getCurrentAccount(context);
        if(currentAccount == null){
            return;
        }
        String url = String.format(Locale.US,"%s/images/%d/actions", ApiHelper.API_URL, imageId);

        HashMap<String,Object> options = new HashMap<String, Object>();
        options.put("type", "transfer");
        options.put("region", regionSlug);

        JSONObject jsonObject = new JSONObject(options);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
        ByteArrayEntity entity;
        try {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            client.post(context, url, entity, "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(statusCode == 401){
                        ApiHelper.showAccessDenied();
                    }
                }
            });
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ActionService.trackActions(context);
    }

    public void updateImage(long imageId, String name){
        Account currentAccount = ApiHelper.getCurrentAccount(context);
        if(currentAccount == null){
            return;
        }
        String url = String.format(Locale.US,"%s/images/%d", ApiHelper.API_URL, imageId);

        HashMap<String,Object> options = new HashMap<String, Object>();
        options.put("name", name);

        JSONObject jsonObject = new JSONObject(options);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
        ByteArrayEntity entity;
        try {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            client.put(context, url, entity, "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(statusCode == 401){
                        ApiHelper.showAccessDenied();
                    }
                }
            });
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ActionService.trackActions(context);
    }
	
	public Boolean requiresRefresh(){
		SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
		return settings.getBoolean("image_require_refresh", true);
	}

	public List<Image> getSnapshotsOnly() {
		ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(context));
        return imageDao.getSnapshotsOnly();
	}
	
	public List<Image> getImagesOnly() {
		ImageDao imageDao = new ImageDao(DatabaseHelper.getInstance(context));
        return imageDao.getImagesOnly();
	}

	public boolean isRefreshing() {
		return isRefreshing;
	}

    public void destroySnapshot(long imageId) {
        Account currentAccount = ApiHelper.getCurrentAccount(context);
        if(currentAccount == null){
            return;
        }
        String url = String.format(Locale.US,"%s/images/%d", ApiHelper.API_URL, imageId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
        try {
            client.delete(context, url, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(statusCode == 401){
                        ApiHelper.showAccessDenied();
                    }
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
        ActionService.trackActions(context);
    }
}
