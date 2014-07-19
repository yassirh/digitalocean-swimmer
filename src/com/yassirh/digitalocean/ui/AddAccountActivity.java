package com.yassirh.digitalocean.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.utils.ApiHelper;

public class AddAccountActivity extends Activity {

	EditText accountNameEditText;
	EditText tokenEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_account);
		accountNameEditText = (EditText)findViewById(R.id.accountNameEditText);
		tokenEditText = (EditText)findViewById(R.id.tokenEditText);
		accountNameEditText.setText("Default");
		
		Uri uri = getIntent().getData();
		if(uri != null && uri.toString().startsWith("callback://com.yassirh.digitalocean")){
			tokenEditText.setText(uri.getQueryParameter("code"));
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if(uri != null && uri.toString().startsWith("callback://com.yassirh.digitalocean")){
			tokenEditText.setText(uri.getQueryParameter("code"));
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
			account.setSelected(true);
			ApiHelper.selectAccount(this,account);
			startActivity(new Intent(this, MainActivity.class));
    		finish();
    		return true;
    	}
    	return false;
    }
}
