package com.yassirh.digitalocean.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListFragment;
import android.content.Context;
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
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.service.DomainService;

public class DomainsFragment extends ListFragment implements OnItemClickListener, Updatable{
		
	private DomainAdapter mDomainAdapter;
	private DomainService mDomainService;
	private List<Domain> mDomains;
	private Domain mDomain;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDomainService = new DomainService(getActivity());
		update(this.getActivity());
		return inflater.inflate(R.layout.fragment_regions, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setOnItemClickListener(this);
		registerForContextMenu(getListView());
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
				break;
		}
		
		return true;
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
}
