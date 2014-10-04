package com.yassirh.digitalocean.ui;

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
import com.yassirh.digitalocean.data.SizeTable;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.service.SizeService;

public class SizesFragment extends ListFragment implements Updatable, SwipeRefreshLayout.OnRefreshListener{
		
	SizeAdapter mSizeAdapter;
	List<Size> mSizes;
	SizeService mSizeService;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mSizeService = new SizeService(this.getActivity());
		update(this.getActivity());
		View layout = inflater.inflate(R.layout.fragment_sizes, container, false);
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
				boolean enable = false;
		        if(listView != null && listView.getChildCount() > 0){
		            boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
		            boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
		            enable = firstItemVisible && topOfFirstItemVisible;
		        }
			    mSwipeRefreshLayout.setEnabled(enable);
			}
		});
		registerForContextMenu(listView);
	}

	@Override
	public void update(Context context) {
		mSizes = mSizeService.getAllSizes(SizeTable.MEMORY);
		mSizeAdapter = new SizeAdapter(this.getActivity(), mSizes);
		setListAdapter(mSizeAdapter);
	}
	
	@Override
	public void onRefresh() {
		mSizeService.getAllSizesFromAPI(true);
		handler.post(refreshing);
	}
	
	private final Runnable refreshing = new Runnable(){
	    public void run(){
	        try {
	        	if(mSizeService.isRefreshing()){
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
