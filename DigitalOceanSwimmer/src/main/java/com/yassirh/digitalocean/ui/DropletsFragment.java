package com.yassirh.digitalocean.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.SizeTable;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.model.Network;
import com.yassirh.digitalocean.model.Size;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.ImageService;
import com.yassirh.digitalocean.service.SizeService;

import java.util.HashMap;
import java.util.List;

public class DropletsFragment extends ListFragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, Updatable{

	// droplet that holds the selected droplet from the contextual menu
	private Droplet droplet;
    private DropletService dropletService;
	private ImageService imageService;
	private SwipeRefreshLayout swipeRefreshLayout;
	private Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		dropletService = new DropletService(this.getActivity());
		imageService = new ImageService(getActivity());
		update(this.getActivity());
		View layout = inflater.inflate(R.layout.fragment_droplets, container, false);
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
		        if(listView.getChildCount() > 0){
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
        List<Droplet> droplets = new DropletService(context).getAllDroplets();
        DropletAdapter dropletAdapter = new DropletAdapter(context, droplets);
		setListAdapter(dropletAdapter);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		View view;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder alertDialog = new Builder(getActivity());
		alertDialog.setNegativeButton(R.string.no, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		switch (item.getItemId()) {
		case R.id.action_power_cycle:
			alertDialog.setTitle(getString(R.string.power_cycle) + " : " + droplet.getName());
			alertDialog.setMessage(R.string.power_cycle_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dropletService.executeAction(droplet.getId(), DropletService.DropletActions.POWER_CYCLE, new HashMap<String, String>());
				}
			});
			alertDialog.show();
			break;
		case R.id.action_reboot:
			alertDialog.setTitle(getString(R.string.reboot) + " : " + droplet.getName());
			alertDialog.setMessage(R.string.reboot_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dropletService.executeAction(droplet.getId(), DropletService.DropletActions.REBOOT, new HashMap<String, String>());
				}
			});
			alertDialog.show();
			break;
		case R.id.action_shutdown:
			alertDialog.setTitle(getString(R.string.shut_down) + " : " + droplet.getName());
			alertDialog.setMessage(R.string.shut_down_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dropletService.executeAction(droplet.getId(), DropletService.DropletActions.SHUTDOWN, new HashMap<String, String>());
				}
			});
			alertDialog.show();
			break;
		case R.id.action_power_off:
			alertDialog.setTitle(getString(R.string.power_off) + " : " + droplet.getName());
			alertDialog.setMessage(R.string.power_off_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dropletService.executeAction(droplet.getId(), DropletService.DropletActions.POWER_OFF, new HashMap<String, String>());
				}
			});
			alertDialog.show();
			break;
		case R.id.action_power_on:
			alertDialog.setTitle(getString(R.string.power_on) + " : " + droplet.getName());
			alertDialog.setMessage(R.string.power_on_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dropletService.executeAction(droplet.getId(), DropletService.DropletActions.POWER_ON, new HashMap<String, String>());
				}
			});
			alertDialog.show();
			break;
		case R.id.action_password_reset:
            alertDialog.setTitle(getString(R.string.password_reset) + " : " + droplet.getName());
            alertDialog.setMessage(R.string.reset_password_alert);
            alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dropletService.executeAction(droplet.getId(), DropletService.DropletActions.PASSWORD_RESET, new HashMap<String, String>());
                }
            });
            alertDialog.show();
			break;
		case R.id.action_destroy:
			view = inflater.inflate(R.layout.dialog_droplet_destroy, null);
			builder.setTitle(getString(R.string.destroy) + " : " + droplet.getName());
			builder.setView(view);
			builder.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dropletService.destroyDroplet(droplet.getId());
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
			alertDialog.setTitle(getString(R.string.enable_backups) + " : " + droplet.getName());
			alertDialog.setMessage(R.string.enable_backups_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dropletService.executeAction(droplet.getId(), DropletService.DropletActions.ENABLE_BACKUPS, new HashMap<String, String>());
				}
			});
			alertDialog.show();
			break;
		case R.id.action_disable_backups:
			alertDialog.setTitle(getString(R.string.disable_backups) + " : " + droplet.getName());
			alertDialog.setMessage(R.string.disable_backups_alert);
			alertDialog.setPositiveButton(R.string.yes, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dropletService.executeAction(droplet.getId(), DropletService.DropletActions.DISABLE_BACKUPS, new HashMap<String, String>());
				}
			});
			alertDialog.show();
			break;
		case R.id.action_resize:
			view = inflater.inflate(R.layout.dialog_droplet_resize,null);
		    SizeService sizeService = new SizeService(getActivity());
			builder.setTitle(R.string.title_resize_droplet);
			final Spinner sizeSpinner = (Spinner)view.findViewById(R.id.sizeSpinner);
			sizeSpinner.setAdapter(new SizeAdapter(getActivity(), sizeService.getAllSizes(SizeTable.MEMORY)));
			builder.setView(view);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("size", ((Size)sizeSpinner.getSelectedItem()).getSlug());
					dropletService.executeAction(droplet.getId(), DropletService.DropletActions.RESIZE, params);
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
        case R.id.action_rename:
            view = inflater.inflate(R.layout.dialog_droplet_rename, null);
            builder.setTitle(R.string.title_droplet_rename);
            final EditText nameEditText = (EditText)view.findViewById(R.id.nameEditText);
            builder.setView(view);
            builder.setPositiveButton(R.string.ok, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("name", nameEditText.getText().toString());
                    dropletService.executeAction(droplet.getId(), DropletService.DropletActions.RENAME, params);
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
		    view = inflater.inflate(R.layout.dialog_droplet_snapshot,null);
			builder.setTitle(R.string.title_snapshot_droplet);
			final EditText snapShotNameEditText = (EditText)view.findViewById(R.id.nameEditText);
			builder.setView(view);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("name", snapShotNameEditText.getText().toString());
					dropletService.executeAction(droplet.getId(), DropletService.DropletActions.SNAPSHOT, params);
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
			view = inflater.inflate(R.layout.dialog_droplet_restore,null);
			builder.setTitle(R.string.title_restore_droplet);
			final Spinner restoreImageSpinner = (Spinner)view.findViewById(R.id.imageSpinner);
			restoreImageSpinner.setAdapter(new ImageAdapter(getActivity(), imageService.getSnapshotsOnly()));
			builder.setView(view);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("image_id", restoreImageSpinner.getSelectedItemId()+"");
					dropletService.executeAction(droplet.getId(), DropletService.DropletActions.RESTORE, params);
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
			view = inflater.inflate(R.layout.dialog_droplet_rebuild,null);
			builder.setTitle(R.string.title_rebuild_droplet);
			final Spinner rebuildImageSpinner = (Spinner)view.findViewById(R.id.imageSpinner);
			rebuildImageSpinner.setAdapter(new ImageAdapter(getActivity(), imageService.getImagesOnly()));
			builder.setView(view);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("image", ((Image)rebuildImageSpinner.getSelectedItem()).getSlug());
					dropletService.executeAction(droplet.getId(), DropletService.DropletActions.REBUILD, params);
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
				String ipAddress = "";
				for (Network network : droplet.getNetworks()) {
					if(network.getType().equals("public")){
						ipAddress = network.getIpAddress();
						break;
					}
				}
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("ssh://" + ipAddress + "/#" + droplet.getName())));
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
		droplet = new DropletService(getActivity()).findById(info.id);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.droplet_context, menu);
		
		if(droplet.getStatus().equals("active")){
			menu.removeItem(R.id.action_power_on);
		}
		else{
			menu.removeItem(R.id.action_power_off);
			menu.removeItem(R.id.action_shutdown);
			menu.removeItem(R.id.action_reboot);
			menu.removeItem(R.id.action_power_cycle);
			menu.removeItem(R.id.action_ssh);
		}		
		if(droplet.isBackupsEnabled())
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
		FragmentManager supportFragment = this.getActivity().getSupportFragmentManager();
		dropletDetailsDialogFragment.show(supportFragment, "droplet_details_fragment");
	}

	@Override
	public void onRefresh() {
		dropletService.getAllDropletsFromAPI(true, true);
		handler.post(refreshing);
	}
	
	private final Runnable refreshing = new Runnable(){
	    public void run(){
	        try {
	        	if(dropletService.isRefreshing()){
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
