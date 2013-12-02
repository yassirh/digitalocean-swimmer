package com.yassirh.digitalocean.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.SizeTable;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.ImageService;
import com.yassirh.digitalocean.service.RegionService;
import com.yassirh.digitalocean.service.SizeService;

public class CreateDropletDialogFragment extends DialogFragment {


	DropletService mDropletService;
	ImageService mImageService;
	SizeService mSizeService;
	RegionService mRegionService;
	
	public CreateDropletDialogFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_droplet_create, container);
		mDropletService = new DropletService(this.getActivity());
		mImageService = new ImageService(this.getActivity());
		mSizeService = new SizeService(this.getActivity());
		mRegionService = new RegionService(this.getActivity());
		getDialog().setTitle(getResources().getString(R.string.create_droplet));	
		
		final Spinner imageSpinner = (Spinner)view.findViewById(R.id.imageSpinner);
		final Spinner regionSpinner = (Spinner)view.findViewById(R.id.regionSpinner);
		final Spinner sizeSpinner = (Spinner)view.findViewById(R.id.sizeSpinner);
		final EditText hostnameEditText = (EditText)view.findViewById(R.id.hostnameEditText);
		final CheckBox privateNetworkingCheckBox = (CheckBox)view.findViewById(R.id.privateNetworkingCheckBox);
		Button createDropletButton = (Button)view.findViewById(R.id.createDropletButton);
				
		imageSpinner.setAdapter(new ImageAdapter(getActivity(), mImageService.getAllImages()));
		regionSpinner.setAdapter(new RegionAdapter(getActivity(), mRegionService.getAllRegions()));
		sizeSpinner.setAdapter(new SizeAdapter(getActivity(), mSizeService.getAllSizes(SizeTable.MEMORY),false));
		
		createDropletButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Long imageId = imageSpinner.getSelectedItemId();
				Long regionId = regionSpinner.getSelectedItemId();
				Long sizeId = sizeSpinner.getSelectedItemId();
				String hostname = hostnameEditText.getText().toString();
				boolean virtualNetworking = privateNetworkingCheckBox.isChecked(); 
				mDropletService.createDroplet(hostname,imageId,regionId,sizeId,virtualNetworking);
				CreateDropletDialogFragment.this.dismiss();
			}
		});
		
		return view;
	}
	
}
