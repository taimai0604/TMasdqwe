package com.tm.environmenttm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tm.environmenttm.R;
import com.tm.environmenttm.adapter.CustomListDeviceAdapter;
import com.tm.environmenttm.model.Device;

import java.util.ArrayList;

public class DeviceFragment extends Fragment {
    private ListView lvDevices;

    private ArrayList<Device> dataModels;

    private CustomListDeviceAdapter adapter;

    public DeviceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        dataModels = new ArrayList<>();
        Device device = new Device("asdasd", "asdasd", "bienhoa", true, 10.0, 11.1, "des", "asdasd", "asdasd");
        Device device1 = new Device("123", "456", "bienhoa", false, 10.0, 11.1, "des", "asdasd", "asdasd");
        Device device2 = new Device("321", "654", "bienhoa", true, 10.0, 11.1, "des", "asdasd", "asdasd");
        dataModels.add(device);
        dataModels.add(device1);
        dataModels.add(device2);
        adapter = new CustomListDeviceAdapter(view.getContext(), dataModels);

        lvDevices = (ListView) view.findViewById(R.id.lvDevices);
        lvDevices.setAdapter(adapter);

        return view;
    }


}
