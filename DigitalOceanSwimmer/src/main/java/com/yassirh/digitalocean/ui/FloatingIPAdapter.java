package com.yassirh.digitalocean.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.FloatingIP;
import com.yassirh.digitalocean.model.Size;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;


public class FloatingIPAdapter extends BaseAdapter {

    private List<FloatingIP> data;
    private static LayoutInflater inflater = null;

    public FloatingIPAdapter(Activity activity, List<FloatingIP> data) {
        this.data = data;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.floatingip_list_row, parent, false);

        final FloatingIP floatingIP = data.get(position);

        TextView ipTextView = (TextView) vi.findViewById(R.id.ipTextView);

        ipTextView.setText(floatingIP.getIp());

        return vi;
    }


    public void setData(List<FloatingIP> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}