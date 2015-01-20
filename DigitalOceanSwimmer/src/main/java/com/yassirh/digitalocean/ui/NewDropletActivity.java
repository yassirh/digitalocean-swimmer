package com.yassirh.digitalocean.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.SizeTable;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.model.Region;
import com.yassirh.digitalocean.model.SSHKey;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.ImageService;
import com.yassirh.digitalocean.service.RegionService;
import com.yassirh.digitalocean.service.SSHKeyService;
import com.yassirh.digitalocean.service.SizeService;
import com.yassirh.digitalocean.ui.widget.MultiSelectSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class NewDropletActivity extends ActionBarActivity implements OnItemSelectedListener, OnCheckedChangeListener {
	
	private DropletService dropletService;
	private SizeService sizeService;
    private CheckBox privateNetworkingCheckBox;
    private CheckBox enableBackupsCheckBox;
    private CheckBox userDataCheckBox;
    private EditText userDataEditText;
    private CheckBox ipv6CheckBox;
    private Spinner regionSpinner;
    private Spinner sizeSpinner;
    private Spinner imageSpinner;
    private EditText hostnameEditText;
    private MultiSelectSpinner sshKeysMultiSelectSpinner;
    private RegionAdapter regionAdapter;
    private SizeAdapter sizeAdapter;
    private HashMap<String, Region> regions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_droplet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
		dropletService = new DropletService(this);
        ImageService imageService = new ImageService(this);
        sizeService = new SizeService(this);
        RegionService regionService = new RegionService(this);
		
		imageSpinner = (Spinner)findViewById(R.id.imageSpinner);
		regionSpinner = (Spinner)findViewById(R.id.regionSpinner);
		sizeSpinner = (Spinner)findViewById(R.id.sizeSpinner);
		hostnameEditText = (EditText)findViewById(R.id.hostnameEditText);
		sshKeysMultiSelectSpinner = (MultiSelectSpinner) findViewById(R.id.sshKeysMultiSelectSpinner);
        TextView sshKeysTextView = (TextView) findViewById(R.id.sshKeysTextView);
		
		privateNetworkingCheckBox = (CheckBox)findViewById(R.id.privateNetworkingCheckBox);
		enableBackupsCheckBox = (CheckBox)findViewById(R.id.enableBackupsCheckBox);
		userDataCheckBox = (CheckBox)findViewById(R.id.userDataCheckBox);
		userDataEditText = (EditText)findViewById(R.id.userDataEditText);
		ipv6CheckBox = (CheckBox)findViewById(R.id.ipv6CheckBox);
		List<Image> images = new ArrayList<>();
		images.addAll(imageService.getSnapshotsOnly());
		images.addAll(imageService.getImagesOnly());
		imageSpinner.setAdapter(new ImageAdapter(this, images));
		List<String> sshKeysNames = new ArrayList<>();
		List<Long> sshKeysIds = new ArrayList<>();
		SSHKeyService sshKeyService = new SSHKeyService(this);
		List<SSHKey> sshKeys = sshKeyService.getAllSSHKeys();
		for (SSHKey sshKey : sshKeys) {
			sshKeysIds.add(sshKey.getId());
			sshKeysNames.add(sshKey.getName());
		}
		sshKeysMultiSelectSpinner.setIds(sshKeysIds);
		sshKeysMultiSelectSpinner.setItems(sshKeysNames);
		if(sshKeys.size() == 0){
			sshKeysMultiSelectSpinner.setVisibility(View.GONE);
			sshKeysTextView.setVisibility(View.GONE);
		}
        sizeAdapter = new SizeAdapter(this, sizeService.getAllSizes(SizeTable.MEMORY));
        List<Region> allRegions = regionService.getAllRegionsOrderedByName();
        regions = new HashMap<>();
        regionAdapter = new RegionAdapter(this, allRegions);
        for(Region region : allRegions){
            regions.put(region.getSlug(), region);
        }
        regionSpinner.setAdapter(regionAdapter);
		sizeSpinner.setAdapter(sizeAdapter);
		regionSpinner.setOnItemSelectedListener(this);
        imageSpinner.setOnItemSelectedListener(this);
		userDataCheckBox.setOnCheckedChangeListener(this);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_droplet, menu);
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_new_droplet){
			Long imageId = imageSpinner.getSelectedItemId();
			String regionSlug = ((Region)regionSpinner.getSelectedItem()).getSlug();
			String sizeSlug = ((Size)sizeSpinner.getSelectedItem()).getSlug();
			String hostname = hostnameEditText.getText().toString();
			boolean virtualNetworking = privateNetworkingCheckBox.isChecked();
			boolean enableBackups = enableBackupsCheckBox.isChecked();
			boolean enableIPv6 = ipv6CheckBox.isChecked();
			String userData = userDataEditText.getText().toString();
			List<Long> selectedSSHKeysIds = sshKeysMultiSelectSpinner.getSelectedIds();
			dropletService.createDroplet(hostname,imageId,regionSlug,sizeSlug,virtualNetworking,enableBackups,enableIPv6,userData,selectedSSHKeysIds);
			finish();
		}else if(item.getItemId() == android.R.id.home){
			finish();
		}
		return true;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
		if(parentView.getId() == R.id.regionSpinner){
			Region region = (Region)regionSpinner.getItemAtPosition(position);
			String features = region.getFeatures();
			
			enableBackupsCheckBox.setVisibility(features.contains("backups") ? View.VISIBLE : View.GONE);
			userDataCheckBox.setVisibility(features.contains("metadata") ? View.VISIBLE : View.GONE);
			privateNetworkingCheckBox.setVisibility(features.contains("private_networking") ? View.VISIBLE : View.GONE);
			ipv6CheckBox.setVisibility(features.contains("ipv6") ? View.VISIBLE : View.GONE);
		}
        else if(parentView.getId() == R.id.imageSpinner){
            Image image = (Image)imageSpinner.getItemAtPosition(position);
            SortedSet<String> imageRegions = new TreeSet<>(Arrays.asList(image.getRegions().split(";")));
            List<Region> newRegions = new ArrayList<>();
            for (String imageRegion : imageRegions) {
                newRegions.add(regions.get(imageRegion));
            }
            regionAdapter.setData(newRegions);

            List<Size> availableSizes = sizeService.getAllAvailableSizesFromMinDiskSize(image.getMinDiskSize());
            sizeAdapter.setData(availableSizes);
        }
	}

	@Override
	public void onNothingSelected(AdapterView<?> parentView) {
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView.getId() == R.id.userDataCheckBox){
			userDataEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
		}
	}
	
}
