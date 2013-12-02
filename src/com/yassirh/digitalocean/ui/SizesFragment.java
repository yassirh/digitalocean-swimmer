package com.yassirh.digitalocean.ui;

import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.SizeTable;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.service.SizeService;

public class SizesFragment extends ListFragment{
		
	SizeAdapter sizeAdapter;
	List<Size> sizes;
	SizeService sizeService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		update();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sizes, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		registerForContextMenu(getListView());
	}


	public void update() {
		sizes = new SizeService(this.getActivity()).getAllSizes(SizeTable.MEMORY);
		sizeAdapter = new SizeAdapter(this.getActivity(), sizes, true);
		setListAdapter(sizeAdapter);
	}
}
