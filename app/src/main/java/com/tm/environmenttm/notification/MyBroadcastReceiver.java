package com.tm.environmenttm.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.tm.environmenttm.Home;
import com.tm.environmenttm.R;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private String TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
        System.out.println("running MyBroadcastReceiver");
    }

    private void showNotification(Context context) {
        String timeCurrent = SystemClock.elapsedRealtime() + "";
        Device device = (Device) RealmTM.INSTANT.findFirst(Device.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, Home.class), 0);
        NotificationCompat.Builder mBuilder;
        if (device != null) {
            mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_logo)
                            .setContentTitle(device.getLocation())
                            .setContentText(timeCurrent);
        } else {
            mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_logo)
                            .setContentTitle("My notification")
                            .setContentText(timeCurrent);
        }
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
