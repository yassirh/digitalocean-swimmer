package com.yassirh.digitalocean.ui;

import java.util.ArrayList;
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
		
	ImageAdapter mImageAdapter;
	List<Image> mImages;
	List<Image> mSnapshots;
	List<Image> mAllImages;
	ImageService mImageService;
	
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
		mImages = new ImageService(this.getActivity()).getImagesOnly();
		mSnapshots = new ImageService(this.getActivity()).getSnapshotsOnly();
		mAllImages = new ArrayList<Image>();
		if(mSnapshots.size() > 0){
			// used for the listview header
			Image snapshot = new Image();
			snapshot.setId(0);
			snapshot.setName("Snapshots");
			mAllImages.add(snapshot);
			mAllImages.addAll(mSnapshots);
		}
		// used for the listview header
		Image image = new Image();
		image.setId(0);
		image.setName("Public images");		
		
		mAllImages.add(image);
		mAllImages.addAll(mImages);
		mImageAdapter = new ImageAdapter(this.getActivity(), mAllImages);
		setListAdapter(mImageAdapter);	
	}
}
