package com.yassirh.digitalocean.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.service.DomainService;
import com.yassirh.digitalocean.service.DropletService;

public class CreateDomainDialogFragment extends DialogFragment {


	DropletService mDropletService;
	DomainService mDomainService;
	
	public CreateDomainDialogFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_domain_create, container);
		mDropletService = new DropletService(this.getActivity());
		mDomainService = new DomainService(this.getActivity());
		getDialog().setTitle(getResources().getString(R.string.create_domain));	
		
		final EditText domainNameEditText = (EditText)view.findViewById(R.id.domainNameEditText);
		final Spinner dropletSpinner = (Spinner)view.findViewById(R.id.dropletSpinner);
		
		Button createDomainButton = (Button)view.findViewById(R.id.createDomainButton);
				
		dropletSpinner.setAdapter(new DropletAdapter(getActivity(), mDropletService.getAllDroplets()));
		
		createDomainButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 
				mDomainService.createDomain(domainNameEditText.getText().toString(),((Droplet)dropletSpinner.getSelectedItem()).getIpAddress(),true);
				CreateDomainDialogFragment.this.dismiss();
			}
		});
		
		return view;
	}
	
}
