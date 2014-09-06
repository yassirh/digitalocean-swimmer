package com.yassirh.digitalocean.ui;

import java.util.List;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.utils.ApiHelper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ImageAdapter extends BaseAdapter {
    
    private List<Image> data;
    private static LayoutInflater inflater=null;
    
    public ImageAdapter(Activity activity, List<Image> data) {
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
            vi = inflater.inflate(R.layout.image_list_row, null);
        
        final Image image = data.get(position);
        
        if(image.getId() == 0){
        	vi = inflater.inflate(R.layout.image_list_header, null);
        	TextView listHeaderTextView = (TextView)vi.findViewById(R.id.listHeaderTextView);
        	listHeaderTextView.setText(image.getName());
        	vi.setOnClickListener(null);
        }else{
        	if(vi.findViewById(R.id.nameTextView) == null){
	        	vi = inflater.inflate(R.layout.image_list_row, null);
	        }
	        TextView nameTextView = (TextView)vi.findViewById(R.id.nameTextView);
	        ImageView distroImageView = (ImageView)vi.findViewById(R.id.distroImageView);
	        TextView visibilityTextView = (TextView)vi.findViewById(R.id.visibilityTextView);	        
	        distroImageView.setImageResource(ApiHelper.getDistributionLogo(image.getDistribution(), "active"));
	        
	        nameTextView.setText(image.getName());
	        String visibility = vi.getResources().getString(R.string.public_visibility);
	        if(!image.isPublic())
	        	visibility = vi.getResources().getString(R.string.private_visibility);
	        
	        visibilityTextView.setText(visibility);
        }
        return vi;
    }
}