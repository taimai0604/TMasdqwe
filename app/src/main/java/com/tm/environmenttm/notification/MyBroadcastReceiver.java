package com.tm.environmenttm.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.tm.environmenttm.LoginActivity;
import com.tm.environmenttm.R;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private String TAG = this.getClass().getName();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        String timeCurrent = SystemClock.elapsedRealtime()+"";
        Log.d(TAG, "onReceive: " + timeCurrent);
        Device device = (Device) RealmTM.INSTANT.findFirst(Device.class);
        if (device != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, LoginActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_logo)
                            .setContentTitle(device.getLocation())
                            .setContentText(timeCurrent)
                            .setContentIntent(pendingIntent).setAutoCancel(true);

            NotificationManager mNM;
            mNM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            mNM.notify(0, notification.build());
        }
    }
}
