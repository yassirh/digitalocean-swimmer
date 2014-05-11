package com.yassirh.digitalocean.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.service.ImageService;

public class ImagesFragment extends ListFragment implements Updatable, SwipeRefreshLayout.OnRefreshListener{
		
	private ImageAdapter mImageAdapter;
	private List<Image> mImages;
	private List<Image> mSnapshots;
	private List<Image> mAllImages;
	private ImageService mImageService;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mImageService = new ImageService(this.getActivity());
		update(this.getActivity());
		View layout = inflater.inflate(R.layout.fragment_images, container, false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(R.color.blue_bright,
	            R.color.green_light,
	            R.color.orange_light,
	            R.color.red_light);
		return layout;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final ListView listView = getListView();
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			    mSwipeRefreshLayout.setEnabled(listView.getFirstVisiblePosition() == 0);
			}
		});
		registerForContextMenu(listView);
	}

	@Override
	public void update(Context context) {
		mImages = mImageService.getImagesOnly();
		mSnapshots = mImageService.getSnapshotsOnly();
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
	
	@Override
	public void onRefresh() {
		mImageService.getAllImagesFromAPI(true);
		handler.post(refreshing);
	}
	
	private final Runnable refreshing = new Runnable(){
	    public void run(){
	        try {
	        	if(mImageService.isRefreshing()){
	        		handler.postDelayed(this, 1000);   
	        	}else{
	        		mSwipeRefreshLayout.setRefreshing(false);
	        	}
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }   
	    }
	};
}
