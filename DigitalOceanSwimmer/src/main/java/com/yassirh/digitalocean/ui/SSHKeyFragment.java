package com.yassirh.digitalocean.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.SSHKey;
import com.yassirh.digitalocean.service.SSHKeyService;

public class SSHKeyFragment extends ListFragment implements Updatable, SwipeRefreshLayout.OnRefreshListener{

    private SSHKeyAdapter sshKeyAdapter;
    private List<SSHKey> sshKeys;
    private SSHKeyService sshKeyService;
	private SSHKey sshKey;
	private SwipeRefreshLayout swipeRefreshLayout;
	private Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		sshKeyService = new SSHKeyService(getActivity());
		update(this.getActivity());
		View layout = inflater.inflate(R.layout.fragment_ssh_keys, container, false);
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
		sshKeys = sshKeyService.getAllSSHKeys();
		sshKeyAdapter = new SSHKeyAdapter(this.getActivity(), sshKeys);
		setListAdapter(sshKeyAdapter);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		sshKey = sshKeyService.findById(info.id);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.ssh_key_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(sshKeyService == null)
			sshKeyService = new SSHKeyService(getActivity());
		AlertDialog.Builder alertDialog = new Builder(getActivity());
		alertDialog.setNegativeButton(R.string.no, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		switch (item.getItemId()) {
			case R.id.action_edit:
				Bundle args = new Bundle();
		        args.putLong("ssh_key_id", sshKey.getId());
		        FragmentManager fm = getActivity().getSupportFragmentManager();
				SSHKeyCreateDialogFragment sshKeyCreateDialogFragment = new SSHKeyCreateDialogFragment();
				sshKeyCreateDialogFragment.setArguments(args);
				sshKeyCreateDialogFragment.show(fm, "edit_ssh_key");
				return true;
			case R.id.action_destroy:
				alertDialog.setTitle(getString(R.string.destroy) + " : " + sshKey.getName());
				alertDialog.setMessage(R.string.destroy_ssh_key_alert);
				alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sshKeyService.delete(sshKey.getId());
					}
				});
				alertDialog.show();
				return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onRefresh() {
		sshKeyService.getAllSSHKeysFromAPI(true);
		handler.post(refreshing);
	}
	
	private final Runnable refreshing = new Runnable(){
	    public void run(){
	        try {
	        	if(sshKeyService.isRefreshing()){
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
