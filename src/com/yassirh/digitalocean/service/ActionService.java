package com.yassirh.digitalocean.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.DropletDao;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.Action;
import com.yassirh.digitalocean.utils.ApiHelper;

public class ActionService {

	private Context context;
	private Thread t;
	private static SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	public ActionService(Context context) {
		this.context = context;
	}

	public void trackEvents(){
		final Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null || t != null){
			return;
		}
		final String url = String.format("%s/actions", ApiHelper.API_URL); 
		t = new Thread(new Runnable() {
			NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);;
			NotificationCompat.Builder builder;
			
			boolean sleep = false;
			Object sleepLock = new Object();
			@Override
			public void run() {
				for (;;) {
					try {
						AsyncHttpClient client = new AsyncHttpClient();
						client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
						client.get(url, new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(String response) {
								try {
									DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(context));
									JSONObject jsonObject = new JSONObject(response);
									JSONArray actionsJSONArray = jsonObject.getJSONArray("actions");
									for(int i = 0; i < actionsJSONArray.length(); i++){
										JSONObject actionJSONObject = actionsJSONArray.getJSONObject(i);
										Action action = jsonObjectToAction(actionJSONObject);
										if(action.getStatus().equals("in-progress")){
											builder = new NotificationCompat.Builder(context);
											if(action.getResourceType().equals("droplet") && (dropletDao.findById(action.getResourceId())) != null){
												builder.setContentTitle(dropletDao.findById(action.getResourceId()).getName())
												.setContentText(action.getType() + " - In progress")
												.setSmallIcon(R.drawable.ic_launcher);	
											}
											
											builder.setContentIntent(PendingIntent.getActivity(context,0,new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));
											notifyManager.notify((int)action.getId(), builder.build());
										}
										else{
											notifyManager.cancel((int)action.getId());
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
	}
	
	

	private Action jsonObjectToAction(JSONObject actionJSONObject) {
		Action action = new Action();
		try {
			action.setId(actionJSONObject.getLong("id"));
			action.setStatus(actionJSONObject.getString("status"));
			action.setType(actionJSONObject.getString("type"));
			action.setResourceId(actionJSONObject.getLong("resource_id"));
			action.setResourceType(actionJSONObject.getString("resource_type"));
			action.setRegion(actionJSONObject.getString("region"));
			if(!actionJSONObject.getString("started_at").equals("null")){
				action.setStartedAt(iso8601Format.parse(actionJSONObject.getString("started_at").replace("Z", "")));
			}
			if(!actionJSONObject.getString("completed_at").equals("null")){
				action.setCompletedAt(iso8601Format.parse(actionJSONObject.getString("completed_at").replace("Z", "")));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return action;
	};
	
}
