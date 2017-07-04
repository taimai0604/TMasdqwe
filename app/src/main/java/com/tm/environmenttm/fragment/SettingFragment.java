package com.example.my.myapplication.fenjuly.toggleexpandlayout;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.my.myapplication.IOT.materialprofile.InfoDeviceFragment;
import com.example.my.myapplication.IOT.server.models.Device;
import com.example.my.myapplication.IOT.server.service.ServiceControl;
import com.example.my.myapplication.IOT.server.service.ServiceDevice;
import com.example.my.myapplication.R;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class SettingCustomFragment extends Fragment {
    //alert
    final CharSequence myList[] = {"'C", "'K"};
    int position = 0;

    RelativeLayout rl;

    Calendar c = Calendar.getInstance();
    int hourOfDays, minutes;

    TextView unitTemp, unitTempValue, NotificationState, NotificationDailyState, timeNotification, timeNotificationValue, UpdateInfoState, dateNotification, dateNotificationValue, AutoAutoUpdateInfoState, UpdateInfoTime, UpdateInfoTimeValue;
    SwitchButton switch_button_NotificationState, switch_button_NotificationDaily, switch_button_UpdateInfo, switch_button_AutoUpdateInfo;
    private View rootView;

    TextView NotificationControlLedState;
    SwitchButton NotinControlLedState;

    SharedPreferences sharedPreferences;
    ServiceDevice serviceDevice;
    ServiceControl serviceControl;
    Device deviceInfo;

    @Override
    @TargetApi(21)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            rootView = inflater.inflate(R.layout.activity_settingcustomlayout, container, false);
        } catch (InflateException e) {
            Log.e("TAG", "Inflate exception");
        }
        if (serviceDevice == null) {
            serviceDevice = new ServiceDevice();
        }
        if (serviceControl == null) {
            serviceControl = new ServiceControl();
        }
        deviceInfo = InfoDeviceFragment.checkSessionDeviceInfo(getContext(), sharedPreferences);

        if (deviceInfo == null) {
            InfoDeviceFragment.reloadIOTDeviceListFragmentt(getContext());
        } else {
        }

        unitTemp = (TextView) rootView.findViewById(R.id.unitTemp);
        unitTempValue = (TextView) rootView.findViewById(R.id.unitTempValue);

        switch_button_NotificationState = (SwitchButton) rootView.findViewById(R.id.switch_button_NotificationState);
        switch_button_NotificationDaily = (SwitchButton) rootView.findViewById(R.id.switch_button_NotificationDaily);
        switch_button_UpdateInfo = (SwitchButton) rootView.findViewById(R.id.switch_button_UpdateInfo);
        switch_button_AutoUpdateInfo = (SwitchButton) rootView.findViewById(R.id.switch_button_AutoUpdateInfo);
        NotificationState = (TextView) rootView.findViewById(R.id.NotificationState);
        NotificationDailyState = (TextView) rootView.findViewById(R.id.NotificationDailyState);
        timeNotification = (TextView) rootView.findViewById(R.id.timeNotification);
        timeNotificationValue = (TextView) rootView.findViewById(R.id.timeNotificationValue);
        UpdateInfoState = (TextView) rootView.findViewById(R.id.UpdateInfoState);
        dateNotification = (TextView) rootView.findViewById(R.id.dateNotification);
        dateNotificationValue = (TextView) rootView.findViewById(R.id.dateNotificationValue);
        AutoAutoUpdateInfoState = (TextView) rootView.findViewById(R.id.AutoAutoUpdateInfoState);

        UpdateInfoTime = (TextView) rootView.findViewById(R.id.UpdateInfoTime);
        UpdateInfoTimeValue = (TextView) rootView.findViewById(R.id.UpdateInfoTimeValue);

        NotificationControlLedState = (TextView) rootView.findViewById(R.id.NotificationControlLedState);
        NotinControlLedState = (SwitchButton) rootView.findViewById(R.id.switch_button_NotinControlLedState);

        Calendar calanders = Calendar.getInstance();
        int cDay = calanders.get(Calendar.DAY_OF_MONTH);
        int cMonth = calanders.get(Calendar.MONTH) + 1;
        int cYear = calanders.get(Calendar.YEAR);
        String selectedMonth = "" + cMonth;
        String selectedYear = "" + cYear;
        int cHour = calanders.get(Calendar.HOUR);
        int cMinute = calanders.get(Calendar.MINUTE);
        int cSecond = calanders.get(Calendar.SECOND);

        dateNotificationValue.setText(cMonth + " / " + cDay + " / " + cYear);

        //set unit temp
        //alert
        rl = (RelativeLayout) rootView.findViewById(R.id.myRL);
        final AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
        ad.setTitle("Đơn vị nhiệt độ");
        ad.setSingleChoiceItems(myList, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                Toast.makeText(getContext(),
                        "You Choose : " + myList[arg1]
                        ,
                        Toast.LENGTH_LONG).show();
                position = arg1;
            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),
                        "You Have Cancel the Dialog box", Toast.LENGTH_LONG)
                        .show();

            }
        });
        ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                unitTempValue.setText(myList[position]);
            }
        });

        unitTemp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                ad.show();
            }
        });
        //end set unit temp

        switch_button_NotificationState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //  layout.open();
                    switch_button_NotificationDaily.setChecked(isChecked);
                    NotificationState.setText(setState(isChecked));
                    NotificationDailyState.setText(setState(isChecked));

                } else {
                    // layout.close();
                    switch_button_NotificationDaily.setChecked(isChecked);
                    NotificationState.setText(setState(isChecked));
                    NotificationDailyState.setText(setState(isChecked));


                }
            }
        });

        switch_button_NotificationDaily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    NotificationDailyState.setText(setState(isChecked));
                } else {
                    NotificationDailyState.setText(setState(isChecked));
                }
            }
        });


        timeNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), time, c
                        .get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                        true).show();
            }
        });

        dateNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDatePicker(inflater, v);
            }
        });

        switch_button_UpdateInfo.setOnCheckedChangeListener(switchUpdateInfo);

        switch_button_AutoUpdateInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AutoAutoUpdateInfoState.setText(setState(isChecked));
                } else {
                    AutoAutoUpdateInfoState.setText(setState(isChecked));
                }
            }
        });

        UpdateInfoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), timeDelay, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                        false).show();
            }
        });

        NotinControlLedState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean result = false;
                Log.i("check", isChecked + "");
                if (isChecked) {
                    result = controlLed(deviceInfo.getDeviceId(), "on");
                } else {
                    result = controlLed(deviceInfo.getDeviceId(), "off");
                }
                Log.i("result", result + "");

                if (!result) {
                    NotificationControlLedState.setText(setState(!isChecked));
                    NotinControlLedState.setChecked(!isChecked);
                    showDialogError();

                } else {
                    NotificationControlLedState.setText(setState(isChecked));
                    NotinControlLedState.setChecked(isChecked);
                }
            }
        });


        return rootView;
    }

    private boolean controlLed(String deviceId, String state) {
        boolean result = false;
        try {
            result = serviceControl.controlLed(deviceId, state);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean controlTimeDelay(String deviceId, int time) {
        boolean result = false;
        try {
            result = serviceControl.controlTimeDelay(deviceId, time + "");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }


    public void showDialogError() {

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Lỗi");
        alertDialog.setMessage("Vui lòng thử  lại");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    private String setState(boolean state) {
        if (state)
            return getString(R.string.turnon);
        return getString(R.string.turnoff);
    }

    /*
    * Show AlertDialog with date picker.
    */
    public void alertDatePicker(LayoutInflater inflater, View v) {

/*
     * Inflate the XML view. activity_main is in res/layout/date_picker.xml
     */
        View view = inflater.inflate(R.layout.date_picker, null, false);

// the time picker on the alert dialog, this is how to get the value
        final DatePicker myDatePicker = (DatePicker) view.findViewById(R.id.myDatePicker);

// so that the calendar view won't appear
        myDatePicker.setCalendarViewShown(false);

// the alert dialog
        new AlertDialog.Builder(getContext()).setView(view)
                .setTitle("Set Date")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),
                                "You Have Cancel the Dialog box", Toast.LENGTH_LONG)
                                .show();

                    }
                })
                .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {

/*
                     * In the docs of the calendar class, January = 0, so we
                     * have to add 1 for getting correct month.
                     * http://goo.gl/9ywsj
                     */
                        int month = myDatePicker.getMonth() + 1;
                        int day = myDatePicker.getDayOfMonth();
                        int year = myDatePicker.getYear();

                        dateNotificationValue.setText(month + "/" + day + "/" + year);

                        dialog.cancel();

                    }

                }).show();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateNotificationValue.setText(monthOfYear + "/" + dayOfMonth + "/" + year);


        }
    };

    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            Log.e("time", "invoked");
            hourOfDays = hourOfDay;
            minutes = minute;
            updateTime();
        }
    };

    private void updateTime() {
        timeNotificationValue.setText(hourOfDays + ":" + minutes);
    }

    //set time delay
    TimePickerDialog.OnTimeSetListener timeDelay = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            Log.e("time", "invoked");
            hourOfDays = hourOfDay;
            minutes = minute;
            updateTimeDelay();
        }
    };

    private void updateTimeDelay() {
        int time = hourOfDays * 60 * 60 + minutes * 60;
        Log.i("time", time + "");
        if (controlTimeDelay(deviceInfo.getDeviceId(), time)) {
            UpdateInfoTimeValue.setText(hourOfDays + ":" + minutes);
        } else {
            showDialogError();
        }
    }
    //end set time delay
    CompoundButton.OnCheckedChangeListener switchUpdateInfo = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                //  layout.open();
                switch_button_AutoUpdateInfo.setChecked(isChecked);
                UpdateInfoState.setText(setState(isChecked));
                AutoAutoUpdateInfoState.setText(setState(isChecked));

            } else {
                // layout.close();
                switch_button_AutoUpdateInfo.setChecked(isChecked);
                UpdateInfoState.setText(setState(isChecked));
                AutoAutoUpdateInfoState.setText(setState(isChecked));


            }
        }
    };

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}
