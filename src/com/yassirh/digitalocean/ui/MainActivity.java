package com.yassirh.digitalocean.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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
        }		
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
        		domainService.getAllDomainFromAPI(true);
        	}
        	else if(mCurrentSelected == DrawerPositions.IMAGES_FRAGMENT_POSITION){
        		ImageService imageService = new ImageService(this);
        		imageService.getAllImagesFromAPI(true);	
        	}
        	else if(mCurrentSelected == DrawerPositions.REGIONS_FRAGMENT_POSITION){
        		RegionService regionService = new RegionService(this);
        		regionService.getAllRegionFromAPI(true);	
        	}
        	else{
        		SizeService sizeService = new SizeService(this);
        		sizeService.getAllSizeFromAPI(true);
        	}    		
        	return true;
        case R.id.action_add_droplet:
        	CreateDropletDialogFragment createDropletDialogFragment = new CreateDropletDialogFragment();
        	createDropletDialogFragment.show(getSupportFragmentManager(), "create_droplet_dialog");
        	return true;
        case R.id.action_add_domain:
        	AlertDialog.Builder alertDialog = new Builder(this);
        	alertDialog.setMessage(R.string.not_yet_implemented);
        	alertDialog.show();
        	return true;
        case R.id.action_settings:
        	Intent intent = new Intent(this, SettingsActivity.class);
        	startActivity(intent);
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
    }

	@Override
	public void update() {
		try {
			((Updatable)mFragment).update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
