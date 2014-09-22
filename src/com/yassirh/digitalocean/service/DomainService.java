package com.yassirh.digitalocean.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.yassirh.digitalocean.data.DomainDao;
import com.yassirh.digitalocean.data.DomainTable;
import com.yassirh.digitalocean.data.RecordDao;
import com.yassirh.digitalocean.data.RecordTable;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.utils.ApiHelper;

public class DomainService {

	private Context context;
	private boolean isRefreshing;
		
	public DomainService(Context context) {
		this.context = context;
	}

	public void getAllDomainsFromAPI(final boolean showProgress){
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		isRefreshing = true;
		String url = String.format(Locale.US,"%s/domains?per_page=%d", ApiHelper.API_URL, Integer.MAX_VALUE);
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
					    .setContentText(context.getResources().getString(R.string.synchronising_domains))
					    .setSmallIcon(R.drawable.ic_launcher);
					builder.setContentIntent(PendingIntent.getActivity(context,0,new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));
					notifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_DOMAINS, builder.build());
				}
			}
			
			@Override
			public void onFinish() {
				isRefreshing = false;
				if(showProgress){
					notifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_DOMAINS);
				}
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				if(showProgress){
					builder.setProgress(100, (int)100*bytesWritten/totalSize, false);
					notifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_DOMAINS, builder.build());
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
					List<Domain> domains = new ArrayList<Domain>();
					JSONArray domainJSONArray = jsonObject.getJSONArray("domains");
					for(int i = 0; i < domainJSONArray.length(); i++){
						JSONObject domainJSONObject = domainJSONArray.getJSONObject(i);
						Domain domain = new Domain();
						domain.setName(domainJSONObject.getString("name"));
						domain.setTtl(domainJSONObject.getInt("ttl"));
						domain.setLiveZoneFile(domainJSONObject.getString("zone_file"));
						domains.add(domain);
					}
					DomainService.this.deleteAll();
					DomainService.this.saveAll(domains);
					for (Domain domain : domains) {
						new RecordService(context).getRecordsByDomainFromAPI(domain.getName(),false);
					}
					DomainService.this.setRequiresRefresh(true);
				} catch (JSONException e) {
					e.printStackTrace();
				}  
		    }
		});
	}

	protected void saveAll(List<Domain> domains) {
		DomainDao domainDao = new DomainDao(DatabaseHelper.getInstance(context));
		for (Domain domain : domains) {
			domainDao.create(domain);
		}
	}
	
	public void deleteAll() {
		DomainDao domainDao = new DomainDao(DatabaseHelper.getInstance(context));
		domainDao.deleteAll();
	}
	
	public List<Domain> getAllDomains(){
		DomainDao domainDao = new DomainDao(DatabaseHelper.getInstance(context));
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(context));
		List<Domain> domains = domainDao.getAll(null);
		for (Domain domain : domains) {
			domain.setRecords(recordDao.getAllByDomain(domain.getName()));
		}
		return domains;
	}

	public void setRequiresRefresh(Boolean requireRefresh){
		SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("domain_require_refresh", requireRefresh);
		editor.commit();
	}
	public Boolean requiresRefresh(){
		SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
		return settings.getBoolean("domain_require_refresh", true);
	}

	// TODO : show progress
	public void createDomain(String domainName, String ipAddress, final boolean showProgress) {
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		String url = "";//"https://api.digitalocean.com/domains/new?client_id=" + currentAccount.getClientId() + "&api_key=" + currentAccount.getApiKey() + "&name=" + domainName + "&ip_address=" + ipAddress;
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
					builder.setContentTitle(context.getResources().getString(R.string.creating_domain))
					    .setContentText("")
					    .setSmallIcon(R.drawable.ic_launcher);

					notifyManager.notify(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN, builder.build());
				}
			}
			
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if(ApiHelper.API_STATUS_OK.equals(status)){
						
					}
					else{
						// TODO handle error Access Denied/Not Found
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    }
		    
		    @Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					ApiHelper.showAccessDenied();
				}
			}
		    
		    @Override
			public void onProgress(int bytesWritten, int totalSize) {	
				if(showProgress){
					builder.setProgress(100, (int)100*bytesWritten/totalSize, false);
					notifyManager.notify(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN, builder.build());
				}
			}
		    
		    @Override
		    public void onFinish() {
		    	if(showProgress)
					notifyManager.cancel(NotificationsIndexes.NOTIFICATION_CREATE_DOMAIN);
		    	DomainService.this.getAllDomainsFromAPI(false);
		    }
		});
	}
	
	public Domain findByDomainName(String domainName) {
		DomainDao domainDao = new DomainDao(DatabaseHelper.getInstance(context));
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(context));
		Domain domain = domainDao.findByProperty(DomainTable.NAME, domainName);
		if(domain != null)
			domain.setRecords(recordDao.getAllByProperty(RecordTable.DOMAIN_NAME, domain.getName()));		
		return domain;
	}

	public void deleteDomain(final long id, final boolean showProgress) {
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		String url = "";//String url = "https://api.digitalocean.com/domains/"  + id + "/destroy/" + "?client_id=" + currentAccount.getClientId() + "&api_key=" + currentAccount.getApiKey();
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
			NotificationManager notifyManager;
			NotificationCompat.Builder builder;
			
			@Override
			public void onStart() {
				if(showProgress){
					notifyManager =
					        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					builder = new NotificationCompat.Builder(context);
					builder.setContentTitle(context.getResources().getString(R.string.destroying_domain))
					    .setContentText("")
					    .setSmallIcon(R.drawable.ic_launcher);

					notifyManager.notify(NotificationsIndexes.NOTIFICATION_DESTROY_DOMAIN, builder.build());
				}
			}
			
		    @Override
		    public void onSuccess(String response) {
		        try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if(ApiHelper.API_STATUS_OK.equals(status)){
						
					}
					else{
						// TODO handle error Access Denied/Not Found
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    }
		    
		    @Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					ApiHelper.showAccessDenied();
				}
			}
		    
		    @Override
			public void onProgress(int bytesWritten, int totalSize) {	
				if(showProgress){
					builder.setProgress(100, (int)100*bytesWritten/totalSize, false);
					notifyManager.notify(NotificationsIndexes.NOTIFICATION_DESTROY_DOMAIN, builder.build());
				}
			}
		    
		    @Override
		    public void onFinish() {
		    	if(showProgress)
					notifyManager.cancel(NotificationsIndexes.NOTIFICATION_DESTROY_DOMAIN);
		    	DomainDao domainDao = new DomainDao(DatabaseHelper.getInstance(context));
		    	domainDao.delete(id);
		    	DomainService.this.setRequiresRefresh(true);
		    }
		});
	}

	public boolean isRefreshing() {
		return isRefreshing;
	}
}
