package com.yassirh.digitalocean.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.utils.ApiHelper;

public class SwitchAccountDialogFragment extends DialogFragment implements OnItemClickListener {
	
	Long CREATE_NEW_ACCOUNT_ID = -2L;
	Long ADD_EXISTING_ACCOUNT_ID = -1L; 
	List<Account> accounts;
			
	public SwitchAccountDialogFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_switch_account, container);
		accounts = new ArrayList<Account>();
		getDialog().setTitle(getString(R.string.switch_account));
		accounts.add(new Account(ADD_EXISTING_ACCOUNT_ID,getString(R.string.add_existing_account),"","",null,false));
		accounts.add(new Account(CREATE_NEW_ACCOUNT_ID,getString(R.string.create_new_account),"","",null,false));
		accounts.addAll(ApiHelper.getAllAccounts(getActivity()));

        AccountAdapter accountAdapter = new AccountAdapter(getActivity(), accounts);
        ListView accountsListView = (ListView)view.findViewById(R.id.accountsListView);
        accountsListView.setAdapter(accountAdapter);
        accountsListView.setOnItemClickListener(this);
        
		return view;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(id == CREATE_NEW_ACCOUNT_ID){
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.digitalocean.com/?refcode=8ebfd919c850"));
			startActivity(browserIntent);
		}else if(id == ADD_EXISTING_ACCOUNT_ID){
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cloud.digitalocean.com/v1/oauth/authorize?" +
					"client_id=76d93180752d757185733a318cec4af9c2f4c681ac6264924294facab3ea54ff&" +
					"redirect_uri=https://yassirh.com/digitalocean_swimmer/generate_token.php" +
					"&response_type=code&scope=read write"));
			startActivity(intent);
			/*FragmentManager fm = getActivity().getSupportFragmentManager();
			AccountCreateDialogFragment accountCreateDialogFragment = new AccountCreateDialogFragment();
			accountCreateDialogFragment.show(fm, "create_account");*/
		}else{
			ApiHelper.selectAccount(getActivity(), accounts.get(position));
		}
		this.dismiss();
	}
	
}
