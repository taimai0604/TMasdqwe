package com.tm.environmenttm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tm.environmenttm.R;
import com.tm.environmenttm.model.Environment;

import java.util.List;

/**
 * Created by taima on 08/01/2017.
 */

public class CustomListViewDataAdapter extends ArrayAdapter<Environment> {
    private Context context;
    private List<Environment> dataSet;

    // View lookup cache
    private static class ViewHolder {
        TextView tvLocation;
        TextView tvContent1;
        TextView tvContent2;
        TextView tvContent3;
    }

    public CustomListViewDataAdapter(@NonNull Context context, List<Environment> data) {
        super(context, R.layout.row_item_view_data, data);
        this.context = context;
        this.dataSet = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Environment environment = getItem(position);
        ViewHolder viewHolder;


        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_view_data, parent, false);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.tvContent1 = (TextView) convertView.findViewById(R.id.tvContentEnvironment1);
            viewHolder.tvContent2 = (TextView) convertView.findViewById(R.id.tvContentEnvironment2);
            viewHolder.tvContent3 = (TextView) convertView.findViewById(R.id.tvContentEnvironment3);

            viewHolder.tvLocation.setText(DateFormat.format("dd/MM/yyyy HH:mm:ss", environment.getDatedCreated()).toString());
            viewHolder.tvContent1.setText("Temperature: " + environment.getTempC() + "; Humidity: " + environment.getHumidity());
            viewHolder.tvContent2.setText("Pressure: " + environment.getPressure() + "; Light: " + environment.getLightLevel());
            viewHolder.tvContent3.setText("Heat index: " + environment.getHeatIndex() + "; Dew point: " + environment.getDewPoint());
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
}
