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
import com.yassirh.digitalocean.model.Region;


public class RegionAdapter extends BaseAdapter {
    
    private List<Region> data;
    private static LayoutInflater inflater=null;
    
    public RegionAdapter(Activity activity, List<Region> data) {
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
            vi = inflater.inflate(R.layout.region_list_row, null);

        final Region region = data.get(position);
        
        TextView nameTextView = (TextView)vi.findViewById(R.id.nameTextView);
        TextView slugTextView = (TextView)vi.findViewById(R.id.slugTextView);
        ImageView flagImageView = (ImageView)vi.findViewById(R.id.flagImageView);
        
        if(region.getName().equals("Amsterdam 1")){
        	flagImageView.setImageResource(R.drawable.nl_flag);
        }
        else{
        	flagImageView.setImageResource(R.drawable.us_flag);
        }
        
        nameTextView.setText(region.getName());    
    	slugTextView.setText(region.getSlug());        
        
        return vi;
    }
}