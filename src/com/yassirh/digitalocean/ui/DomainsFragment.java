package com.yassirh.digitalocean.ui;

import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.service.DomainService;
import com.yassirh.digitalocean.service.RegionService;

public class DomainsFragment extends ListFragment implements OnItemClickListener, Updatable{
		
	DomainAdapter mDomainAdapter;
	List<Domain> mDomains;
	RegionService regionService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		update(this.getActivity());
		return inflater.inflate(R.layout.fragment_regions, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setOnItemClickListener(this);
		registerForContextMenu(getListView());
	}


	public void update(Context context) {
		mDomains = new DomainService(context).getAllDomains();
		mDomainAdapter = new DomainAdapter(context, mDomains);
		setListAdapter(mDomainAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}
}
