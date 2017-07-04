package com.tm.environmenttm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.tm.environmenttm.R;
import com.tm.environmenttm.SearchLocationActivity;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.EnvironmentCurrent;
import com.tm.environmenttm.model.RealmTM;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {
    private TextView tvTemperature;
    private TextView tvHumidity;
    private TextView tvPressure;
    private TextView tvLight;
    private TextView tvHeatIndex;
    private TextView tvDewPoint;

    private String deviceId;

    private Fragment frgContent;
    private String frgTag;

    private Device device;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ---------------------------------");
        View view;
        device = findDeviceSaveRealm();
        setHasOptionsMenu(true);
        if (device == null) {
            view = inflater.inflate(R.layout.fragment_non_device, container, false);
            ConstantFunction.changeTitleBar(getActivity(), "");
        } else {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            ConstantFunction.changeTitleBar(getActivity(), device.getLocation());

            tvTemperature = (TextView) view.findViewById(R.id.tvTemperature);
            tvHumidity = (TextView) view.findViewById(R.id.tvHumidity);
            tvPressure = (TextView) view.findViewById(R.id.tvPressure);
            tvLight = (TextView) view.findViewById(R.id.tvLight);
            tvHeatIndex = (TextView) view.findViewById(R.id.tvHeatIndex);
            tvDewPoint = (TextView) view.findViewById(R.id.tvDewPoint);

            deviceId = device.getDeviceId();
            loadEnvironmentDevice(deviceId);
        }
        return view;
    }

    private Device findDeviceSaveRealm() {
        return (Device) RealmTM.INSTANT.findFirst(Device.class);
    }


    private void loadEnvironmentDevice(String deviceId) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<String> call = iServices.getEnvironmentCurrent(deviceId);
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    EnvironmentCurrent environmentCurrent = gson.fromJson(response.body(), EnvironmentCurrent.class);
                    tvTemperature.setText(environmentCurrent.getT() + "");
                    tvHumidity.setText(environmentCurrent.getH() + "");
                    tvPressure.setText(environmentCurrent.getPa() + "");
                    tvLight.setText(environmentCurrent.getLa() + "");
                    tvHeatIndex.setText(environmentCurrent.getHi() + "");
                    tvDewPoint.setText(environmentCurrent.getDp() + "");
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                Intent intent = new Intent(getContext(), SearchLocationActivity.class);
                startActivity(intent);
                break;
            case R.id.action_real_time:
                if (device == null) {
                    ConstantFunction.showToast(getContext(), "no location");
                } else {
                    frgTag = "webview";
                    frgContent = new StatictisFragment();
                    ConstantFunction.replaceFragment(getFragmentManager(), R.id.frgContent, frgContent, frgTag);
                }
                break;
            case R.id.action_control:
                frgTag = ConstantValue.FRG_DEVICE_CONTROLLER;
                frgContent = new DeviceControllerFragment();
                ConstantFunction.replaceFragment(getFragmentManager(), R.id.frgContent, frgContent, frgTag);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
