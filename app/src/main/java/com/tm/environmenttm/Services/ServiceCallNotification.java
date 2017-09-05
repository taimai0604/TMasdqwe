package com.tm.environmenttm.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.PubnubTM;
import com.tm.environmenttm.model.RealmTM;

/**
 * Created by taima on 07/10/2017.
 */

public class ServiceCallNotification extends Service {
    private String TAG = this.getClass().getName();
    private Device device;

    public ServiceCallNotification() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        device = (Device) RealmTM.INSTANT.findFirst(Device.class);
        PubnubTM.INSTANT.subChannel(this, device, ConstantValue.CHANNEL_NOTIFICATION_TEMP + "-" + device.getDeviceId());
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        device = (Device) RealmTM.INSTANT.findFirst(Device.class);
        PubnubTM.INSTANT.unsubChannel(this, device, ConstantValue.CHANNEL_NOTIFICATION_TEMP + "-" + device.getDeviceId());
        PubnubTM.INSTANT.updateDevice(device);
    }
}
