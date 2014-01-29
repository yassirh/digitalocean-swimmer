package com.yassirh.digitalocean.ui;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.service.ImageService;

public class ImagesFragment extends ListFragment implements Updatable{
		
	ImageAdapter imageAdapter;
	List<Image> images;
	ImageService imageService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		update(this.getActivity());
		return inflater.inflate(R.layout.fragment_images, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	public void update(Context context) {
		images = new ImageService(this.getActivity()).getAllImages();
		imageAdapter = new ImageAdapter(this.getActivity(), images);
		setListAdapter(imageAdapter);	
	}
}
