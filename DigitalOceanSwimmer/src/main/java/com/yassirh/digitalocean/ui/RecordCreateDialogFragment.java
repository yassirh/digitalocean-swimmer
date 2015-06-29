package com.yassirh.digitalocean.ui;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.Network;
import com.yassirh.digitalocean.model.Record;
import com.yassirh.digitalocean.service.DomainService;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.RecordService;

public class RecordCreateDialogFragment extends DialogFragment {
	
	private long recordId = 0L;
	private String domainName = "";
	private RecordService recordService;
    private AlertDialog.Builder builder;

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		if(args != null){
			recordId = args.getLong("id",0);
			domainName = args.getString("domain_name");
		}
		recordService = new RecordService(getActivity());
        DomainService domainService = new DomainService(getActivity());
		List<Domain> domains = domainService.getAllDomains();
		LayoutInflater inflater;
		builder = new AlertDialog.Builder(getActivity());
		inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_record_create, null);
		final Spinner domainSpinner = (Spinner) view.findViewById(R.id.domainSpinner);
		domainSpinner.setAdapter(new DomainAdapter(getActivity(), domains));
		final Record record = recordService.findById(recordId);
		final Domain domain = domainService.findByDomainName(domainName);
		final Spinner recordTypeSpinner = (Spinner) view.findViewById(R.id.recordTypeSpinner);
		final Spinner dropletSpinner = (Spinner) view.findViewById(R.id.aIpAddressSpinner);
		final DropletService dropletService = new DropletService(getActivity());
		final DropletAdapter dropletAdapter = new DropletAdapter(getActivity(), dropletService.getAllDroplets());
		dropletSpinner.setAdapter(dropletAdapter);
		final RecordTypeAdapter recordTypeAdapter = new RecordTypeAdapter(getActivity());
		recordTypeSpinner.setAdapter(recordTypeAdapter);
		recordTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				LinearLayout aLinearLayout = (LinearLayout) view.findViewById(R.id.aLinearLayout);
				LinearLayout aaaaLinearLayout = (LinearLayout) view.findViewById(R.id.aaaaLinearLayout);
				LinearLayout cnameLinearLayout = (LinearLayout) view.findViewById(R.id.cnameLinearLayout);
				LinearLayout mxLinearLayout = (LinearLayout) view.findViewById(R.id.mxLinearLayout);
				LinearLayout nsLinearLayout = (LinearLayout) view.findViewById(R.id.nsLinearLayout);
				LinearLayout txtLinearLayout = (LinearLayout) view.findViewById(R.id.txtLinearLayout);
				LinearLayout srvLinearLayout = (LinearLayout) view.findViewById(R.id.srvLinearLayout);
				aLinearLayout.setVisibility(View.GONE);
				aaaaLinearLayout.setVisibility(View.GONE);
				cnameLinearLayout.setVisibility(View.GONE);
				mxLinearLayout.setVisibility(View.GONE);
				nsLinearLayout.setVisibility(View.GONE);
				txtLinearLayout.setVisibility(View.GONE);
				srvLinearLayout.setVisibility(View.GONE);
				switch ((Integer)recordTypeAdapter.getItem(position)) {
				case R.drawable.a:
					aLinearLayout.setVisibility(View.VISIBLE);
					break;
				case R.drawable.aaaa:
					aaaaLinearLayout.setVisibility(View.VISIBLE);
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
        if(record != null){
            domainSpinner.setSelection(domains.indexOf(record.getDomain()));
            builder.setTitle(getString(R.string.edit_record));
        }
        else if(domain != null){
            domainSpinner.setSelection(domains.indexOf(domain));
            builder.setTitle(getString(R.string.add_record));
        }

		final EditText aHostnameEditText = (EditText) view.findViewById(R.id.aHostnameEditText);
		final EditText aIpAddressEditText = (EditText) view.findViewById(R.id.aIpAddressEditText);
		final EditText aaaaHostnameEditText = (EditText) view.findViewById(R.id.aaaaHostnameEditText);
		final EditText aaaaIpAddressEditText = (EditText) view.findViewById(R.id.aaaaIpAddressEditText);
		final EditText cnameHostnameEditText = (EditText) view.findViewById(R.id.cnameHostnameEditText);
		final EditText cnameNameEditText = (EditText) view.findViewById(R.id.cnameNameEditText);
		final EditText mxHostnameEditText = (EditText) view.findViewById(R.id.mxHostnameEditText);
		final EditText mxPriorityEditText = (EditText) view.findViewById(R.id.mxPriorityEditText);
		final EditText txtNameEditText = (EditText) view.findViewById(R.id.txtNameEditText);
		final EditText txtTextEditText = (EditText) view.findViewById(R.id.txtTextEditText);
		final EditText srvNameEditText = (EditText) view.findViewById(R.id.srvNameEditText);
		final EditText srvHostnameEditText = (EditText) view.findViewById(R.id.srvHostnameEditText);
		final EditText srvPriorityEditText = (EditText) view.findViewById(R.id.srvPriorityEditText);
		final EditText srvPortEditText = (EditText) view.findViewById(R.id.srvPortEditText);
		final EditText srvWeightEditText = (EditText) view.findViewById(R.id.srvWeightEditText);
		final EditText nsHostnameEditText = (EditText) view.findViewById(R.id.nsHostnameEditText);
		
		dropletSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Droplet droplet = (Droplet)dropletAdapter.getItem(position);
				for(Network network : droplet.getNetworks()) {
				    if(network.getType().equals("public")) {
					aIpAddressEditText.setText(network.getIpAddress());
					break;
				    }
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});

		if(record != null){
			if(record.getRecordType().equals("SRV")){
				recordTypeSpinner.setSelection(1);
				srvNameEditText.setText(record.getName());
				srvHostnameEditText.setText(record.getData());
				srvPriorityEditText.setText(record.getPriority() + "");
				srvPortEditText.setText(record.getPort() + "");
				srvWeightEditText.setText(record.getWeight() + "");
			}else if(record.getRecordType().equals("MX")){
				recordTypeSpinner.setSelection(3);
				mxHostnameEditText.setText(record.getData());
				mxPriorityEditText.setText(record.getPriority() + "");
			}else if(record.getRecordType().equals("NS")){
				recordTypeSpinner.setSelection(0);
				nsHostnameEditText.setText(record.getData());
			}else if(record.getRecordType().equals("CNAME")){
				recordTypeSpinner.setSelection(4);
				cnameNameEditText.setText(record.getName());
				cnameHostnameEditText.setText(record.getData());				
			}else if(record.getRecordType().equals("TXT")){
				recordTypeSpinner.setSelection(2);
				txtNameEditText.setText(record.getName());
				txtTextEditText.setText(record.getData());
			}else if(record.getRecordType().equals("A")){
				recordTypeSpinner.setSelection(5);
				aHostnameEditText.setText(record.getName());
				aIpAddressEditText.setText(record.getData());
			}else if(record.getRecordType().equals("AAAA")){
				recordTypeSpinner.setSelection(6);
				aaaaHostnameEditText.setText(record.getName());
				aaaaIpAddressEditText.setText(record.getData());
			}
		}
		builder.setView(view);
		int positiveString = R.string.edit_record;
		if(record == null)
			 positiveString = R.string.add_record;
		builder.setPositiveButton(positiveString, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				HashMap<String, String> params = new HashMap<String, String>();
				
				/*if(record != null)
					params.put("id", record.getId() + "");*/
				switch ((Integer)recordTypeAdapter.getItem(recordTypeSpinner.getSelectedItemPosition())) {
				case R.drawable.a:
					params.put("type", "A");
					params.put("name", aHostnameEditText.getText().toString());
					params.put("data", aIpAddressEditText.getText().toString());
					break;
				case R.drawable.aaaa:
					params.put("type", "AAAA");
					params.put("name", aaaaHostnameEditText.getText().toString());
					params.put("data", aaaaIpAddressEditText.getText().toString());
					break;
				case R.drawable.cname:
					params.put("type", "CNAME");
					params.put("name", cnameNameEditText.getText().toString());
					params.put("data", cnameHostnameEditText.getText().toString());
					break;
				case R.drawable.mx:
					params.put("type", "MX");
					params.put("data", mxHostnameEditText.getText().toString());
					params.put("priority", mxPriorityEditText.getText().toString());
					break;
				case R.drawable.txt:
					params.put("type", "TXT");
					params.put("name", txtNameEditText.getText().toString());
					params.put("data", txtTextEditText.getText().toString());
					break;
				case R.drawable.srv:
					params.put("type", "SRV");
					params.put("name", srvNameEditText.getText().toString());
					params.put("data", srvHostnameEditText.getText().toString());
					params.put("priority", srvPriorityEditText.getText().toString());
					params.put("port", srvPortEditText.getText().toString());
					params.put("weight", srvWeightEditText.getText().toString());
					break;
				case R.drawable.ns:
					params.put("record_type", "NS");
					params.put("data", nsHostnameEditText.getText().toString());
					break;
				default:
					break;
				}
				long recordId;
				if(record != null){
					recordId = record.getId();
					recordService.updateRecord(((Domain)domainSpinner.getSelectedItem()).getName(), params, recordId, true);
				}else{
					recordService.createRecord(((Domain)domainSpinner.getSelectedItem()).getName(), params, true);
				}
				
			}
		});
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}
}
