package com.yassirh.digitalocean.ui;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.SizeTable;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.service.SizeService;

public class SizesFragment extends ListFragment implements Updatable{
		
	SizeAdapter sizeAdapter;
	List<Size> sizes;
	SizeService sizeService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		update(this.getActivity());
		return inflater.inflate(R.layout.fragment_sizes, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	public void update(Context context) {
		sizes = new SizeService(this.getActivity()).getAllSizes(SizeTable.MEMORY);
		sizeAdapter = new SizeAdapter(this.getActivity(), sizes, true);
		setListAdapter(sizeAdapter);
	}
}
