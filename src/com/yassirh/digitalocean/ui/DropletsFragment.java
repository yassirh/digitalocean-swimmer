package com.yassirh.digitalocean.ui;

import java.util.List;

import android.app.ListFragment;
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

public class DropletsFragment extends ListFragment implements OnItemClickListener{
		
	DropletAdapter mDropletAdapter;
	List<Droplet> mDroplets;
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
	
	public void update() {
		mDroplets = new DropletService(this.getActivity()).getAllDroplets();
		mDropletAdapter = new DropletAdapter(this.getActivity(), mDroplets);
		setListAdapter(mDropletAdapter);
	}
	
	// droplet that holds the selected droplet from the contextual menu
	Droplet mDroplet;
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_power_cycle:
			mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.POWER_ON);
			break;
		case R.id.action_shutdown:
			mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.SHUTDOWN);
			break;
		case R.id.action_power_off:
			mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.POWER_OFF);
			break;
		case R.id.action_power_on:
			mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.POWER_ON);
			break;
		case R.id.action_password_reset:
			mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.PASSWORD_RESET);
			break;
		case R.id.action_destroy:
			mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.DESTROY);
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
