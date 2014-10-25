package com.yassirh.digitalocean.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.utils.ApiHelper;

import java.util.Calendar;

public class AddAccountActivity extends ActionBarActivity {

	EditText accountNameEditText;
	EditText tokenEditText;
	Calendar expiresIn = Calendar.getInstance();
	String refreshToken = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
		accountNameEditText = (EditText)findViewById(R.id.accountNameEditText);
		tokenEditText = (EditText)findViewById(R.id.tokenEditText);
		try {
			Uri uri = getIntent().getData();
			if(uri != null && uri.toString().startsWith("callback://com.yassirh.digitalocean")){
				tokenEditText.setText(uri.getQueryParameter("code"));
				accountNameEditText.setText(uri.getQueryParameter("account_name"));
				refreshToken = uri.getQueryParameter("refresh_token");
				expiresIn.add(Calendar.SECOND, Integer.parseInt(uri.getQueryParameter("expires_in")));
			}
		} catch (Exception ignored) {
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if(uri != null && uri.toString().startsWith("callback://com.yassirh.digitalocean")){
			tokenEditText.setText(uri.getQueryParameter("code"));
			accountNameEditText.setText(uri.getQueryParameter("account_name"));
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_account, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == R.id.action_save_account){
    		Account account = new Account();
			account.setName(accountNameEditText.getText().toString());
			account.setToken(tokenEditText.getText().toString());
			account.setExpiresIn(expiresIn.getTime());
			account.setRefreshToken(refreshToken);
			account.setSelected(true);
			ApiHelper.selectAccount(this,account);
			startActivity(new Intent(this, MainActivity.class));
    		finish();
    		return true;
    	}
        else if(item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return false;
    }
}
