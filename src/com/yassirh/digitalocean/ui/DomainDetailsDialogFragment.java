package com.yassirh.digitalocean.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.service.DomainService;

public class DomainDetailsDialogFragment extends DialogFragment {


	DomainService mDomainService;
	
	public DomainDetailsDialogFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View vi = inflater.inflate(R.layout.dialog_domain_details, container);
		long id = getArguments().getLong("id");
		mDomainService = new DomainService(getActivity());
		Domain domain = mDomainService.findById(id);
        RecordAdapter recordAdapter = new RecordAdapter(getActivity(), domain.getRecords());
        
		getDialog().setTitle(domain.getName());
		TextView domainTextView = (TextView)vi.findViewById(R.id.domainTextView);
        TextView ttlTextView = (TextView)vi.findViewById(R.id.ttlTextView);
        ListView recordsListView = (ListView)vi.findViewById(R.id.recordsListView);
        
        domainTextView.setText(domain.getName());
        ttlTextView.setText("ttl : " + domain.getTtl());
        recordsListView.setAdapter(recordAdapter);
		return vi;
	}
	
}
