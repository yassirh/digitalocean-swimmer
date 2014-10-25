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
            vi = inflater.inflate(R.layout.record_list_row, parent, false);

        final Record record = data.get(position);
        
        TextView nameTextView = (TextView)vi.findViewById(R.id.nameTextView);
        TextView dataTextView = (TextView)vi.findViewById(R.id.dataTextView);
        TextView portTextView = (TextView)vi.findViewById(R.id.portTextView);
        TextView weightTextView = (TextView)vi.findViewById(R.id.weightTextView);
        TextView priorityTextView = (TextView)vi.findViewById(R.id.priorityTextView);
        
        ImageView recordTypeImageView = (ImageView)vi.findViewById(R.id.recordTypeImageView);
        
        nameTextView.setVisibility(View.VISIBLE);
        dataTextView.setVisibility(View.VISIBLE);
        portTextView.setVisibility(View.VISIBLE);
        weightTextView.setVisibility(View.VISIBLE);
        priorityTextView.setVisibility(View.VISIBLE);
        
        if(record.getName().equals("null"))
        	nameTextView.setVisibility(View.GONE);
        else
        	nameTextView.setText(record.getName());
        dataTextView.setText(record.getData());
        
        if(record.getPort() == 0)
        	portTextView.setVisibility(View.GONE);
        else
        	portTextView.setText(record.getPort().toString());
        
        if(record.getPriority() == 0)
        	priorityTextView.setVisibility(View.GONE);
        else
        	priorityTextView.setText(record.getPriority().toString());
        
        if(record.getWeight() == 0)
        	weightTextView.setVisibility(View.GONE);
        else
        	weightTextView.setText(record.getWeight().toString());
        recordTypeImageView.setImageResource(ApiHelper.getRecordLabel(record.getRecordType()));
        
        return vi;
    }
}
