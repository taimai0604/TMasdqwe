package com.tm.environmenttm.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.R;
import com.tm.environmenttm.fragment.InfoDeviceFragment;
import com.tm.environmenttm.model.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taima on 06/27/2017.
 */

public class CustomListDeviceAdapter extends ArrayAdapter<Device> implements View.OnClickListener {
    private List<Device> data;
    private Context context;

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        final Device dataModel = (Device) object;

        switch (v.getId()) {
            case R.id.imgDeleteDevice:
                new MaterialDialog.Builder(v.getContext())
                        .title(v.getResources().getString(R.string.delete))
                        .content(R.string.log_delete)
                        .positiveText(R.string.agree)
                        .negativeText(R.string.disagree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(context, "delete " + dataModel.getNameDevice(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                break;
        }
    }

    // View lookup cache
    private static class ViewHolder {
        TextView tvDeviceId;
        TextView tvNameDevice;
        ImageView info;
        ToggleButton chkState;
    }

    public CustomListDeviceAdapter(Context context, List<Device> data) {
        super(context, R.layout.row_item_device, data);
        this.data = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Device dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_device, parent, false);
            viewHolder.tvDeviceId = (TextView) convertView.findViewById(R.id.tvDeviceId);
            viewHolder.tvNameDevice = (TextView) convertView.findViewById(R.id.tvNameDevice);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.imgDeleteDevice);
            viewHolder.chkState = (ToggleButton) convertView.findViewById(R.id.chkState);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
//        lastPosition = position;

        viewHolder.tvDeviceId.setText(dataModel.getDeviceId());
        viewHolder.tvNameDevice.setText(dataModel.getNameDevice());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        viewHolder.chkState.setChecked(dataModel.isActive());
        viewHolder.chkState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Toast.makeText(context, dataModel.getNameDevice() + " - " +isChecked, Toast.LENGTH_SHORT).show();
            }
        });
        // Return the completed view to render on screen
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDevice(v.getContext(), dataModel);
            }
        });

        return convertView;
    }

    public static void showInfoDevice(Context mContext, Device device) {

        Fragment mFragment;
        mFragment = new InfoDeviceFragment();
        if (mFragment != null) {

            Bundle bundle = new Bundle();

            bundle.putSerializable("device", device);
            bundle.putBoolean("active",
                    (device.isActive()));

            mFragment.setArguments(bundle);

            FragmentManager manager = ((FragmentActivity) mContext).getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frgContent, mFragment).commit();
        }

    }
}
