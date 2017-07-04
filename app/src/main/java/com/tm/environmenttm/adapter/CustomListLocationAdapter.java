package com.tm.environmenttm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tm.environmenttm.R;
import com.tm.environmenttm.model.Device;

import java.util.List;

/**
 * Created by taima on 07/02/2017.
 */

public class CustomListLocationAdapter extends ArrayAdapter<Device> {
    private Context context;
    private List<Device> dataSet;

    public CustomListLocationAdapter(Context context, List<Device> data){
        super(context, R.layout.row_item_location, data);
        this.context = context;
        this.dataSet = data;
    }
    // View lookup cache
    private static class ViewHolder {
        TextView tvLocation;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Device device = getItem(position);
        ViewHolder viewHolder;


        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_location, parent, false);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvLocation.setText(device.getLocation());
        return convertView;
    }
}
