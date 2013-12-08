package com.yassirh.digitalocean.service;

import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.DropletDao;
import com.yassirh.digitalocean.data.RecordDao;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.model.Droplet;
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
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(mContext));
		dropletDao.deleteAll();
	}

	private Record jsonObjectToRecord(JSONObject recordJSONObject) throws JSONException {
		Record record = new Record();
		Domain domain = new Domain();
		domain.setId(recordJSONObject.getLong("domain_id"));
				
		record.setId(recordJSONObject.getLong("id"));
		record.setName(recordJSONObject.getString("name"));
		record.setData(recordJSONObject.getString("data"));
		record.setDomain(domain);
		record.setRecordType(recordJSONObject.getString("record_type"));
		
		return record;
	}

	protected void saveAll(List<Droplet> droplets) {
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(mContext));
		for (Droplet droplet : droplets) {
			dropletDao.createOrUpdate(droplet);
		}
	}
	
	public List<Record> getAllRecords(){
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(mContext));
		List<Record> records = recordDao.getAll(null);
		return records;
	}

	public Droplet findById(long id) {
		DropletDao dropletDao = new DropletDao(DatabaseHelper.getInstance(mContext));
		Droplet droplet = dropletDao.findById(id);
		return droplet;
	}


	public void getRecordsByDomainFromAPI(long domainId, final boolean showProgress) {
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    }			
		});
	}

	protected void update(Record record) {
		RecordDao recordDao = new RecordDao(DatabaseHelper.getInstance(mContext));
		recordDao.createOrUpdate(record);
	}	
}
