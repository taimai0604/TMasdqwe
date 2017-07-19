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
import com.tm.environmenttm.model.Account;
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
    private TextView tvValueLowTemp;
    private TextView tvTitleLowTemp;
    private TextView tvValueHeightTemp;
    private TextView tvTitleHeightTemp;
    private Device device;
    private Account account;


    public DeviceControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstantFunction.changeTitleBar(getActivity(), ConstantValue.TITLE_DEVICE_CONTROL);
        View view = inflater.inflate(R.layout.fragment_device_controller, container, false);
        device = (Device) RealmTM.INSTANT.findFirst(Device.class);
        account = (Account) RealmTM.INSTANT.findFirst(Account.class);

        sbLedControl = (SwitchButton) view.findViewById(R.id.sbLedControl);
        tvLedControl = (TextView) view.findViewById(R.id.tvNotificationTemp);
        tvValueTimeDelay = (TextView) view.findViewById(R.id.tvValueTimeDelay);
        tvTitleTimeDelay = (TextView) view.findViewById(R.id.tvTitleTimeDelay);

        tvValueLowTemp= (TextView) view.findViewById(R.id.tvValueLowTemp);
        tvTitleLowTemp = (TextView) view.findViewById(R.id.tvTitleLowTemp);

        tvValueHeightTemp = (TextView) view.findViewById(R.id.tvValueHeightTemp);
        tvTitleHeightTemp = (TextView) view.findViewById(R.id.tvTitleHeightTemp);

        loadStatusLed();
        if(account.isRule() == true){
            loadTimeDelay();
        }else{
            tvTitleTimeDelay.setVisibility(View.INVISIBLE);
            tvValueTimeDelay.setVisibility(View.INVISIBLE);
        }

        loadLowTemp();

        loadHeightTemp();


        sbLedControl.setOnCheckedChangeListener(this);
        tvValueTimeDelay.setOnClickListener(this);
        tvTitleTimeDelay.setOnClickListener(this);
        tvValueLowTemp.setOnClickListener(this);
        tvTitleLowTemp.setOnClickListener(this);
        tvValueHeightTemp.setOnClickListener(this);
        tvTitleHeightTemp.setOnClickListener(this);
        return view;
    }

    private void loadHeightTemp() {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ResponeNumber> call = iServices.getHeightTemp(device.getDeviceId());
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
        call.enqueue(new Callback<ResponeNumber>() {
            @Override
            public void onResponse(Call<ResponeNumber> call, Response<ResponeNumber> response) {
                if (response.code() == 200) {
                    tvValueHeightTemp.setText(response.body().getResult() + "");
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponeNumber> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                dialog.dismiss();
            }

        });
    }

    private void loadLowTemp() {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ResponeNumber> call = iServices.getLowTemp(device.getDeviceId());
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
        call.enqueue(new Callback<ResponeNumber>() {
            @Override
            public void onResponse(Call<ResponeNumber> call, Response<ResponeNumber> response) {
                if (response.code() == 200) {
                    tvValueLowTemp.setText(response.body().getResult() + "");
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponeNumber> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                dialog.dismiss();
            }

        });
    }

    private void loadTimeDelay() {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ResponeNumber> call = iServices.getTimeDelay(device.getDeviceId());
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
        call.enqueue(new Callback<ResponeNumber>() {
            @Override
            public void onResponse(Call<ResponeNumber> call, Response<ResponeNumber> response) {
                if (response.code() == 200) {
                    tvValueTimeDelay.setText(response.body().getResult() + "");
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponeNumber> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                dialog.dismiss();
            }

        });
    }

    private void loadStatusLed() {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ResponeBoolean> call = iServices.isLed(device.getDeviceId());
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
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
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponeBoolean> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                dialog.dismiss();
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
                                        } else {
                                            ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                                        }
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponeBoolean> call, Throwable t) {
                                        dialog.dismiss();
                                    }

                                });
                            }
                        }).show();
                break;
            case R.id.tvTitleLowTemp:
            case R.id.tvValueLowTemp:
                new MaterialDialog.Builder(getContext())
                        .title(getResources().getString(R.string.low_temp))
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input(getResources().getString(R.string.low_temp), tvValueLowTemp.getText(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                tvValueLowTemp.setText(input);
                                setTimeDelay(Integer.valueOf(input.toString()));
                            }

                            private void setTimeDelay(int timeDelay) {
                                IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
                                Call<ResponeBoolean> call = iServices.setLowTemp(device.getDeviceId(), timeDelay);
                                final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
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
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponeBoolean> call, Throwable t) {
                                        dialog.dismiss();

                                    }

                                });
                            }
                        }).show();
                break;
            case R.id.tvTitleHeightTemp:
            case R.id.tvValueHeightTemp:
                new MaterialDialog.Builder(getContext())
                        .title(getResources().getString(R.string.height_temp))
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input(getResources().getString(R.string.height_temp), tvValueHeightTemp.getText(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                tvValueHeightTemp.setText(input);
                                setTimeDelay(Integer.valueOf(input.toString()));
                            }

                            private void setTimeDelay(int timeDelay) {
                                IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
                                Call<ResponeBoolean> call = iServices.setHeightTemp(device.getDeviceId(), timeDelay);
                                final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
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
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponeBoolean> call, Throwable t) {
                                        dialog.dismiss();
                                    }

                                });
                            }
                        }).show();
                break;
        }
    }
}
