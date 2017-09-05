package com.tm.environmenttm.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.tm.environmenttm.R;
import com.tm.environmenttm.Services.ServiceCallNotification;
import com.tm.environmenttm.config.ConfigApp;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.PubnubTM;
import com.tm.environmenttm.model.RealmTM;
import com.tm.environmenttm.notification.MyBroadcastReceiver;

import java.util.Calendar;

import io.realm.Realm;

public class SettingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private View rootView;

    private TextView tvNotificationTemp;
    private SwitchButton sbNotificationTemp;
    //noti
    SwitchButton sbNotification;
    private PendingIntent pendingIntent;
    //
    private ConfigApp configApp;
    private Realm realm;

    private Device device;

    private final Context context;

    @SuppressLint("ValidFragment")
    public SettingFragment(Context context) {
        this.context = context;
    }

    @SuppressLint("ValidFragment")
    public SettingFragment() {
        context = null;
    }

    @Override
    @TargetApi(21)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ConstantFunction.changeTitleBar(getActivity(), ConstantValue.TITLE_SETTING);
        configApp = (ConfigApp) RealmTM.INSTANT.findFirst(ConfigApp.class);


        realm = Realm.getDefaultInstance();

        rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        device = (Device) RealmTM.INSTANT.findFirst(Device.class);
        if (device != null) {
            PubnubTM.INSTANT.initPubnub(context, device);
        }

        sbNotificationTemp = (SwitchButton) rootView.findViewById(R.id.sbNotificationTemp);
        tvNotificationTemp = (TextView) rootView.findViewById(R.id.tvNotificationTemp);

        if (configApp.isNotificationTemp()) {
            tvNotificationTemp.setText(ConstantValue.LED_ON);
        } else {
            tvNotificationTemp.setText(ConstantValue.LED_OFF);
        }

        sbNotificationTemp.setChecked(configApp.isNotificationTemp());

        sbNotificationTemp.setOnCheckedChangeListener(this);

        //noti
//        sbNotification = (SwitchButton) rootView.findViewById(R.id.sbNotification);
//        sbNotification.setChecked(configApp.isNotification());
//        sbNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                realm.beginTransaction();
//                configApp.setNotification(isChecked);
//                realm.commitTransaction();
//
//                checkService(configApp.isNotification());
//
//            }
//        });

        Intent alarmIntent = new Intent(context, MyBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

//        checkService(configApp.isNotification());
        //
        return rootView;
    }

//    private void checkService(boolean notification) {
//        if (notification) {
//            start();
//        } else
//            cancel();
//    }
//
//    public void start() {
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        int interval = 1000;
//        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
//        Log.e("Alarm", "started");
//    }
//
//    public void cancel() {
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        manager.cancel(pendingIntent);
//        Log.e("Alarm", "Canceled");
//    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (device != null) {
            realm.beginTransaction();
            configApp.setNotificationTemp(isChecked);
            realm.commitTransaction();
            // sub pubnub
            if (isChecked) {
                context.startService(new Intent(context, ServiceCallNotification.class));
            } else {
                context.stopService(new Intent(context, ServiceCallNotification.class));
            }
        } else {
            ConstantFunction.showToast(context, "no device!!");
        }

        if (isChecked) {
            tvNotificationTemp.setText(ConstantValue.LED_ON);
        } else {
            tvNotificationTemp.setText(ConstantValue.LED_OFF);
        }

    }
}
