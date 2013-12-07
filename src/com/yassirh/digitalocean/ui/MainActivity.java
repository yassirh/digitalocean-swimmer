package com.yassirh.digitalocean.ui;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.service.DomainService;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.ImageService;
import com.yassirh.digitalocean.service.RegionService;
import com.yassirh.digitalocean.service.SizeService;

public class MainActivity extends FragmentActivity implements Updatable {

	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavigationTitles;
    
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
					Thread.sleep(5000);
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
					if(update)
						mUiHandler.sendMessage(new Message());
					
				} catch (InterruptedException e) {
				}
			}
		}
	});
    	
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

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
            update(this);
            t.start();
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /*boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);*/
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
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
        	else{
        		SizeService sizeService = new SizeService(this);
        		sizeService.getAllSizesFromAPI(true);
        	}    		
        	return true;
        case R.id.action_add_droplet:
        	CreateDropletDialogFragment createDropletDialogFragment = new CreateDropletDialogFragment();
        	createDropletDialogFragment.show(getSupportFragmentManager(), "create_droplet_dialog");
        	return true;
        case R.id.action_add_domain:
        	CreateDomainDialogFragment createDomainDialogFragment = new CreateDomainDialogFragment();
        	createDomainDialogFragment.show(getSupportFragmentManager(), "create_domain_dialog");
        	return true;
        case R.id.action_settings:
        	Intent intent = new Intent(this, SettingsActivity.class);
        	startActivity(intent);
        	finish();
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
    
    public static class DrawerPositions{
    	public static final Integer DROPLETS_FRAGMENT_POSITION = 0;
    	public static final Integer DOMAINS_FRAGMENT_POSITION = 1;
    	public static final Integer IMAGES_FRAGMENT_POSITION = 2;
    	public static final Integer REGIONS_FRAGMENT_POSITION = 3;
    	public static final Integer SIZES_FRAGMENT_POSITION = 4;
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
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavigationTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.mTitle = title;
        getActionBar().setTitle(title);
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
