package com.yassirh.digitalocean.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.utils.ApiHelper;

public class AccountCreateDialogFragment extends DialogFragment {
	
	AlertDialog.Builder mBuilder;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater;
		mBuilder = new AlertDialog.Builder(getActivity());
		inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_account_create, null);
		final EditText nameEditText = (EditText) view.findViewById(R.id.accountNameEditText);
		final EditText clientIdEditText = (EditText) view.findViewById(R.id.clientIdEditText);
		final EditText apiKeyEditText = (EditText) view.findViewById(R.id.apiKeyEditText);
		mBuilder.setView(view);
		mBuilder.setTitle(getString(R.string.create_account));
		int positiveString = R.string.save_account;
		mBuilder.setPositiveButton(positiveString, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Account account = new Account();
				account.setName(nameEditText.getText().toString());
				account.setClientId(clientIdEditText.getText().toString());
				account.setApiKey(apiKeyEditText.getText().toString());
				account.setSelected(true);
				ApiHelper.selectAccount(getActivity(),account);
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
}
