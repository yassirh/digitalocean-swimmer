package com.yassirh.digitalocean.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.model.Record;
import com.yassirh.digitalocean.service.DomainService;
import com.yassirh.digitalocean.service.RecordService;

public class DomainDetailsDialogFragment extends DialogFragment {


	DomainService mDomainService;
	RecordService mRecordService;
	
	public DomainDetailsDialogFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View vi = inflater.inflate(R.layout.dialog_domain_details, container);
		long id = getArguments().getLong("id");
		mDomainService = new DomainService(getActivity());
		mRecordService = new RecordService(getActivity());
		Domain domain = mDomainService.findById(id);
        RecordAdapter recordAdapter = new RecordAdapter(getActivity(), domain.getRecords());
        
		getDialog().setTitle(domain.getName());
		TextView domainTextView = (TextView)vi.findViewById(R.id.domainTextView);
        TextView ttlTextView = (TextView)vi.findViewById(R.id.ttlTextView);
        ListView recordsListView = (ListView)vi.findViewById(R.id.recordsListView);
        
        domainTextView.setText(domain.getName());
        ttlTextView.setText("ttl : " + domain.getTtl());
        recordsListView.setAdapter(recordAdapter);
        
        registerForContextMenu(recordsListView);
        
		return vi;
	}
	
	private Record mRecord;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.recordsListView) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			mRecord = new RecordService(getActivity()).findById(info.id);
			MenuInflater inflater = getActivity().getMenuInflater();
			inflater.inflate(R.menu.record_context, menu);
			
			OnMenuItemClickListener listener = new OnMenuItemClickListener() {
		        @Override
		        public boolean onMenuItemClick(MenuItem item) {
		            onContextItemSelected(item);
		            return true;
		        }
		    };

		    for (int i = 0, n = menu.size(); i < n; i++)
		        menu.getItem(i).setOnMenuItemClickListener(listener);
		}
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit_record:
				Bundle args = new Bundle();
				args.putLong("id", mRecord.getId());
				FragmentManager fm = getActivity().getSupportFragmentManager();
				RecordCreateDialogFragment recordCreateDialogFragment = new RecordCreateDialogFragment();
				recordCreateDialogFragment.setArguments(args);
				recordCreateDialogFragment.show(fm, "create_record");
				return true;
			case R.id.action_destroy:
				mRecordService.deleteDomainRecord(mRecord.getDomain().getId(), mRecord.getId(), true);
				this.dismiss();
				return true;				
		}
		return false;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}
	
}
