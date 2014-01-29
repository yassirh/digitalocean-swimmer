package com.yassirh.digitalocean.service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.RecordDao;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.model.Record;
import com.yassirh.digitalocean.utils.ApiHelper;

public class RecordService {

	private Context mContext;
			
	public RecordService(Context context) {
		mContext = context;
	}
	
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	public void deleteAll() {
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(mContext));
		recordDao.deleteAll();
	}

	private Record jsonObjectToRecord(JSONObject recordJSONObject) throws JSONException {
		Domain domain = new Domain();
		domain.setId(recordJSONObject.getLong("domain_id"));
		Integer priority = null;
	    Integer port = null;
	    Integer weight = null;
	    if(!recordJSONObject.getString("priority").equals("null")){
	    	priority = Integer.parseInt(recordJSONObject.getString("priority"));
	    }
		if(!recordJSONObject.getString("port").equals("null")){
			port = Integer.parseInt(recordJSONObject.getString("port"));    	
	    }
		if(!recordJSONObject.getString("weight").equals("null")){
			weight = Integer.parseInt(recordJSONObject.getString("weight"));
		}
	    Record record = new Record();
		record.setId(recordJSONObject.getLong("id"));
		record.setName(recordJSONObject.getString("name"));
		record.setData(recordJSONObject.getString("data"));
		record.setRecordType(recordJSONObject.getString("record_type"));
		record.setPriority(priority);
		record.setPort(port);
		record.setWeight(weight);
		record.setDomain(domain);
		
		return record;
	}

	protected void saveAll(List<Record> records) {
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(mContext));
		for (Record record : records) {
			recordDao.createOrUpdate(record);
		}
	}
	
	public List<Record> getAllRecords(){
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(mContext));
		List<Record> records = recordDao.getAll(null);
		return records;
	}

	public Record findById(long id) {
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(mContext));
		Record record = recordDao.findById(id);
		return record;
	}


	public void getRecordsByDomainFromAPI(final long domainId, final boolean showProgress) {
		String url = "https://api.digitalocean.com/domains/" + domainId + "/records?client_id=" + ApiHelper.getClientId(mContext) + "&api_key=" + ApiHelper.getAPIKey(mContext); 
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
			
			@Override
			public void onStart() {				
			}
			
			@Override
			public void onFinish() {
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					Toast.makeText(mContext, R.string.access_denied_message, Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
			}
			
		    @Override
		    public void onSuccess(String response) { 
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if(ApiHelper.API_STATUS_OK.equals(status)){
						RecordService.this.deleteAllRecordsByDomain(domainId);
						JSONArray recordsJSONArray = jsonObject.getJSONArray("records");
						for(int i = 0; i < recordsJSONArray.length(); i++){
							JSONObject recordJSONObject = recordsJSONArray.getJSONObject(i);
							Record record = jsonObjectToRecord(recordJSONObject);							
							RecordService.this.update(record);
						}
					}
					else{
						// TODO handle error Access Denied/Not Found
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}  
		    }			
		});
	}

	protected void deleteAllRecordsByDomain(long domainId) {
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(mContext));
		recordDao.deleteAllRecordsByDomain(domainId);
	}

	protected void update(Record record) {
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(mContext));
		recordDao.createOrUpdate(record);
	}
	
	public void createRecord(final long domainId, HashMap<String,String> params, final boolean showProgress) {
		String url = "https://api.digitalocean.com/domains/" + domainId + "/records/new?client_id=" + ApiHelper.getClientId(mContext) + "&api_key=" + ApiHelper.getAPIKey(mContext);
		Iterator<Entry<String, String>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> pairs = it.next();
			url += "&" + pairs.getKey() + "=" + pairs.getValue();
		}

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
					mBuilder.setContentTitle(mContext.getResources().getString(R.string.creating_record))
					    .setContentText("")
					    .setSmallIcon(R.drawable.ic_launcher);
					mBuilder.setContentIntent(PendingIntent.getActivity(mContext,0,new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN, mBuilder.build());
				}
			}
			
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if(ApiHelper.API_STATUS_OK.equals(status)){
				    	RecordService.this.getRecordsByDomainFromAPI(domainId, false);
					}
					else{
						// TODO handle error Access Denied/Not Found
						Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}  
		    }
		    
		    @Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					Toast.makeText(mContext, R.string.access_denied_message, Toast.LENGTH_SHORT).show();
				}
			}
		    
		    @Override
			public void onProgress(int bytesWritten, int totalSize) {	
				if(showProgress){
					mBuilder.setProgress(100, (int)100*bytesWritten/totalSize, false);
					mNotifyManager.notify(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN, mBuilder.build());
				}
			}
		    
		    @Override
		    public void onFinish() {
		    	if(showProgress)
					mNotifyManager.cancel(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN);
		    }
		});
	}
}
