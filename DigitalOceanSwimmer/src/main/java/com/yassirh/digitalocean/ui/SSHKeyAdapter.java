package com.yassirh.digitalocean.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.SSHKey;


public class SSHKeyAdapter extends BaseAdapter {
    
    private List<SSHKey> data;
    private static LayoutInflater inflater=null;
    
    public SSHKeyAdapter(Context context, List<SSHKey> data) {
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
            vi = inflater.inflate(R.layout.ssh_key_list_row, parent, false);

        final SSHKey sshKey = data.get(position);
        
        TextView nameTextView = (TextView)vi.findViewById(R.id.nameTextView);
        TextView fingerprintTextView = (TextView)vi.findViewById(R.id.fingerprintTextView);
        
        nameTextView.setText(sshKey.getName());
        fingerprintTextView.setText(sshKey.getFingerprint());
        
        return vi;
    }
}