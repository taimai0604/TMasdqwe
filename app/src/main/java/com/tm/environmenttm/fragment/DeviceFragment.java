package com.tm.environmenttm.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.Home;
import com.tm.environmenttm.R;
import com.tm.environmenttm.adapter.CustomListDeviceAdapter;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceFragment extends Fragment {
    private ListView lvDevices;
    private SwipeRefreshLayout srlDevices;

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
        srlDevices = (SwipeRefreshLayout) view.findViewById(R.id.srlDevices);

        loadDevices();
        srlDevices.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
                Call<List<Device>> call = iServices.getAllDevice();
                dataModels = new ArrayList<>();
                // show it
                call.enqueue(new Callback<List<Device>>() {
                    @Override
                    public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                        if (getContext() != null) {
                            dataModels = response.body();
                            CustomListDeviceAdapter adapter = new CustomListDeviceAdapter(getContext(), dataModels);
                            lvDevices.setAdapter(adapter);
                            srlDevices.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Device>> call, Throwable t) {
                        srlDevices.setRefreshing(false);
                        ConstantFunction.showToast(getContext(), "error");
                    }
                });
            }
        });
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
                if (getContext() != null) {
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
            ConstantFunction.replaceFragmentHasBackStack(getFragmentManager(), R.id.frgContent, fragment, ConstantValue.FRG_ADD_DEVICE);
        }

        return super.onOptionsItemSelected(item);
    }

}
