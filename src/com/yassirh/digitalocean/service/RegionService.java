package com.yassirh.digitalocean.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.RegionDao;
import com.yassirh.digitalocean.model.Region;
import com.yassirh.digitalocean.utils.ApiHelper;

public class RegionService {

	private Context context;
		
	public RegionService(Context context) {
		this.context = context;
	}

	public void getAllRegionFromAPI(final boolean showProgress){
		String url = "https://api.digitalocean.com/regions/?client_id=" + ApiHelper.CLIENT_ID + "&api_key=" + ApiHelper.API_KEY; 
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
				if(showProgress && mProgressDialog != null){
					mProgressDialog.dismiss();
				}
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
					List<Region> regions = new ArrayList<Region>();
					Log.v("api", "status : " + status);
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
						RegionService.this.saveAll(regions);
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

	protected void saveAll(List<Region> regions) {
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		RegionDao regionDao = new RegionDao(databaseHelper);
		for (Region region : regions) {
			regionDao.create(region);
		}
		databaseHelper.close();
	}
	
	public List<Region> getAllRegions(){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		RegionDao regionDao = new RegionDao(databaseHelper);
		List<Region> regions = regionDao.getAll(null);
		databaseHelper.close();
		return regions;
	}	
}
