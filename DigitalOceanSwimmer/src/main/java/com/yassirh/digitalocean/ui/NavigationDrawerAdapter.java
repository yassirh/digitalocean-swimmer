package com.yassirh.digitalocean.ui;

import java.util.ArrayList;
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


public class NavigationDrawerAdapter extends BaseAdapter {
    
	class ItemImage{
		String title;
		int image;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getImage() {
			return image;
		}
		public void setImage(int image) {
			this.image = image;
		}
		public ItemImage(String title, int image) {
			super();
			this.title = title;
			this.image = image;
		}
		
	}
	
    private List<ItemImage> data;
    private static LayoutInflater inflater=null;
    
    public NavigationDrawerAdapter(Activity activity,String navigationTitles[]) {
    	data = new ArrayList<NavigationDrawerAdapter.ItemImage>();
    	int i = 0;
    	for (String navigationTitle : navigationTitles) {
    		if(i == DrawerPositions.DROPLETS_FRAGMENT_POSITION)
    			data.add(new ItemImage(navigationTitle, R.drawable.droplets));
    		else if(i == DrawerPositions.DOMAINS_FRAGMENT_POSITION)
    			data.add(new ItemImage(navigationTitle, R.drawable.domains));
			else if(i == DrawerPositions.IMAGES_FRAGMENT_POSITION)
				data.add(new ItemImage(navigationTitle, R.drawable.images));
			else if(i == DrawerPositions.REGIONS_FRAGMENT_POSITION)
				data.add(new ItemImage(navigationTitle, R.drawable.regions));
			else if(i == DrawerPositions.SIZES_FRAGMENT_POSITION)
				data.add(new ItemImage(navigationTitle, R.drawable.sizes));
			else if(i == DrawerPositions.SSHKEYS_FRAGMENT_POSITION)
				data.add(new ItemImage(navigationTitle, R.drawable.keys));
    		i++;
		}
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
    	// TODO
    	return 0;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.navigation_list_row, parent, false);

        final ItemImage itemImage = data.get(position);
        TextView titleTextView = (TextView)vi.findViewById(R.id.titleTextView);
        ImageView imageImageView = (ImageView)vi.findViewById(R.id.imageImageView);
        
        titleTextView.setText(itemImage.getTitle());
        imageImageView.setImageResource(itemImage.getImage());
        
        return vi;
    }
}