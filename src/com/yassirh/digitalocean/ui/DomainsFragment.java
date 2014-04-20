package com.yassirh.digitalocean.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.service.DomainService;

public class DomainsFragment extends ListFragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, Updatable{
		
	private DomainAdapter mDomainAdapter;
	private DomainService mDomainService;
	private List<Domain> mDomains;
	private Domain mDomain;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDomainService = new DomainService(getActivity());
		update(this.getActivity());
		View layout = inflater.inflate(R.layout.fragment_domains, container, false);
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
				int topRowVerticalPosition = 
					      (listView == null || listView.getChildCount() == 0) ? 
					        0 : listView.getChildAt(0).getTop();
					    mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
			}
		});
		registerForContextMenu(listView);
	}


	public void update(Context context) {
		mDomains = new DomainService(context).getAllDomains();
		mDomainAdapter = new DomainAdapter(context, mDomains);
		setListAdapter(mDomainAdapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		mDomain = new DomainService(getActivity()).findById(info.id);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.domain_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AlertDialog.Builder alertDialog = new Builder(getActivity());
		alertDialog.setNegativeButton(R.string.no, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		switch (item.getItemId()) {
			case R.id.action_destroy:
				alertDialog.setTitle(getString(R.string.destroy) + " : " + mDomain.getName());
				alertDialog.setMessage(R.string.destroy_domain_alert);
				alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDomainService.deleteDomain(mDomain.getId(),true);
					}
				});
				alertDialog.show();
				return true;
			case R.id.action_add_record:
				Bundle args = new Bundle();
		        args.putLong("domain_id", mDomain.getId());
				FragmentManager fm = getActivity().getSupportFragmentManager();
				RecordCreateDialogFragment recordCreateDialogFragment = new RecordCreateDialogFragment();
				recordCreateDialogFragment.setArguments(args);
				recordCreateDialogFragment.show(fm, "create_record");
				return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Bundle args = new Bundle();
        args.putLong("id", id);
		DomainDetailsDialogFragment domainDetailsDialogFragment = new DomainDetailsDialogFragment();
		domainDetailsDialogFragment.setArguments(args);
		FragmentManager supportFragment = ((FragmentActivity)this.getActivity()).getSupportFragmentManager();
		domainDetailsDialogFragment.show(supportFragment, "droplet_domain_fragment");
	}
	
	@Override
	public void onRefresh() {
		mDomainService.getAllDomainsFromAPI(true);
		handler.post(refreshing);
	}
	
	private final Runnable refreshing = new Runnable(){
	    public void run(){
	        try {
	        	if(mDomainService.isRefreshing()){
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
