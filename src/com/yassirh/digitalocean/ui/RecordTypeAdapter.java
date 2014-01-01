package com.yassirh.digitalocean.ui;

import java.util.HashMap;

import com.yassirh.digitalocean.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


public class RecordTypeAdapter extends BaseAdapter {

    public static HashMap<String, Integer> sData = new HashMap<String, Integer>();
    
    static{
    	sData.put("ns", R.drawable.ns);
        sData.put("srv", R.drawable.srv);
        sData.put("txt", R.drawable.txt);
        sData.put("mx", R.drawable.mx);
        sData.put("cname", R.drawable.cname);
        sData.put("a",R.drawable.a);
    }
    
    private String[] mKeys;
    private static LayoutInflater mInflater=null;
    
    public RecordTypeAdapter(Activity activity){
        mKeys = sData.keySet().toArray(new String[sData.size()]);
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return sData.size();
    }

    @Override
    public Object getItem(int position) {
        return sData.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
    	 View vi=convertView;
         if(convertView==null)
             vi = mInflater.inflate(R.layout.record_type_list_row, null);
         
        ImageView recordTypeImageView = (ImageView) vi.findViewById(R.id.recordTypeImageView);
        recordTypeImageView.setImageResource((Integer)getItem(pos));
        
        return vi;
    }
}