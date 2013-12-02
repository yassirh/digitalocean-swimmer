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
import com.yassirh.digitalocean.data.DomainDao;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.utils.ApiHelper;

public class DomainService {

	private Context context;
		
	public DomainService(Context context) {
		this.context = context;
	}

	public void getAllDomainFromAPI(final boolean showProgress){
		String url = "https://api.digitalocean.com/domains/?client_id=" + ApiHelper.getClientId(context) + "&api_key=" + ApiHelper.getAPIKey(context);
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
					    .setContentText(context.getResources().getString(R.string.synchronising_domains))
					    .setSmallIcon(R.drawable.ic_launcher);

					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_DOMAINS, mBuilder.build());
				}
			}
			
			@Override
			public void onFinish() {
				mNotifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_DOMAINS);
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {	
				mBuilder.setProgress(100, (int)100*bytesWritten/totalSize, false);
				mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_DOMAINS, mBuilder.build());
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
					List<Domain> domains = new ArrayList<Domain>();
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
						DomainService.this.deleteAll();
						DomainService.this.saveAll(domains);
					}
				} catch (JSONException e) {
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
	
	protected void deleteAll() {
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		DomainDao domainDao = new DomainDao(databaseHelper);
		domainDao.deleteAll();
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
