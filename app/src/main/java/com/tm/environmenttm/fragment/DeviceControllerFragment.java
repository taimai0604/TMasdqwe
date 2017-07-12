package com.tm.environmenttm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.tm.environmenttm.model.ResponeNumber;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceControllerFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private SwitchButton sbLedControl;
    private TextView tvLedControl;
    private TextView tvValueTimeDelay;
    private TextView tvTitleTimeDelay;
    private Device device;


    public DeviceControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstantFunction.changeTitleBar(getActivity(), ConstantValue.TITLE_DEVICE_CONTROL);
        View view = inflater.inflate(R.layout.fragment_device_controller, container, false);
        device = (Device) RealmTM.INSTANT.findFirst(Device.class);
        sbLedControl = (SwitchButton) view.findViewById(R.id.sbLedControl);
        tvLedControl = (TextView) view.findViewById(R.id.tvNotificationTemp);
        tvValueTimeDelay = (TextView) view.findViewById(R.id.tvValueTimeDelay);
        tvTitleTimeDelay = (TextView) view.findViewById(R.id.tvTitleTimeDelay);

        loadStatusLed();

        loadTimeDelay();


        sbLedControl.setOnCheckedChangeListener(this);
        tvValueTimeDelay.setOnClickListener(this);
        tvTitleTimeDelay.setOnClickListener(this);
        return view;
    }

    private void loadTimeDelay() {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ResponeNumber> call = iServices.getTimeDelay(device.getDeviceId());
        call.enqueue(new Callback<ResponeNumber>() {
            @Override
            public void onResponse(Call<ResponeNumber> call, Response<ResponeNumber> response) {
                if (response.code() == 200) {
                    tvValueTimeDelay.setText(response.body().getResult() + "");
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
            }

            @Override
            public void onFailure(Call<ResponeNumber> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
            }

        });
    }

    private void loadStatusLed() {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ResponeBoolean> call = iServices.isLed(device.getDeviceId());
        call.enqueue(new Callback<ResponeBoolean>() {
            @Override
            public void onResponse(Call<ResponeBoolean> call, Response<ResponeBoolean> response) {
                if (response.code() == 200) {
                    boolean isLed = response.body().isResult();
                    sbLedControl.setChecked(isLed);
                    if (isLed) {
                        tvLedControl.setText(ConstantValue.LED_ON);
                    } else {
                        tvLedControl.setText(ConstantValue.LED_OFF);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sbLedControl:
                String command;
                if (isChecked) {
                    command = ConstantValue.LED_ON;
                    tvLedControl.setText(ConstantValue.LED_ON);
                } else {
                    command = ConstantValue.LED_OFF;
                    tvLedControl.setText(ConstantValue.LED_OFF);
                }
                ledControl(command);
                break;
        }
    }

    private void ledControl(String command) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ResponeBoolean> call = iServices.ledControl(device.getDeviceId(), command);
        call.enqueue(new Callback<ResponeBoolean>() {
            @Override
            public void onResponse(Call<ResponeBoolean> call, Response<ResponeBoolean> response) {
                if (response.code() == 200) {
                    ResponeBoolean result = response.body();
                    if (!result.isResult()) {
                        ConstantFunction.showToast(getContext(), "control fail");
                    }
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
            }

            @Override
            public void onFailure(Call<ResponeBoolean> call, Throwable t) {

            }

        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvTitleTimeDelay:
            case R.id.tvValueTimeDelay:
                new MaterialDialog.Builder(getContext())
                        .title(getResources().getString(R.string.time_delay))
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input(getResources().getString(R.string.time_delay), tvValueTimeDelay.getText(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                tvValueTimeDelay.setText(input);
                                setTimeDelay(Integer.valueOf(input.toString()));
                            }

                            private void setTimeDelay(int timeDelay) {
                                IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
                                Call<ResponeBoolean> call = iServices.setTimeDelay(device.getDeviceId(), timeDelay);
                                final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
                                call.enqueue(new Callback<ResponeBoolean>() {
                                    @Override
                                    public void onResponse(Call<ResponeBoolean> call, Response<ResponeBoolean> response) {
                                        if (response.code() == 200) {
                                            ResponeBoolean result = response.body();
                                            if (!result.isResult()) {
                                                ConstantFunction.showToast(getContext(), "control fail");
                                            }
                                            dialog.dismiss();
                                        } else {
                                            ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponeBoolean> call, Throwable t) {

                                    }

                                });
                            }
                        }).show();
                break;
        }
    }
}
