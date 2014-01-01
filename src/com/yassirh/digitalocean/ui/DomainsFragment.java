package com.yassirh.digitalocean.ui;

import java.util.HashMap;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.service.DomainService;
import com.yassirh.digitalocean.service.RecordService;

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
		return inflater.inflate(R.layout.fragment_domains, container, false);
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
			case R.id.action_add_record:
				builder = new AlertDialog.Builder(getActivity());
				inflater = getActivity().getLayoutInflater();
				final View view = inflater.inflate(R.layout.dialog_record_create, null);
				builder.setTitle(getString(R.string.add_record));
				final Spinner domainSpinner = (Spinner) view.findViewById(R.id.domainSpinner);
				domainSpinner.setAdapter(new DomainAdapter(getActivity(), new DomainService(getActivity()).getAllDomains()));
				final Spinner recordTypeSpinner = (Spinner) view.findViewById(R.id.recordTypeSpinner);
				final RecordTypeAdapter recordTypeAdapter = new RecordTypeAdapter(getActivity());
				recordTypeSpinner.setAdapter(recordTypeAdapter);
				recordTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
						LinearLayout aLinearLayout = (LinearLayout) view.findViewById(R.id.aLinearLayout);
						LinearLayout cnameLinearLayout = (LinearLayout) view.findViewById(R.id.cnameLinearLayout);
						LinearLayout mxLinearLayout = (LinearLayout) view.findViewById(R.id.mxLinearLayout);
						LinearLayout nsLinearLayout = (LinearLayout) view.findViewById(R.id.nsLinearLayout);
						LinearLayout txtLinearLayout = (LinearLayout) view.findViewById(R.id.txtLinearLayout);
						LinearLayout srvLinearLayout = (LinearLayout) view.findViewById(R.id.srvLinearLayout);
						aLinearLayout.setVisibility(View.GONE);
						cnameLinearLayout.setVisibility(View.GONE);
						mxLinearLayout.setVisibility(View.GONE);
						nsLinearLayout.setVisibility(View.GONE);
						txtLinearLayout.setVisibility(View.GONE);
						srvLinearLayout.setVisibility(View.GONE);
						switch ((Integer)recordTypeAdapter.getItem(position)) {
						case R.drawable.a:
							aLinearLayout.setVisibility(View.VISIBLE);
							break;
						case R.drawable.cname:
							cnameLinearLayout.setVisibility(View.VISIBLE);
							break;
						case R.drawable.mx:
							mxLinearLayout.setVisibility(View.VISIBLE);
							break;
						case R.drawable.txt:
							txtLinearLayout.setVisibility(View.VISIBLE);
							break;
						case R.drawable.srv:
							srvLinearLayout.setVisibility(View.VISIBLE);
							break;
						case R.drawable.ns:
							nsLinearLayout.setVisibility(View.VISIBLE);
							break;
						default:
							break;
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parentView) {
					}
				});
				builder.setView(view);
				builder.setPositiveButton(R.string.create_record, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						RecordService recordService = new RecordService(getActivity());
						HashMap<String, String> params = new HashMap<String, String>();
						switch ((Integer)recordTypeAdapter.getItem(recordTypeSpinner.getSelectedItemPosition())) {
						case R.drawable.a:
							params.put("record_type", "A");
							params.put("name", ((EditText) view.findViewById(R.id.aHostnameEditText)).getText().toString());
							params.put("data", ((EditText) view.findViewById(R.id.aIpAddressEditText)).getText().toString());
							break;
						case R.drawable.cname:
							params.put("record_type", "CNAME");
							params.put("name", ((EditText) view.findViewById(R.id.cnameNameEditText)).getText().toString());
							params.put("data", ((EditText) view.findViewById(R.id.cnameHostnameEditText)).getText().toString());
							break;
						case R.drawable.mx:
							params.put("record_type", "MX");
							params.put("data", ((EditText) view.findViewById(R.id.mxHostnameEditText)).getText().toString());
							params.put("priority", ((EditText) view.findViewById(R.id.mxPriorityEditText)).getText().toString());
							break;
						case R.drawable.txt:
							params.put("record_type", "TXT");
							params.put("name", ((EditText) view.findViewById(R.id.txtNameEditText)).getText().toString());
							params.put("data", ((EditText) view.findViewById(R.id.txtTextEditText)).getText().toString());
							break;
						case R.drawable.srv:
							params.put("record_type", "SRV");
							params.put("name", ((EditText) view.findViewById(R.id.srvNameEditText)).getText().toString());
							params.put("data", ((EditText) view.findViewById(R.id.srvHostnameEditText)).getText().toString());
							params.put("priority", ((EditText) view.findViewById(R.id.srvPriorityEditText)).getText().toString());
							params.put("port", ((EditText) view.findViewById(R.id.srvPortEditText)).getText().toString());
							params.put("weight", ((EditText) view.findViewById(R.id.srvWeightEditText)).getText().toString());
							break;
						case R.drawable.ns:
							params.put("record_type", "NS");
							params.put("data", ((EditText) view.findViewById(R.id.nsHostnameEditText)).getText().toString());
							break;
						default:
							break;
						}
						recordService.createRecord(domainSpinner.getSelectedItemId(), params, true);
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
