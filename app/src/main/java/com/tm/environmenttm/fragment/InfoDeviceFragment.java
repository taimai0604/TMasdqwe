package com.tm.environmenttm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tm.environmenttm.R;
import com.tm.environmenttm.model.Device;

public class InfoDeviceFragment extends Fragment {
    private TextView tvNameDevice;
    private TextView tvType;
    private TextView tvDeviceId;
    private TextView tvLocation;
    private TextView tvKeyThingspeak;

    public InfoDeviceFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_info_device, container, false);
        setHasOptionsMenu(true);

        tvNameDevice = (TextView) view.findViewById(R.id.tvNameDevice);
        tvType = (TextView) view.findViewById(R.id.tvType);
        tvDeviceId = (TextView) view.findViewById(R.id.tvDeviceId);
        tvLocation = (TextView) view.findViewById(R.id.tvLocation);
        tvKeyThingspeak = (TextView) view.findViewById(R.id.tvKeyThingspeak);

        Device device = (Device) getArguments().getSerializable("device");
        tvNameDevice.setText(device.getNameDevice());
        tvType.setText(device.getTypeId());
        tvDeviceId.setText(device.getDeviceId());
        tvLocation.setText(device.getLocation());
        tvKeyThingspeak.setText(device.getKeyThingspeak());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_info_device, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
