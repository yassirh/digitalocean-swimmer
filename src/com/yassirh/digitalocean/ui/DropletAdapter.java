package com.yassirh.digitalocean.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.utils.ApiHelper;


public class DropletAdapter extends BaseAdapter {
    
    private List<Droplet> data;
    private static LayoutInflater inflater=null;
    
    public DropletAdapter(Activity activity, List<Droplet> data) {
        this.data=data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            vi = inflater.inflate(R.layout.droplet_list_row, null);

        final Droplet droplet = data.get(position);
        
        TextView nameTextView = (TextView)vi.findViewById(R.id.nameTextView);
        TextView ipAddressTextView = (TextView)vi.findViewById(R.id.ipAddressTextView);
        TextView statusTextView = (TextView)vi.findViewById(R.id.statusTextView);
        ImageView distroImageView = (ImageView)vi.findViewById(R.id.distroImageView);
        ImageView flagImageView = (ImageView)vi.findViewById(R.id.flagImageView);
        
        flagImageView.setImageResource(ApiHelper.getLocationFlag(droplet.getRegion().getName()));
    	distroImageView.setImageResource(ApiHelper.getDistributionLogo(droplet.getImage().getDistribution(), droplet.getStatus()));
        nameTextView.setText(droplet.getName());
        ipAddressTextView.setText(droplet.getIpAddress());
        statusTextView.setText(droplet.getStatus());
        
        return vi;
    }
}