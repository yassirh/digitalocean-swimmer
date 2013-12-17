package com.yassirh.digitalocean.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Record;
import com.yassirh.digitalocean.utils.ApiHelper;


public class RecordAdapter extends BaseAdapter {
    
    private List<Record> data;
    private static LayoutInflater inflater=null;
    
    public RecordAdapter(Context context, List<Record> data) {
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
            vi = inflater.inflate(R.layout.record_list_row, null);

        final Record record = data.get(position);
        
        TextView nameTextView = (TextView)vi.findViewById(R.id.nameTextView);
        TextView dataTextView = (TextView)vi.findViewById(R.id.dataTextView);
        ImageView recordTypeImageView = (ImageView)vi.findViewById(R.id.recordTypeImageView);
        if(!record.getName().equals("null"))
        	nameTextView.setText(record.getName());
        dataTextView.setText(record.getData());
        recordTypeImageView.setImageResource(ApiHelper.getRecordLabel(record.getRecordType()));
        
        return vi;
    }
}