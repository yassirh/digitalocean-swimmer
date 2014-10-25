package com.yassirh.digitalocean.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Account;


public class AccountAdapter extends BaseAdapter {
    
    private List<Account> data;
    private static LayoutInflater inflater=null;
    
    public AccountAdapter(Context context, List<Account> data) {
        this.data=data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
    	return data.get(position).getId();
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.account_list_row, parent, false);

        Account account = data.get(position);
        
        TextView nameTextView = (TextView)vi.findViewById(R.id.nameTextView);
                
        nameTextView.setText(account.getName());
        
        return vi;
    }
}