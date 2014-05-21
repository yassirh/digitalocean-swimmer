package com.yassirh.digitalocean.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar.LayoutParams;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.model.Region;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.utils.ApiHelper;

public class DropletDetailsDialogFragment extends DialogFragment {


	DropletService mDropletService;
	
	public DropletDetailsDialogFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_droplet_details, container);
		mDropletService = new DropletService(this.getActivity());
		long id = getArguments().getLong("id");
		Droplet droplet = mDropletService.findById(id);
		Region region = droplet.getRegion();
		Size size = droplet.getSize();
		Image image = droplet.getImage();
		
		getDialog().setTitle(droplet.getName());
		
        TextView ipAddressTextView = (TextView)view.findViewById(R.id.ipAddressTextView);
        TextView nameTextView = (TextView)view.findViewById(R.id.nameTextView);
        TextView statusTextView = (TextView)view.findViewById(R.id.statusTextView);
        ImageView distroImageView = (ImageView)view.findViewById(R.id.distroImageView);
        ImageView flagImageView = (ImageView)view.findViewById(R.id.flagImageView);
        TextView backupsActiveTextView = (TextView)view.findViewById(R.id.backupsActiveTextView);
        TextView lockedTextView = (TextView)view.findViewById(R.id.lockedTextView);
        TextView createdAtTextView = (TextView)view.findViewById(R.id.createdAtTextView);
        TextView memoryTextView = (TextView)view.findViewById(R.id.memoryTextView);
        TextView diskTextView = (TextView)view.findViewById(R.id.diskTextView);
        TextView cpusTextView = (TextView)view.findViewById(R.id.cpusTextView);
        TextView regionTextView = (TextView)view.findViewById(R.id.regionTextView);
        TextView imageTextView = (TextView)view.findViewById(R.id.imageTextView);

        nameTextView.setText(droplet.getName());
        if(region != null){
        	flagImageView.setImageResource(ApiHelper.getLocationFlag(region.getName()));
        	regionTextView.setText(region.getName());
        }
        else{
        	flagImageView.setVisibility(View.GONE);
            regionTextView.setText("");
        }
    	    	
    	if(image != null){	
	    	distroImageView.setImageResource(ApiHelper.getDistributionLogo(image.getDistribution(), droplet.getStatus()));
    		imageTextView.setText(image.getName());
    	}
    	
        ipAddressTextView.setText(droplet.getIpAddress());
        backupsActiveTextView.setText(droplet.isBackupsActive() ? getResources().getString(R.string.yes) : getResources().getString(R.string.no));
        lockedTextView.setText(droplet.isLocked() ? getResources().getString(R.string.yes) : getResources().getString(R.string.no));
        createdAtTextView.setText(DateFormat.format("yyyy-MM-dd hh:mm", droplet.getCreatedAt()));
        statusTextView.setText(droplet.getStatus());
        if(size != null){
	        memoryTextView.setText(size.getMemory()  +"MB");
	        diskTextView.setText(size.getDisk() +"GB");
	        cpusTextView.setText(size.getCpu() + "");
        }
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}
}
