package com.yassirh.digitalocean.ui;

import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.service.ImageService;

public class ImagesFragment extends ListFragment{
		
	ImageAdapter imageAdapter;
	List<Image> images;
	ImageService imageService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		update();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_images, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		registerForContextMenu(getListView());
	}


	public void update() {
		images = new ImageService(this.getActivity()).getAllImages();
		imageAdapter = new ImageAdapter(this.getActivity(), images);
		setListAdapter(imageAdapter);
	}
}
