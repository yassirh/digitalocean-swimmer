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
import com.yassirh.digitalocean.data.SizeDao;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.utils.ApiHelper;

public class SizeService {

	private Context context;
		
	public SizeService(Context context) {
		this.context = context;
	}

	public void getAllSizeFromAPI(final boolean showProgress){
		String url = "https://api.digitalocean.com/sizes/?client_id=" + ApiHelper.CLIENT_ID + "&api_key=" + ApiHelper.API_KEY; 
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
					List<Size> sizes = new ArrayList<Size>();
					Log.v("api", "status : " + status);
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
						SizeService.this.saveAll(sizes);
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
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SizeDao sizeDao = new SizeDao(databaseHelper);
		for (Size size : sizes) {
			sizeDao.create(size);
		}
		databaseHelper.close();
	}
	
	public List<Size> getAllSizes(String orderBy){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SizeDao sizeDao = new SizeDao(databaseHelper);
		List<Size> sizes = sizeDao.getAll(orderBy);
		databaseHelper.close();
		return sizes;
	}	
}
