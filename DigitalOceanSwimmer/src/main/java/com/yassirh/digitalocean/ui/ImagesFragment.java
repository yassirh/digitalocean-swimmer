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

    private ImageService imageService;
	private SwipeRefreshLayout swipeRefreshLayout;
	private Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		imageService = new ImageService(this.getActivity());
		update(this.getActivity());
		View layout = inflater.inflate(R.layout.fragment_images, container, false);
		swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setColorSchemeResources(R.color.blue_bright,
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
				boolean enable = false;
		        if(listView != null && listView.getChildCount() > 0){
		            boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
		            boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
		            enable = firstItemVisible && topOfFirstItemVisible;
		        }
			    swipeRefreshLayout.setEnabled(enable);
			}
		});
		registerForContextMenu(listView);
	}

	@Override
	public void update(Context context) {
        List<Image> images = imageService.getImagesOnly();
        List<Image> snapshots = imageService.getSnapshotsOnly();
        List<Image> allImages = new ArrayList<Image>();
		if(snapshots.size() > 0){
			// used for the listview header
			Image snapshot = new Image();
			snapshot.setId(0);
			snapshot.setName("Snapshots");
			allImages.add(snapshot);
			allImages.addAll(snapshots);
		}
		// used for the listview header
		Image image = new Image();
		image.setId(0);
		image.setName("Public images");		
		
		allImages.add(image);
		allImages.addAll(images);
        ImageAdapter imageAdapter = new ImageAdapter(this.getActivity(), allImages);
		setListAdapter(imageAdapter);
	}
	
	@Override
	public void onRefresh() {
		imageService.getAllImagesFromAPI(true);
		handler.post(refreshing);
	}
	
	private final Runnable refreshing = new Runnable(){
	    public void run(){
	        try {
	        	if(imageService.isRefreshing()){
	        		handler.postDelayed(this, 1000);   
	        	}else{
	        		swipeRefreshLayout.setRefreshing(false);
	        	}
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }   
	    }
	};
}
