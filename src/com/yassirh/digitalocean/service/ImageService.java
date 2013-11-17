package com.yassirh.digitalocean.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

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
		String url = "https://api.digitalocean.com/images/?client_id=" + ApiHelper.CLIENT_ID + "&api_key=" + ApiHelper.API_KEY; 
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
			ProgressDialog mProgressDialog;
			@Override
			public void onStart() {
				if(showProgress){
					mProgressDialog = new ProgressDialog(context);
					mProgressDialog.setIndeterminate(false);
					mProgressDialog.setMax(100);
					mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					mProgressDialog.setTitle(context.getResources().getString(R.string.synchronising));
					mProgressDialog.setMessage(context.getResources().getString(R.string.synchronising_images));
					mProgressDialog.show();
					mProgressDialog.setProgress(0);
				}
			}
			
			@Override
			public void onFinish() {
				if(showProgress && mProgressDialog != null){
					mProgressDialog.dismiss();
				}
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {				
				if(mProgressDialog != null){
					mProgressDialog.setProgress((int)100*bytesWritten/totalSize);
				}
			}
			
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					List<Image> images = new ArrayList<Image>();
					Log.v("api", "status : " + status);
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
}
