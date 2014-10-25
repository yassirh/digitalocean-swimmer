package com.yassirh.digitalocean.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Domain;


public class DomainAdapter extends BaseAdapter {
    
    private List<Domain> data;
    private static LayoutInflater inflater=null;
    
    public DomainAdapter(Context context, List<Domain> data) {
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
    	return (long)position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.domain_list_row, parent, false);

        final Domain domain = data.get(position);
        TextView domainTextView = (TextView)vi.findViewById(R.id.domainTextView);
        TextView ttlTextView = (TextView)vi.findViewById(R.id.ttlTextView);
        
        domainTextView.setText(domain.getName());
        ttlTextView.setText("ttl : " + domain.getTtl());
        
        return vi;
    }
}