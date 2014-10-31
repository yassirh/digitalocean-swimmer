package com.yassirh.digitalocean.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Size;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;


public class SizeAdapter extends BaseAdapter {
    
    private List<Size> data;
    private static LayoutInflater inflater=null;
    public SizeAdapter(Activity activity, List<Size> data) {
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
            vi = inflater.inflate(R.layout.size_list_row, parent, false);

        final Size size = data.get(position);
        
        TextView ramcpuTextView = (TextView)vi.findViewById(R.id.ramcpuTextView);
        TextView diskTextView = (TextView)vi.findViewById(R.id.diskTextView);
        TextView transferTextView = (TextView)vi.findViewById(R.id.transferTextView);
        TextView monthlyPriceTextView = (TextView)vi.findViewById(R.id.monthlyPriceTextView);
        TextView hourlyPriceTextView = (TextView)vi.findViewById(R.id.hourlyPriceTextView);
        
        ramcpuTextView.setText(size.getSlug().toUpperCase(Locale.US) + "/" + size.getCpu() + "CPU");
        diskTextView.setText(size.getDisk() +"GB SSD DISK");
        transferTextView.setText(size.getTransfer() + "TB TRANSFER");
        Formatter formatter = new Formatter();
        monthlyPriceTextView.setText("$" + size.getCostPerMonth()+"/mo");
        hourlyPriceTextView.setText("$" + formatter.format("%1.5f",size.getCostPerHour())+"/hour");
        formatter.close();
        
        return vi;
    }


    public void setData(List<Size> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}