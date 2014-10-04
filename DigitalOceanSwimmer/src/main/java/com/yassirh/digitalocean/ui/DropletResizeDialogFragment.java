package com.yassirh.digitalocean.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.SizeTable;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.SizeService;

public class DropletResizeDialogFragment extends DialogFragment {


	DropletService mDropletService;
	
	public DropletResizeDialogFragment() {
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View view = inflater.inflate(R.layout.dialog_droplet_resize,null);
	    SizeService sizeService = new SizeService(getActivity());
		getDialog().setTitle(R.string.title_resize_droplet);
		Spinner sizeSpinner = (Spinner)view.findViewById(R.id.sizeSpinner);
		sizeSpinner.setAdapter(new SizeAdapter(getActivity(), sizeService.getAllSizes(SizeTable.MEMORY)));
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
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
