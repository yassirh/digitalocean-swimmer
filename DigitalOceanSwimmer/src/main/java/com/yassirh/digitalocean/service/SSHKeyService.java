package com.yassirh.digitalocean.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
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
import com.yassirh.digitalocean.data.SSHKeyDao;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.model.SSHKey;
import com.yassirh.digitalocean.utils.ApiHelper;

import cz.msebera.android.httpclient.HttpEntity;

public class SSHKeyService {

	private Context context;
	private boolean isRefreshing;
		
	public SSHKeyService(Context context) {
		this.context = context;
	}

	public void getAllSSHKeysFromAPI(final boolean showProgress){
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		isRefreshing = true;
		String url = String.format("%s/account/keys/", ApiHelper.API_URL);
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
					    .setContentText(context.getResources().getString(R.string.synchronising_keys))
					    .setSmallIcon(R.drawable.ic_launcher);
					builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
                    Notification note = builder.build();
                    note.flags |= Notification.FLAG_ONGOING_EVENT;
					notifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_KEYS, note);
				}
			}
			
			@Override
			public void onFinish() {
				isRefreshing = false;
				if(showProgress){
					notifyManager.cancel(NotificationsIndexes.NOTIFICATION_GET_ALL_KEYS);
				}
			}
			
			@Override
            public void onProgress(long bytesWritten, long totalSize) {
				if(showProgress){
					builder.setProgress(100, (int) (100 * bytesWritten / totalSize), false);
					notifyManager.notify(NotificationsIndexes.NOTIFICATION_GET_ALL_KEYS, builder.build());
				}
			}
			
			@Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
				if(statusCode == 401){
					ApiHelper.showAccessDenied();
				}
			}

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    List<SSHKey> sshKeys = new ArrayList<>();
                    JSONArray sshKeysJSONArray = jsonObject.getJSONArray("ssh_keys");
                    for(int i = 0; i < sshKeysJSONArray.length(); i++){
                        JSONObject sshKeysJSONObject = sshKeysJSONArray.getJSONObject(i);
                        SSHKey sshKey = new SSHKey();
                        sshKey.setId(sshKeysJSONObject.getLong("id"));
                        sshKey.setName(sshKeysJSONObject.getString("name"));
                        sshKey.setFingerprint(sshKeysJSONObject.getString("fingerprint"));
                        sshKey.setPublicKey(sshKeysJSONObject.getString("public_key"));
                        sshKeys.add(sshKey);
                    }
                    SSHKeyService.this.deleteAll();
                    SSHKeyService.this.saveAll(sshKeys);
                    SSHKeyService.this.setRequiresRefresh(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


		});
	}
	
	protected void saveAll(List<SSHKey> sshKeys) {
		SSHKeyDao sshKeyDao = new SSHKeyDao(DatabaseHelper.getInstance(context));
		for (SSHKey sshKey : sshKeys) {
			sshKeyDao.create(sshKey);
		}
	}
	
	public void deleteAll() {
		SSHKeyDao sshKeyDao = new SSHKeyDao(DatabaseHelper.getInstance(context));
		sshKeyDao.deleteAll();
	}
	
	public List<SSHKey> getAllSSHKeys(){
		SSHKeyDao sshKeyDao = new SSHKeyDao(DatabaseHelper.getInstance(context));
		return sshKeyDao.getAll(null);
	}

	public void setRequiresRefresh(Boolean requireRefresh){
		SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("key_require_refresh", requireRefresh);
		editor.commit();
	}
	public Boolean requiresRefresh(){
		SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
		return settings.getBoolean("key_require_refresh", true);
	}
	
	public SSHKey findById(long id) {
		SSHKeyDao sshKeyDao = new SSHKeyDao(DatabaseHelper.getInstance(context));
		return sshKeyDao.findById(id);
	}

	public void delete(final long id) {
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		String url = String.format(Locale.US, "%s/account/keys/%d", ApiHelper.API_URL, id);
		AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
		client.delete(url, new AsyncHttpResponseHandler() {

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
		    public void onFinish() {
		    	SSHKeyDao sshKeyDao = new SSHKeyDao(DatabaseHelper.getInstance(context));
		    	sshKeyDao.delete(id);
		    	SSHKeyService.this.setRequiresRefresh(true);
		    }
		});
	}

    public void create(SSHKey sshKey) {
        Account currentAccount = ApiHelper.getCurrentAccount(context);
        if(currentAccount == null){
            return;
        }
        String url = String.format("%s/account/keys", ApiHelper.API_URL);
        HashMap<String,Object> options = new HashMap<>();
        options.put("name", sshKey.getName());
        options.put("public_key", sshKey.getPublicKey());
        JSONObject jsonObject = new JSONObject(options);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
        try {
            HttpEntity entity = new cz.msebera.android.httpclient.entity.ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            client.post(context, url, entity, "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 401) {
                        ApiHelper.showAccessDenied();
                    }
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(responseBody));
                        JSONObject sshKeysJSONObject = jsonObject.getJSONObject("ssh_key");
                        SSHKey sshKey = new SSHKey();
                        sshKey.setId(sshKeysJSONObject.getLong("id"));
                        sshKey.setName(sshKeysJSONObject.getString("name"));
                        sshKey.setFingerprint(sshKeysJSONObject.getString("fingerprint"));
                        sshKey.setPublicKey(sshKeysJSONObject.getString("public_key"));
                        SSHKeyDao sshKeyDao = new SSHKeyDao(DatabaseHelper.getInstance(context));
                        sshKeyDao.create(sshKey);
                        SSHKeyService.this.setRequiresRefresh(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void update(SSHKey sshKey) {
        Account currentAccount = ApiHelper.getCurrentAccount(context);
        if(currentAccount == null){
            return;
        }
        String url = String.format(Locale.US, "%s/account/keys/%d", ApiHelper.API_URL, sshKey.getId());
        HashMap<String,Object> options = new HashMap<>();
        options.put("name", sshKey.getName());
        JSONObject jsonObject = new JSONObject(options);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
        try {
            HttpEntity entity = new cz.msebera.android.httpclient.entity.ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            client.put(context, url, entity, "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 401) {
                        ApiHelper.showAccessDenied();
                    }
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(responseBody));
                        JSONObject sshKeysJSONObject = jsonObject.getJSONObject("ssh_key");
                        SSHKey sshKey = new SSHKey();
                        sshKey.setId(sshKeysJSONObject.getLong("id"));
                        sshKey.setName(sshKeysJSONObject.getString("name"));
                        sshKey.setFingerprint(sshKeysJSONObject.getString("fingerprint"));
                        sshKey.setPublicKey(sshKeysJSONObject.getString("public_key"));
                        SSHKeyDao sshKeyDao = new SSHKeyDao(DatabaseHelper.getInstance(context));
                        sshKeyDao.delete(sshKey.getId());
                        sshKeyDao.create(sshKey);
                        SSHKeyService.this.setRequiresRefresh(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

	public boolean isRefreshing() {
		return isRefreshing;
	}
}
