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
import com.yassirh.digitalocean.data.DomainDao;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.utils.ApiHelper;

public class DomainService {

	private Context context;
		
	public DomainService(Context context) {
		this.context = context;
	}

	public void getAllDomainFromAPI(final boolean showProgress){
		String url = "https://api.digitalocean.com/domains/?client_id=" + ApiHelper.CLIENT_ID + "&api_key=" + ApiHelper.API_KEY; 
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
					List<Domain> domains = new ArrayList<Domain>();
					Log.v("api", "status : " + status);
					if(ApiHelper.API_STATUS_OK.equals(status)){
						JSONArray domainJSONArray = jsonObject.getJSONArray("domains");
						for(int i = 0; i < domainJSONArray.length(); i++){
							JSONObject domainJSONObject = domainJSONArray.getJSONObject(i);
							Domain domain = new Domain();
							domain.setId(domainJSONObject.getLong("id"));
							domain.setName(domainJSONObject.getString("name"));
							domain.setTtl(domainJSONObject.getInt("ttl"));
							domain.setLiveZoneFile(domainJSONObject.getString("live_zone_file"));
							domain.setError(!domainJSONObject.getString("error").equals("null") ? domainJSONObject.getString("error") : "" );
							domain.setZoneFileWithError(!domainJSONObject.getString("zone_file_with_error").equals("null") ? domainJSONObject.getString("zone_file_with_error") : "");
							domains.add(domain);
						}
						DomainService.this.saveAll(domains);
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

	protected void saveAll(List<Domain> domains) {
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		DomainDao domainDao = new DomainDao(databaseHelper);
		for (Domain domain : domains) {
			domainDao.create(domain);
		}
		databaseHelper.close();
	}
	
	public List<Domain> getAllDomains(){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		DomainDao domainDao = new DomainDao(databaseHelper);
		List<Domain> domains = domainDao.getAll(null);
		databaseHelper.close();
		return domains;
	}	
}
