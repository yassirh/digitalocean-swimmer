package com.yassirh.digitalocean.ui;

import java.util.List;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Image;

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
        
        TextView nameTextView = (TextView)vi.findViewById(R.id.nameTextView);
        ImageView distroImageView = (ImageView)vi.findViewById(R.id.distroImageView);
        TextView slugTextView = (TextView)vi.findViewById(R.id.slugTextView);
        TextView visibilityTextView = (TextView)vi.findViewById(R.id.visibilityTextView);
        
        if(image.getDistribution().equals("Ubuntu")){
        	distroImageView.setImageResource(R.drawable.ubuntu_active);
        }
        else if(image.getDistribution().equals("Debian")){
        	distroImageView.setImageResource(R.drawable.debian_active);
        }
        else if(image.getDistribution().equals("CentOS")){
        	distroImageView.setImageResource(R.drawable.centos_active);
        }
        else if(image.getDistribution().equals("Fedora")){
        	distroImageView.setImageResource(R.drawable.fedora_active);
        }
        else if(image.getDistribution().equals("Arch Linux")){
        	distroImageView.setImageResource(R.drawable.arch_linux_active);
        }
        
        nameTextView.setText(image.getName());
        if(image.getSlug().isEmpty())
        	slugTextView.setVisibility(View.GONE);
        else
        	slugTextView.setText(image.getSlug());
        String visibility = vi.getResources().getString(R.string.public_visibility);
        if(!image.isPublic())
        	visibility = vi.getResources().getString(R.string.private_visibility);
        
        visibilityTextView.setText(visibility);
        
        return vi;
    }
}