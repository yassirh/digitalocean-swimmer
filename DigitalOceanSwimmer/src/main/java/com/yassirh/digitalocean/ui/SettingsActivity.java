package com.yassirh.digitalocean.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.yassirh.digitalocean.R;

public class SettingsActivity extends ActionBarActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        PrefsFragment prefsFragment = new PrefsFragment();
        prefsFragment.setActivity(this);
		getSupportFragmentManager().beginTransaction()
        	.replace(R.id.content_frame, prefsFragment).commit();
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

}
