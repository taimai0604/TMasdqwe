package com.tm.environmenttm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tm.environmenttm.R;
import com.tm.environmenttm.adapter.CustomListDeviceAdapter;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Device;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceFragment extends Fragment {
    private ListView lvDevices;

    private List<Device> dataModels;

    private CustomListDeviceAdapter adapter;

    public DeviceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        lvDevices = (ListView) view.findViewById(R.id.lvDevices);
        loadDevices();

        return view;
    }


    public void loadDevices(){
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<List<Device>> call = iServices.getAllDevice();
        dataModels = new ArrayList<>();
        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                dataModels = response.body();
                adapter = new CustomListDeviceAdapter(getContext(), dataModels);
                lvDevices.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
            }
        });
    }
}
