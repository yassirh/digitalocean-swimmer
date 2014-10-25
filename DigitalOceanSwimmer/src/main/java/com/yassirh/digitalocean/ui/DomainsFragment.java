package com.yassirh.digitalocean.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
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
		
	private DomainAdapter domainAdapter;
	private DomainService domainService;
	private List<Domain> domains;
	private Domain domain;
	private SwipeRefreshLayout swipeRefreshLayout;
	private Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		domainService = new DomainService(getActivity());
		update(this.getActivity());
		View layout = inflater.inflate(R.layout.fragment_domains, container, false);
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
		listView.setOnItemClickListener(this);
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


	public void update(Context context) {
		domains = new DomainService(context).getAllDomains();
		domainAdapter = new DomainAdapter(context, domains);
		setListAdapter(domainAdapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		domain = (Domain)(domainAdapter.getItem(info.position));
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
				alertDialog.setTitle(getString(R.string.destroy) + " : " + domain.getName());
				alertDialog.setMessage(R.string.destroy_domain_alert);
				alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						domainService.deleteDomain(domain.getName(),true);
					}
				});
				alertDialog.show();
				return true;
			case R.id.action_add_record:
				Bundle args = new Bundle();
				args.putString("domain_name", domain.getName());
				FragmentManager fm = getActivity().getSupportFragmentManager();
				RecordCreateDialogFragment recordCreateDialogFragment = new RecordCreateDialogFragment();
				recordCreateDialogFragment.setArguments(args);
				recordCreateDialogFragment.show(fm, "create_record");
				return true;
			case R.id.action_visit_domain:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + domain.getName()));
				startActivity(browserIntent);
				return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Bundle args = new Bundle();
		Domain domain = (Domain) domainAdapter.getItem(position);
        args.putString("domainName", domain.getName());
		DomainDetailsDialogFragment domainDetailsDialogFragment = new DomainDetailsDialogFragment();
		domainDetailsDialogFragment.setArguments(args);
		FragmentManager supportFragment = ((FragmentActivity)this.getActivity()).getSupportFragmentManager();
		domainDetailsDialogFragment.show(supportFragment, "droplet_domain_fragment");
	}
	
	@Override
	public void onRefresh() {
		domainService.getAllDomainsFromAPI(true);
		handler.post(refreshing);
	}
	
	private final Runnable refreshing = new Runnable(){
	    public void run(){
	        try {
	        	if(domainService.isRefreshing()){
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
