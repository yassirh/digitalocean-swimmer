package com.yassirh.digitalocean.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.service.DomainService;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.ImageService;
import com.yassirh.digitalocean.service.RegionService;
import com.yassirh.digitalocean.service.SSHKeyService;
import com.yassirh.digitalocean.service.SizeService;
import com.yassirh.digitalocean.utils.ApiHelper;
import com.yassirh.digitalocean.utils.MyApplication;

public class SettingsActivity extends ActionBarActivity{

	static SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = 
			new OnSharedPreferenceChangeListener() {
		
				@Override
				public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
						String key) {
					ApiHelper.getCurrentAccount(MyApplication.getAppContext());
					// Clear all the previously stored data and get the new account data.
					if(key.equals("api_key_preference") || key.equals("client_id_preference") || key.equals("account_name_preference")){
						ImageService imageService = new ImageService(MyApplication.getAppContext());
						imageService.deleteAll();
						imageService.getAllImagesFromAPI(true);
						
						RegionService regionService = new RegionService(MyApplication.getAppContext());
						regionService.deleteAll();
						regionService.getAllRegionsFromAPI(true);
						
						SizeService sizeService = new SizeService(MyApplication.getAppContext());
						sizeService.deleteAll();
						sizeService.getAllSizesFromAPI(true);
						
						DomainService domainService = new DomainService(MyApplication.getAppContext());
						domainService.deleteAll();
						domainService.getAllDomainsFromAPI(true);
						
						DropletService dropletService = new DropletService(MyApplication.getAppContext());
						dropletService.deleteAll();
						dropletService.getAllDropletsFromAPI(true);
						
						SSHKeyService sshKeyService = new SSHKeyService(MyApplication.getAppContext());
						sshKeyService.deleteAll();
						sshKeyService.getAllSSHKeysFromAPI(true);
					}
				}
			};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportFragmentManager().beginTransaction()
        	.replace(android.R.id.content, new PrefsFragment()).commit();
	}	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
	        case android.R.id.home: 
	            onBackPressed();
	            break;
	
	        default:
	            return super.onOptionsItemSelected(item);
        }
        return true;
    }
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this,MainActivity.class));
		finish();
	}
		
	public static class PrefsFragment extends PreferenceFragment {
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			
		}
		
		@Override
		public void onResume() {
		    super.onResume();
		    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

		}

		@Override
		public void onPause() {
		    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
		    super.onPause();
		}
	}

}
