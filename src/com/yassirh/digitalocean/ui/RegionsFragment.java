package com.yassirh.digitalocean.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Region;
import com.yassirh.digitalocean.service.RegionService;

public class RegionsFragment extends ListFragment implements Updatable{
		
	RegionAdapter regionAdapter;
	List<Region> regions = new ArrayList<Region>();
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
		registerForContextMenu(getListView());
	}

	@Override
	public void update(Context context) {
		regions = new RegionService(context).getAllRegions();
		regionAdapter = new RegionAdapter(this.getActivity(), regions);
		setListAdapter(regionAdapter);
	}
}
