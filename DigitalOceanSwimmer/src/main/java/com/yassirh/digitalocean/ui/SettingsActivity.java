package com.yassirh.digitalocean.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class SettingsActivity extends ActionBarActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        PrefsFragment prefsFragment = new PrefsFragment();
        prefsFragment.setActivity(this);
		getSupportFragmentManager().beginTransaction()
        	.replace(android.R.id.content, prefsFragment).commit();
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
