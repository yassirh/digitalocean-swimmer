package com.yassirh.digitalocean.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.RecordDao;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.model.Record;
import com.yassirh.digitalocean.utils.ApiHelper;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpEntity;

public class RecordService {

	private Context context;
			
	public RecordService(Context context) {
		this.context = context;
	}	
	
	public void deleteAll() {
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(context));
		recordDao.deleteAll();
	}

	private Record jsonObjectToRecord(JSONObject recordJSONObject) throws JSONException {
		Domain domain = new Domain();
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
		record.setRecordType(recordJSONObject.getString("type"));
		record.setPriority(priority);
		record.setPort(port);
		record.setWeight(weight);
		record.setDomain(domain);
		
		return record;
	}

	protected void saveAll(List<Record> records) {
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(context));
		for (Record record : records) {
			recordDao.createOrUpdate(record);
		}
	}
	
	public List<Record> getAllRecords(){
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(context));
        return recordDao.getAll(null);
	}

	public Record findById(long id) {
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(context));
        return recordDao.findById(id);
	}


	public void getRecordsByDomainFromAPI(final String domainName) {
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		final Domain domain = new Domain();
		domain.setName(domainName);
		if(currentAccount == null){
			return;
		} 
		String url = String.format("%s/domains/%s/records", ApiHelper.API_URL, domainName);
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
		client.get(url, new AsyncHttpResponseHandler() {
						
			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					ApiHelper.showAccessDenied();
				}
			}
			
			@Override
			public void onProgress(long bytesWritten, long totalSize) {
			}

            @Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    RecordService.this.deleteAllRecordsByDomain(domainName);
                    JSONArray recordsJSONArray = jsonObject.getJSONArray("domain_records");
                    for(int i = 0; i < recordsJSONArray.length(); i++){
                        JSONObject recordJSONObject = recordsJSONArray.getJSONObject(i);
                        Record record = jsonObjectToRecord(recordJSONObject);
                        record.setDomain(domain);
                        RecordService.this.update(record);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
	}

	protected void deleteAllRecordsByDomain(String domainName) {
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(context));
		recordDao.deleteAllRecordsByDomain(domainName);
	}

	protected void update(Record record) {
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(context));
		recordDao.createOrUpdate(record);
	}

	public void createRecord(final String domainName, HashMap<String,String> params, final boolean showProgress) {
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		String url = String.format(Locale.US, "%s/domains/%s/records", ApiHelper.API_URL, domainName);
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
		JSONObject jsonObject = new JSONObject(params);
		try {
			HttpEntity entity = new cz.msebera.android.httpclient.entity.ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
			client.post(context, url, entity, "application/json", new AsyncHttpResponseHandler() {
				NotificationManager notifyManager;
				NotificationCompat.Builder builder;
				
				@Override
				public void onStart() {
					if(showProgress){
						notifyManager =
						        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						builder = new NotificationCompat.Builder(context);
						builder.setContentTitle(context.getResources().getString(R.string.creating_record))
						    .setContentText("")
						    .setSmallIcon(R.drawable.ic_launcher);
						builder.setContentIntent(PendingIntent.getActivity(context,0,new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));
						notifyManager.notify(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN_RECORD, builder.build());
					}
				}

                @Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    RecordService.this.getRecordsByDomainFromAPI(domainName);
                }

                @Override
				public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
					if(statusCode == 401){
						ApiHelper.showAccessDenied();
					}
				}
			    
			    @Override
				public void onProgress(long bytesWritten, long totalSize) {
					if(showProgress){
						builder.setProgress(100, (int) (100*bytesWritten/totalSize), false);
						notifyManager.notify(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN_RECORD, builder.build());
					}
				}
			    
			    @Override
			    public void onFinish() {
			    	if(showProgress)
						notifyManager.cancel(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN_RECORD);
			    }
			});
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
	}

	public void updateRecord(final String domainName, HashMap<String,String> params, long recordId, final boolean showProgress) {
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		String url = String.format(Locale.US, "%s/domains/%s/records/%d", ApiHelper.API_URL, domainName, recordId);
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
		JSONObject jsonObject = new JSONObject(params);
		try {
			HttpEntity entity = new cz.msebera.android.httpclient.entity.ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
			client.put(context, url, entity, "application/json", new AsyncHttpResponseHandler() {
				NotificationManager notifyManager;
				NotificationCompat.Builder builder;
				
				@Override
				public void onStart() {
					if(showProgress){
						notifyManager =
						        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						builder = new NotificationCompat.Builder(context);
						builder.setContentTitle(context.getResources().getString(R.string.creating_record))
						    .setContentText("")
						    .setSmallIcon(R.drawable.ic_launcher);
						builder.setContentIntent(PendingIntent.getActivity(context,0,new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));
						notifyManager.notify(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN_RECORD, builder.build());
					}
				}

                @Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    RecordService.this.getRecordsByDomainFromAPI(domainName);
                }

                @Override
				public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
					if(statusCode == 401){
						ApiHelper.showAccessDenied();
					}
				}
			    
			    @Override
				public void onProgress(long bytesWritten, long totalSize) {
					if(showProgress){
						builder.setProgress(100, (int) (100*bytesWritten/totalSize), false);
						notifyManager.notify(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN_RECORD, builder.build());
					}
				}
			    
			    @Override
			    public void onFinish() {
			    	if(showProgress)
						notifyManager.cancel(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN_RECORD);
			    }
			});
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public void deleteDomainRecord(final String domainName, final long recordId, final boolean showProgress) {
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		String url = String.format(Locale.US, "%s/domains/%s/records/%d", ApiHelper.API_URL, domainName, recordId);
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
		client.delete(url, new AsyncHttpResponseHandler() {
			NotificationManager notifyManager;
			NotificationCompat.Builder builder;
			
			@Override
			public void onStart() {
				if(showProgress){
					notifyManager =
					        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					builder = new NotificationCompat.Builder(context);
					builder.setContentTitle(context.getResources().getString(R.string.destroying_record))
					    .setContentText("")
					    .setSmallIcon(R.drawable.ic_launcher);

					notifyManager.notify(NotificationsIndexes.NOTIFICATION_DESTROY_RECORD, builder.build());
				}
			}

            @Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

            }

            @Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					ApiHelper.showAccessDenied();
				}
			}
		    
		    @Override
			public void onProgress(long bytesWritten, long totalSize) {
				if(showProgress){
					builder.setProgress(100, (int) (100*bytesWritten/totalSize), false);
					notifyManager.notify(NotificationsIndexes.NOTIFICATION_DESTROY_RECORD, builder.build());
				}
			}
		    
		    @Override
		    public void onFinish() {
		    	if(showProgress)
					notifyManager.cancel(NotificationsIndexes.NOTIFICATION_DESTROY_RECORD);
		    	RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(context));
		    	recordDao.delete(recordId);
		    }
		});
	}
}
