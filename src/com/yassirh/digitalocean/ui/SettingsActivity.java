package com.yassirh.digitalocean.ui;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.utils.ApiHelper;
import com.yassirh.digitalocean.utils.MyApplication;
import com.yassirh.digitalocean.utils.PreferencesHelper;

public class SettingsActivity extends ActionBarActivity{

	SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = 
			new OnSharedPreferenceChangeListener() {
		
				@Override
				public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
						String key) {
					Account currentAccount = ApiHelper.getCurrentAccount(MyApplication.getAppContext());
					if(currentAccount == null){
						currentAccount = new Account();
					}
					// Clear all the previously stored data and get the new account data.
					if(key.equals("token_preference") || key.equals("account_name_preference")){
						Context context = MyApplication.getAppContext();
						currentAccount.setToken(ApiHelper.getToken(context));
						currentAccount.setName(ApiHelper.getAccountName(context));
						if(currentAccount.getName().equals("")){
							currentAccount.setName("default");
						}
						ApiHelper.selectAccount(context, currentAccount);
					}					
					if(key.equals("pref_locale")){
						Context context = MyApplication.getAppContext();
						Locale locale = PreferencesHelper.getLocal(context);
				        Locale.setDefault(locale);
				        Configuration config = new Configuration();
				        config.locale = locale;
				        context.getResources().updateConfiguration(config, null);
						Intent intent = new Intent(MyApplication.getAppContext(),SettingsActivity.class);
						SettingsActivity.this.startActivity(intent);
						SettingsActivity.this.finish();
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
		
	public class PrefsFragment extends PreferenceFragment {
		
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
