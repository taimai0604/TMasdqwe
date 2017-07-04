package com.tm.environmenttm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;
import com.tm.environmenttm.model.ResponeBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceControllerFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private SwitchButton sbLedControl;
    private TextView tvLedControl;
    private Device device;

    public DeviceControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstantFunction.changeTitleBar(getActivity(),ConstantValue.TITLE_DEVICE_CONTROL);
        View view = inflater.inflate(R.layout.fragment_device_controller, container, false);
        device = (Device) RealmTM.INSTANT.findFirst(Device.class);
        sbLedControl = (SwitchButton) view.findViewById(R.id.sbLedControl);
        tvLedControl = (TextView) view.findViewById(R.id.tvLedControl);

        sbLedControl.setChecked(true);

        sbLedControl.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sbLedControl:
                String command;
                if (isChecked) {
                    command = ConstantValue.LED_ON;
                } else {
                    command = ConstantValue.LED_OFF;
                }
                ledControl(command);
                break;
        }
    }

    private void ledControl(String command){
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ResponeBoolean> call = iServices.ledControl(device.getDeviceId(),command);
        call.enqueue(new Callback<ResponeBoolean>() {
            @Override
            public void onResponse(Call<ResponeBoolean> call, Response<ResponeBoolean> response) {
                if (response.code() == 200) {
                    ResponeBoolean result = response.body();
                    if(!result.isResult()){
                        ConstantFunction.showToast(getContext(), "control fail");
                    }
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
            }

            @Override
            public void onFailure(Call<ResponeBoolean> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
            }

        });
    }
}
