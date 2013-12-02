package com.yassirh.digitalocean.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.service.DropletService;

public class DropletsFragment extends ListFragment implements OnItemClickListener,Updatable{
		
	DropletAdapter mDropletAdapter;
	List<Droplet> mDroplets = new ArrayList<Droplet>();
	DropletService mDropletService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		update();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDropletService = new DropletService(this.getActivity());
		return inflater.inflate(R.layout.fragment_droplets, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		registerForContextMenu(getListView());
		getListView().setOnItemClickListener(this);
		registerForContextMenu(getListView());
	}
	
	@Override
	public void update() {
		mDroplets = new DropletService(this.getActivity()).getAllDroplets();
		mDropletAdapter = new DropletAdapter(this.getActivity(), mDroplets);
		setListAdapter(mDropletAdapter);
	}
	
	// droplet that holds the selected droplet from the contextual menu
	Droplet mDroplet;
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
		case R.id.action_power_cycle:
			alertDialog.setTitle(getString(R.string.power_cycle) + " : " + mDroplet.getName());
			alertDialog.setMessage(R.string.power_cycle_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.POWER_CYCLE);
				}
			});
			alertDialog.show();
			break;
		case R.id.action_reboot:
			alertDialog.setTitle(getString(R.string.reboot) + " : " + mDroplet.getName());
			alertDialog.setMessage(R.string.reboot_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.REBOOT);
				}
			});
			alertDialog.show();
			break;
		case R.id.action_shutdown:
			alertDialog.setTitle(getString(R.string.shut_down) + " : " + mDroplet.getName());
			alertDialog.setMessage(R.string.shut_down_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.SHUTDOWN);
				}
			});
			alertDialog.show();
			break;
		case R.id.action_power_off:
			alertDialog.setTitle(getString(R.string.power_off) + " : " + mDroplet.getName());
			alertDialog.setMessage(R.string.power_off_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.POWER_OFF);
				}
			});
			alertDialog.show();
			break;
		case R.id.action_power_on:
			alertDialog.setTitle(getString(R.string.power_on) + " : " + mDroplet.getName());
			alertDialog.setMessage(R.string.power_on_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.POWER_ON);
				}
			});
			alertDialog.show();
			break;
		case R.id.action_password_reset:
			mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.PASSWORD_RESET);
			break;
		case R.id.action_destroy:
			alertDialog.setTitle(getString(R.string.destroy) + " : " + mDroplet.getName());
			alertDialog.setMessage(R.string.destroy_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.DESTROY);
				}
			});
			alertDialog.show();
			break;
		default:
			break;
		}
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		mDroplet = new DropletService(getActivity()).findById(info.id);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.droplet_context, menu);
		
		if(mDroplet.getStatus().equals("active")){
			menu.removeItem(R.id.action_power_on);
		}
		else{
			menu.removeItem(R.id.action_power_off);
			menu.removeItem(R.id.action_shutdown);
			menu.removeItem(R.id.action_reboot);
			menu.removeItem(R.id.action_power_cycle);
		}		
		if(mDroplet.isBackupsActive())
			menu.removeItem(R.id.action_enable_backups);
		else
			menu.removeItem(R.id.action_disable_backups);
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Bundle args = new Bundle();
        args.putLong("id", id);
		DropletDetailsDialogFragment dropletDetailsDialogFragment = new DropletDetailsDialogFragment();
		dropletDetailsDialogFragment.setArguments(args);
		FragmentManager supportFragment = ((FragmentActivity)this.getActivity()).getSupportFragmentManager();
		dropletDetailsDialogFragment.show(supportFragment, "droplet_details_fragment");
	}
}
