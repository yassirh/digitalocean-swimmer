package com.yassirh.digitalocean.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.model.Region;
import com.yassirh.digitalocean.service.ImageService;
import com.yassirh.digitalocean.service.RegionService;

import java.util.ArrayList;
import java.util.List;

public class ImagesFragment extends ListFragment implements Updatable, SwipeRefreshLayout.OnRefreshListener{

    private ImageService imageService;
	private SwipeRefreshLayout swipeRefreshLayout;
    private Image image;
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
        ImageAdapter imageAdapter = new ImageAdapter(this.getActivity(), allImages, true);
		setListAdapter(imageAdapter);
	}
	
	@Override
	public void onRefresh() {
		imageService.getAllImagesFromAPI(true);
		handler.post(refreshing);
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        image = imageService.findImageById(info.id);
        if(!image.isPublic()) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.image_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        View view;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        switch (item.getItemId()) {
            case R.id.action_transfer:
                view = inflater.inflate(R.layout.dialog_transfer_image,null);
                RegionService sizeService = new RegionService(getActivity());
                builder.setTitle(R.string.title_tranfer_image);
                final Spinner regionSpinner = (Spinner)view.findViewById(R.id.regionSpinner);
                regionSpinner.setAdapter(new RegionAdapter(getActivity(), sizeService.getAllRegions()));
                builder.setView(view);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageService.transferImage(image.getId(),((Region)regionSpinner.getSelectedItem()).getSlug());
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            case R.id.action_rename:
                view = inflater.inflate(R.layout.dialog_image_rename, null);
                builder.setTitle(R.string.title_image_rename);
                final EditText nameEditText = (EditText)view.findViewById(R.id.nameEditText);
                builder.setView(view);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageService.updateImage(image.getId(), nameEditText.getText().toString());
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            case R.id.action_destroy:
                view = inflater.inflate(R.layout.dialog_image_destroy, null);
                builder.setTitle(getString(R.string.destroy) + " : " + image.getName());
                builder.setView(view);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageService.destroySnapshot(image.getId());
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }
        return true;
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
