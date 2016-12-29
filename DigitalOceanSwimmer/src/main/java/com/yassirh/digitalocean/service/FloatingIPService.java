package com.yassirh.digitalocean.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.data.FloatingIPDao;
import com.yassirh.digitalocean.data.ImageDao;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.FloatingIP;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.model.Region;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.utils.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FloatingIPService {

    private Context context;
    private boolean isRefreshing;
    private boolean refreshing;

    public FloatingIPService(Context context) {
        this.context = context;
    }

    public FloatingIP findById(long id) {
        FloatingIPDao floatingIPDao = new FloatingIPDao(DatabaseHelper.getInstance(context));
        return floatingIPDao.findById(id);
    }

    public void getAllFromAPI(boolean showProgress) {
        Account currentAccount = ApiHelper.getCurrentAccount(context);
        if(currentAccount == null){
            return;
        }

        isRefreshing = true;
        String url = String.format("%s/floating_ips/", ApiHelper.API_URL);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", String.format("Bearer %s", currentAccount.getToken()));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    List<FloatingIP> floatingIPs = new ArrayList<>();
                    JSONArray ipsJSONArray = jsonObject.getJSONArray("floating_ips");
                    for (int i = 0; i < ipsJSONArray.length(); i++) {
                        JSONObject ipJSONObject = ipsJSONArray.getJSONObject(i);
                        FloatingIP ip = jsonObjectToFloatingIP(ipJSONObject);
                        floatingIPs.add(ip);
                    }
                    FloatingIPService.this.deleteAll();
                    FloatingIPService.this.saveAll(floatingIPs);
                    FloatingIPService.this.setRequiresRefresh(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 401){
                    ApiHelper.showAccessDenied();
                }
            }
        });
    }

    private void saveAll(List<FloatingIP> floatingIPs) {
        FloatingIPDao floatingIPDao = new FloatingIPDao(DatabaseHelper.getInstance(context));
        for (FloatingIP floatingIP : floatingIPs) {
            floatingIPDao.create(floatingIP);
        }
    }

    public void deleteAll() {
        FloatingIPDao floatingIPDao = new FloatingIPDao(DatabaseHelper.getInstance(context));
        floatingIPDao.deleteAll();
    }

    private static FloatingIP jsonObjectToFloatingIP(JSONObject ipJSONObject)
            throws JSONException {
        FloatingIP floatingIP = new FloatingIP();
        floatingIP.setIp(ipJSONObject.getString("ip"));
        Region region = RegionService.jsonObjectToRegion(ipJSONObject.getJSONObject("region"));
        floatingIP.setRegion(region);
        if(!ipJSONObject.isNull("droplet")) {
            Droplet droplet = DropletService.jsonObjectToDroplet(ipJSONObject.getJSONObject("droplet"));
            floatingIP.setDroplet(droplet);
        }
        return floatingIP;
    }

    private void setRequiresRefresh(boolean requireRefresh) {
        SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("floatingip_require_refresh", requireRefresh);
        editor.commit();
    }

    public Boolean requiresRefresh(){
        SharedPreferences settings = context.getSharedPreferences("prefrences", 0);
        return settings.getBoolean("floatingip_require_refresh", true);
    }

    public List<FloatingIP> getAll() {
        FloatingIPDao floatingIPDao = new FloatingIPDao(DatabaseHelper.getInstance(context));
        return floatingIPDao.getAll(null);
    }

    public boolean isRefreshing() {
        return refreshing;
    }
}
