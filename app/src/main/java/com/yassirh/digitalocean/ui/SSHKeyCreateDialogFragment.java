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
import android.widget.EditText;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.SSHKey;
import com.yassirh.digitalocean.service.SSHKeyService;

public class SSHKeyCreateDialogFragment extends DialogFragment {
	
	private long mSSHKeyId = 0L;
	private SSHKeyService mSSHKeyService;
	AlertDialog.Builder mBuilder;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		if(args != null){
			mSSHKeyId = args.getLong("ssh_key_id",0);
		}
		mSSHKeyService = new SSHKeyService(getActivity());
		final SSHKey sshKey = mSSHKeyService.findById(mSSHKeyId);
		LayoutInflater inflater;
		mBuilder = new AlertDialog.Builder(getActivity());
		inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_ssh_key_create, null);
		
		final EditText nameEditText = (EditText) view.findViewById(R.id.nameEditText);
		final EditText publicSSHKeyEditText = (EditText) view.findViewById(R.id.publicSSHKeyEditText);
		if(sshKey != null){
			nameEditText.setText(sshKey.getName());
			publicSSHKeyEditText.setText(sshKey.getPublicKey());
		}
		mBuilder.setView(view);
		int positiveString = R.string.edit_ssh_key;
		if(sshKey == null)
			 positiveString = R.string.add_ssh_key;
		mBuilder.setPositiveButton(positiveString, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SSHKey sshKey = new SSHKey();
				sshKey.setName(nameEditText.getText().toString());
				sshKey.setPublicKey(publicSSHKeyEditText.getText().toString());
				if(mSSHKeyId == 0L){
					sshKey.setId(0);
					mSSHKeyService.save(sshKey,false,true);
				}
				else{
					sshKey.setId(mSSHKeyId);
					mSSHKeyService.save(sshKey,true,true);
				}
			}
		});
		mBuilder.setNegativeButton(R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return mBuilder.create();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}
}
