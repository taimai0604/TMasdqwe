package com.tm.environmenttm.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.R;
import com.tm.environmenttm.adapter.CustomListDeviceAdapter;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
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

    public DeviceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstantFunction.changeTitleBar(getActivity(), ConstantValue.TITLE_DEVICE);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        lvDevices = (ListView) view.findViewById(R.id.lvDevices);
        loadDevices();
        return view;
    }


    public void loadDevices() {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<List<Device>> call = iServices.getAllDevice();
        dataModels = new ArrayList<>();
        // Set up progress before call
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
        // show it
        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                if(getContext() != null){
                    dataModels = response.body();
                    CustomListDeviceAdapter adapter = new CustomListDeviceAdapter(getContext(), dataModels);
                    lvDevices.setAdapter(adapter);
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_device, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_device) {
            Fragment fragment = new AddDeivceFragment();
            ConstantFunction.replaceFragment(getFragmentManager(), R.id.frgContent, fragment, ConstantValue.FRG_ADD_DEVICE);
        }

        return super.onOptionsItemSelected(item);
    }

}
