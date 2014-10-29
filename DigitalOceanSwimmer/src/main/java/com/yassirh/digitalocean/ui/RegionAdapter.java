package com.yassirh.digitalocean.ui;

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
import com.yassirh.digitalocean.utils.ApiHelper;

import java.util.List;


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
    	return (long)position;
    }
        
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.region_list_row, parent, false);

        final Region region = data.get(position);
        
        TextView nameTextView = (TextView)vi.findViewById(R.id.nameTextView);
        TextView featuresTextView = (TextView)vi.findViewById(R.id.featuresTextView);
        ImageView flagImageView = (ImageView)vi.findViewById(R.id.flagImageView);
        
        flagImageView.setImageResource(ApiHelper.getLocationFlag(region.getName(), region.isAvailable()));
        
        nameTextView.setText(region.getName());    
    	featuresTextView.setText(region.getFeatures().replace(";", ", "));        
        
        return vi;
    }

    public void setData(List<Region> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}