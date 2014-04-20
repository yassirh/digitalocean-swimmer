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
		
	SSHKeyAdapter sshKeyAdapter;
	List<SSHKey> sshKeys;
	SSHKeyService mSSHKeyService;
	SSHKey mSSHKey;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mSSHKeyService = new SSHKeyService(getActivity());
		update(this.getActivity());
		View layout = inflater.inflate(R.layout.fragment_ssh_keys, container, false);
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

	@Override
	public void update(Context context) {
		sshKeys = mSSHKeyService.getAllSSHKeys();
		sshKeyAdapter = new SSHKeyAdapter(this.getActivity(), sshKeys);
		setListAdapter(sshKeyAdapter);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		mSSHKey = mSSHKeyService.findById(info.id);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.ssh_key_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(mSSHKeyService == null)
			mSSHKeyService = new SSHKeyService(getActivity());
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
		        args.putLong("ssh_key_id", mSSHKey.getId());
		        FragmentManager fm = getActivity().getSupportFragmentManager();
				SSHKeyCreateDialogFragment sshKeyCreateDialogFragment = new SSHKeyCreateDialogFragment();
				sshKeyCreateDialogFragment.setArguments(args);
				sshKeyCreateDialogFragment.show(fm, "edit_ssh_key");
				return true;
			case R.id.action_destroy:
				alertDialog.setTitle(getString(R.string.destroy) + " : " + mSSHKey.getName());
				alertDialog.setMessage(R.string.destroy_ssh_key_alert);
				alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSSHKeyService.delete(mSSHKey.getId(),true);
					}
				});
				alertDialog.show();
				return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onRefresh() {
		mSSHKeyService.getAllSSHKeysFromAPI(true);
		handler.post(refreshing);
	}
	
	private final Runnable refreshing = new Runnable(){
	    public void run(){
	        try {
	        	if(mSSHKeyService.isRefreshing()){
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
