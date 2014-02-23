package com.yassirh.digitalocean.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.SSHKey;
import com.yassirh.digitalocean.service.SSHKeyService;

public class SSHKeyFragment extends ListFragment implements Updatable{
		
	SSHKeyAdapter sshKeyAdapter;
	List<SSHKey> sshKeys;
	SSHKeyService mSSHKeyService;
	SSHKey mSSHKey;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		update(this.getActivity());
		return inflater.inflate(R.layout.fragment_ssh_keys, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	public void update(Context context) {
		sshKeys = new SSHKeyService(this.getActivity()).getAllSSHKeys();
		sshKeyAdapter = new SSHKeyAdapter(this.getActivity(), sshKeys);
		setListAdapter(sshKeyAdapter);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		mSSHKey = new SSHKeyService(getActivity()).findById(info.id);
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
}
