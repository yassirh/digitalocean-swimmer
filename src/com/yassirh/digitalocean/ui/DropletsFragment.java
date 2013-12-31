package com.yassirh.digitalocean.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
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
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.DropletTable;
import com.yassirh.digitalocean.data.SizeTable;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.ImageService;
import com.yassirh.digitalocean.service.SizeService;

public class DropletsFragment extends ListFragment implements OnItemClickListener,Updatable{
		
	DropletAdapter mDropletAdapter;
	List<Droplet> mDroplets = new ArrayList<Droplet>();
	DropletService mDropletService;
	ImageService mImageService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDropletService = new DropletService(this.getActivity());
		mImageService = new ImageService(getActivity());
		update(this.getActivity());
		return inflater.inflate(R.layout.fragment_droplets, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setOnItemClickListener(this);
		registerForContextMenu(getListView());
	}
	
	@Override
	public void update(Context context) {
		mDroplets = new DropletService(context).getAllDroplets();
		mDropletAdapter = new DropletAdapter(context, mDroplets);
		setListAdapter(mDropletAdapter);
	}
	
	// droplet that holds the selected droplet from the contextual menu
	Droplet mDroplet;
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		View view;
		AlertDialog.Builder builder;
		LayoutInflater inflater;
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
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.POWER_CYCLE, new HashMap<String, String>());
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
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.REBOOT, new HashMap<String, String>());
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
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.SHUTDOWN, new HashMap<String, String>());
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
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.POWER_OFF, new HashMap<String, String>());
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
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.POWER_ON, new HashMap<String, String>());
				}
			});
			alertDialog.show();
			break;
		case R.id.action_password_reset:
			mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.PASSWORD_RESET, new HashMap<String, String>());
			break;
		case R.id.action_destroy:
			builder = new AlertDialog.Builder(getActivity());
			inflater = getActivity().getLayoutInflater();
			view = inflater.inflate(R.layout.dialog_droplet_destroy, null);
			builder.setTitle(getString(R.string.destroy) + " : " + mDroplet.getName());
			final CheckBox scrubDataCheckBox = (CheckBox) view.findViewById(R.id.scrubDataCheckBox);
			builder.setView(view);
			builder.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					HashMap<String, String> params = new HashMap<String, String>();
					if(scrubDataCheckBox.isChecked())
						params.put("scrub_data", "true");
					else
						params.put("scrub_data", "false");
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.DESTROY, params);
				}
			});
			builder.setNegativeButton(R.string.no, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			break;
		case R.id.action_enable_backups:
			alertDialog.setTitle(getString(R.string.enable_backups) + " : " + mDroplet.getName());
			alertDialog.setMessage(R.string.enable_backups_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.ENABLE_BACKUPS, new HashMap<String, String>());
				}
			});
			alertDialog.show();
			break;
		case R.id.action_disable_backups:
			alertDialog.setTitle(getString(R.string.disable_backups) + " : " + mDroplet.getName());
			alertDialog.setMessage(R.string.disable_backups_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.DISABLE_BACKUPS, new HashMap<String, String>());
				}
			});
			alertDialog.show();
			break;
		case R.id.action_resize:
			builder = new AlertDialog.Builder(getActivity());
		    inflater = getActivity().getLayoutInflater();
		    view = inflater.inflate(R.layout.dialog_droplet_resize,null);
		    SizeService sizeService = new SizeService(getActivity());
			builder.setTitle(R.string.title_resize_droplet);
			final Spinner sizeSpinner = (Spinner)view.findViewById(R.id.sizeSpinner);
			sizeSpinner.setAdapter(new SizeAdapter(getActivity(), sizeService.getAllSizes(SizeTable.MEMORY),false));
			builder.setView(view);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put(DropletTable.SIZE_ID, sizeSpinner.getSelectedItemId()+"");
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.RESIZE, params);
				}
			});
			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			break;
		case R.id.action_snapshot:
			builder = new AlertDialog.Builder(getActivity());
		    inflater = getActivity().getLayoutInflater();
		    view = inflater.inflate(R.layout.dialog_droplet_snapshot,null);
			builder.setTitle(R.string.title_snapshot_droplet);
			final EditText nameEditText = (EditText)view.findViewById(R.id.nameEditText);
			builder.setView(view);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("name", nameEditText.getText().toString());
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.SNAPSHOT, params);
				}
			});
			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			break;			
		case R.id.action_restore:
			builder = new AlertDialog.Builder(getActivity());
		    inflater = getActivity().getLayoutInflater();
		    view = inflater.inflate(R.layout.dialog_droplet_restore,null);
			builder.setTitle(R.string.title_restore_droplet);
			final Spinner restoreImageSpinner = (Spinner)view.findViewById(R.id.imageSpinner);
			restoreImageSpinner.setAdapter(new ImageAdapter(getActivity(), mImageService.getSnapshotsOnly()));
			builder.setView(view);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("image_id", restoreImageSpinner.getSelectedItemId()+"");
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.RESTORE, params);
				}
			});
			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			break;
		case R.id.action_rebuild:
			builder = new AlertDialog.Builder(getActivity());
		    inflater = getActivity().getLayoutInflater();
		    view = inflater.inflate(R.layout.dialog_droplet_rebuild,null);
			builder.setTitle(R.string.title_rebuild_droplet);
			final Spinner rebuildImageSpinner = (Spinner)view.findViewById(R.id.imageSpinner);
			rebuildImageSpinner.setAdapter(new ImageAdapter(getActivity(), mImageService.getImagesOnly()));
			builder.setView(view);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("image_id", rebuildImageSpinner.getSelectedItemId()+"");
					mDropletService.ExecuteAction(mDroplet.getId(), DropletService.DropletActions.REBUILD, params);
				}
			});
			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			break;
		case R.id.action_ssh:
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("ssh://" + mDroplet.getIpAddress() + "/#" + mDroplet.getName())));
				getActivity().finish();	
			} catch (ActivityNotFoundException e) {
				Toast.makeText(getActivity(), R.string.no_ssh_client, Toast.LENGTH_SHORT).show();
			}
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
			menu.removeItem(R.id.action_ssh);
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
