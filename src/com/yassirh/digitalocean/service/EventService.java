package com.yassirh.digitalocean.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.Event;
import com.yassirh.digitalocean.utils.ApiHelper;

public class EventService {

	private Context mContext;
	
	public EventService(Context context) {
		this.mContext = context;
	}

	public void trackEvent(final long eventId,final String name,final String message){
		final String url ="https://api.digitalocean.com/events/" + eventId + "/?client_id=" + ApiHelper.getClientId(mContext) + "&api_key=" + ApiHelper.getAPIKey(mContext); 
		Thread t = new Thread(new Runnable() {
			Event mEvent;
			NotificationManager mNotifyManager;
			NotificationCompat.Builder mBuilder;
			
			boolean sleep = false;
			boolean done = false;
			Object sleepLock = new Object();
			
			@Override
			public void run() {
				mNotifyManager =
				        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
				mBuilder = new NotificationCompat.Builder(mContext);
				mBuilder.setContentTitle(name)
				    .setContentText(message)
				    .setSmallIcon(R.drawable.ic_launcher);
				mBuilder.setContentIntent(PendingIntent.getActivity(mContext,0,new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));
				mNotifyManager.notify((int)eventId, mBuilder.build());
				AsyncHttpClient client = new AsyncHttpClient();
				while(!done){
					if(sleep){
						try {
							Thread.sleep(2000);
							sleep = false;
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					else{
						client.get(url, new AsyncHttpResponseHandler() {
												
							@Override
							public void onStart() {
								synchronized (sleepLock) {
									sleep = true;	
								}								
							}
							@Override
							public void onFinish() {
								synchronized (sleepLock) {
									sleep = false;	
								}
							}
							@Override
							public void onSuccess(String response) {
								try {
									JSONObject jsonObject = new JSONObject(response);
									String status = jsonObject.getString("status");
									mEvent = new Event();
									if(ApiHelper.API_STATUS_OK.equals(status)){
										JSONObject eventJsonObject = jsonObject.getJSONObject("event");
										Droplet mDroplet = new Droplet();
										mDroplet.setId(eventJsonObject.getLong("droplet_id"));
										mEvent.setActionStatus(eventJsonObject.getString("action_status"));
										mEvent.setId(eventJsonObject.getLong("id"));
										mEvent.setPercentage(eventJsonObject.getString("percentage").equals("null") ? 100 : eventJsonObject.getInt("percentage"));
										mEvent.setDroplet(mDroplet);
										mBuilder.setProgress(100, mEvent.getPercentage(), false);
										mNotifyManager.notify((int)eventId, mBuilder.build());
										if(mEvent.getPercentage() == 100){
											done = true;
											mNotifyManager.cancel((int)eventId);
										}
									}
									else{
										done = true;
										mNotifyManager.cancel((int)eventId);
									}
								} catch (JSONException e) {
									e.printStackTrace();
									done = true;
									mNotifyManager.cancel((int)eventId);
								}
							}
						});
					}
				}
				if(mEvent != null){
					DropletService dropletService = new DropletService(mContext);
					dropletService.getDropletFromAPI(mEvent.getDroplet().getId(),false);
				}
				mNotifyManager.cancel((int)eventId);
			}
		});
		t.start();
	}
	
}
