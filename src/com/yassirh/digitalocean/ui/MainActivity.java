package com.yassirh.digitalocean.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.SizeTable;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.model.SSHKey;
import com.yassirh.digitalocean.service.DomainService;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.ImageService;
import com.yassirh.digitalocean.service.RegionService;
import com.yassirh.digitalocean.service.SSHKeyService;
import com.yassirh.digitalocean.service.SizeService;
import com.yassirh.digitalocean.ui.widget.MultiSelectSpinner;
import com.yassirh.digitalocean.utils.MyBroadcastReceiver;
import com.yassirh.digitalocean.utils.PreferencesHelper;

public class MainActivity extends ActionBarActivity implements Updatable {

	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavigationTitles;
    
    private DropletService mDropletService;
    private DomainService mDomainService;
    Intent intent;
    
    @SuppressLint("HandlerLeak")
	Handler mUiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            update(MainActivity.this);
        }
    };
    
    Thread t = new Thread(new Runnable() {	
		@Override
		public void run() {
			for (;;) {
				try {
					Thread.sleep(1000);
					Boolean update = false;
					if(MainActivity.this.mCurrentSelected == DrawerPositions.DROPLETS_FRAGMENT_POSITION){
						DropletService dropletService = new DropletService(MainActivity.this);
						update = dropletService.requiresRefresh();
						dropletService.setRequiresRefresh(false);
					}
					else if(MainActivity.this.mCurrentSelected == DrawerPositions.IMAGES_FRAGMENT_POSITION){
						ImageService imageService = new ImageService(MainActivity.this);
						update = imageService.requiresRefresh();
						imageService.setRequiresRefresh(false);
					}
					else if(MainActivity.this.mCurrentSelected == DrawerPositions.DOMAINS_FRAGMENT_POSITION){
						DomainService domainService = new DomainService(MainActivity.this);
						update = domainService.requiresRefresh();
						domainService.setRequiresRefresh(false);
					}
					else if(MainActivity.this.mCurrentSelected == DrawerPositions.SIZES_FRAGMENT_POSITION){
						SizeService sizeService = new SizeService(MainActivity.this);
						update = sizeService.requiresRefresh();
						sizeService.setRequiresRefresh(false);
					}
					else if(MainActivity.this.mCurrentSelected == DrawerPositions.REGIONS_FRAGMENT_POSITION){
						RegionService regionService = new RegionService(MainActivity.this);
						update = regionService.requiresRefresh();
						regionService.setRequiresRefresh(false);
					}
					else if(MainActivity.this.mCurrentSelected == DrawerPositions.SSHKEYS_FRAGMENT_POSITION){
						SSHKeyService sshKeyService = new SSHKeyService(MainActivity.this);
						update = sshKeyService.requiresRefresh();
						sshKeyService.setRequiresRefresh(false);
					}
					if(update)
						mUiHandler.sendMessage(new Message());
					
				} catch (InterruptedException e) {
				}
			}
		}
	});
	private long lastBackPressed;
    	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTitle = mDrawerTitle = getTitle();
		mNavigationTitles = getResources().getStringArray(R.array.main_navigation_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new NavigationDrawerAdapter(this, mNavigationTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
                ) {
            public void onDrawerClosed(View view) {
            	getSupportActionBar().setTitle(mTitle);
            	supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
            	getSupportActionBar().setTitle(mDrawerTitle);
            	supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
            update(this);
            t.start();
        }
        
        Intent myBroadcastReceiver = new Intent(this, MyBroadcastReceiver.class);
    	//myBroadcastReceiver.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myBroadcastReceiver, PendingIntent.FLAG_CANCEL_CURRENT);
        
        int interval = PreferencesHelper.getSynchronizationInterval(this);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        if(interval == 0)
        	alarmManager.cancel(pendingIntent);
        else
        	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), interval * 60 * 1000, pendingIntent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		t.interrupt();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		t.interrupt();
		super.onStop();
	}
	
	@Override
	public void onBackPressed() {
		if (lastBackPressed + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		}
        else {
        	Toast.makeText(getBaseContext(), R.string.message_press_again_to_exit, Toast.LENGTH_SHORT).show();
        }
        lastBackPressed = System.currentTimeMillis();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        FragmentManager fm;
        switch(item.getItemId()) {
        case R.id.action_sync:
        	if(mCurrentSelected == DrawerPositions.DROPLETS_FRAGMENT_POSITION){
        		DropletService dropletService = new DropletService(this);
        		dropletService.getAllDropletsFromAPI(true);
        	}
        	else if(mCurrentSelected == DrawerPositions.DOMAINS_FRAGMENT_POSITION){
        		DomainService domainService = new DomainService(this);
        		domainService.getAllDomainsFromAPI(true);
        	}
        	else if(mCurrentSelected == DrawerPositions.IMAGES_FRAGMENT_POSITION){
        		ImageService imageService = new ImageService(this);
        		imageService.getAllImagesFromAPI(true);	
        	}
        	else if(mCurrentSelected == DrawerPositions.REGIONS_FRAGMENT_POSITION){
        		RegionService regionService = new RegionService(this);
        		regionService.getAllRegionsFromAPI(true);	
        	}
        	else if(mCurrentSelected == DrawerPositions.SIZES_FRAGMENT_POSITION){
        		SizeService sizeService = new SizeService(this);
        		sizeService.getAllSizesFromAPI(true);
        	}    		
        	else if(mCurrentSelected == DrawerPositions.SSHKEYS_FRAGMENT_POSITION){
        		SSHKeyService sshKeysService = new SSHKeyService(this);
        		sshKeysService.getAllSSHKeysFromAPI(true);
        	}
        	return true;
        case R.id.action_add_droplet:
        	intent = new Intent(MainActivity.this, NewDropletActivity.class);
        	startActivity(intent);
        	return true;
        case R.id.action_add_domain:
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	    LayoutInflater inflater = getLayoutInflater();
        	builder = new AlertDialog.Builder(this);
    	    inflater = getLayoutInflater();
    		View view = inflater.inflate(R.layout.dialog_domain_create, null);
    		mDropletService = new DropletService(this);
    		getResources().getString(R.string.create_domain);
    		builder.setView(view);
    		
    		final EditText domainNameEditText = (EditText)view.findViewById(R.id.domainNameEditText);
    		final Spinner dropletSpinner = (Spinner)view.findViewById(R.id.dropletSpinner);
    		dropletSpinner.setAdapter(new DropletAdapter(this, mDropletService.getAllDroplets()));
    		builder.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDomainService = new DomainService(MainActivity.this);
					//mDomainService.createDomain(domainNameEditText.getText().toString(),((Droplet)dropletSpinner.getSelectedItem()).getIpAddress(),true);
				}
			});
    		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
    		builder.show().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        	return true;
        case R.id.action_add_ssh_key:
			fm = getSupportFragmentManager();
			SSHKeyCreateDialogFragment sshKeyCreateDialogFragment = new SSHKeyCreateDialogFragment();
			sshKeyCreateDialogFragment.show(fm, "create_ssh_key");
        	return true;
        case R.id.action_add_record:
			fm = getSupportFragmentManager();
			RecordCreateDialogFragment recordCreateDialogFragment = new RecordCreateDialogFragment();
			recordCreateDialogFragment.show(fm, "create_record");
        	return true;
        case R.id.action_switch_account:
        	fm = getSupportFragmentManager();
			SwitchAccountDialogFragment switchAccountDialogFragment = new SwitchAccountDialogFragment();
			switchAccountDialogFragment.show(fm, "switch_account");
        	return true;
        case R.id.action_settings:
        	intent = new Intent(this, SettingsActivity.class);
        	startActivity(intent);
        	finish();
        	return true;
        case R.id.action_about:
        	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://yassirh.com/digitalocean_swimmer/"));
			startActivity(browserIntent);
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrentSelected = position;
            selectItem(position);
        }
    }
    
    Fragment mFragment = new Fragment();
    Integer mCurrentSelected = 0;
    private void selectItem(int position) {
    	
    	if(position == DrawerPositions.DROPLETS_FRAGMENT_POSITION){
    		mFragment = new DropletsFragment();
    	}
    	else if(position == DrawerPositions.DOMAINS_FRAGMENT_POSITION){
    		mFragment = new DomainsFragment();
    	}
    	else if(position == DrawerPositions.IMAGES_FRAGMENT_POSITION){
    		mFragment = new ImagesFragment();
    	}
    	else if(position == DrawerPositions.REGIONS_FRAGMENT_POSITION){
    		mFragment = new RegionsFragment();
    	}
    	else if(position == DrawerPositions.SIZES_FRAGMENT_POSITION){
    		mFragment = new SizesFragment();
    	}
    	else if(position == DrawerPositions.SSHKEYS_FRAGMENT_POSITION){
    		mFragment = new SSHKeyFragment();
    	}
    	else if(position == DrawerPositions.SETTINGS_POSITION){
    		Intent intent = new Intent(this, SettingsActivity.class);
        	startActivity(intent);
        	finish();
    	}
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavigationTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.mTitle = title;
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
        selectItem(mCurrentSelected);
        update(this);
    }

	@Override
	public void update(Context context) {
		try {
			((Updatable)mFragment).update(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
