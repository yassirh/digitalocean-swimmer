package com.yassirh.digitalocean.ui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.yassirh.digitalocean.R;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getFragmentManager().beginTransaction()
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
		
	public static class PrefsFragment extends PreferenceFragment {
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			//Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
		}
	}

}
