package com.tm.environmenttm.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.tm.environmenttm.LoginActivity;
import com.tm.environmenttm.R;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager mNM;
        mNM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.mipmap.wakeup, "Test Alarm",
                System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, LoginActivity.class), 0);
        notification = new Notification.Builder(context)
                .setContentTitle("AlarmManagerScheduled")
                .setContentText("This is a test message!")
                .setSmallIcon(R.mipmap.wakeup)
                .setContentIntent(contentIntent).setAutoCancel(true)// auto closed noti
                .build();

        mNM.notify(0, notification);
//        Toast.makeText(context, "Don't panik but your time is up!!!!.", Toast.LENGTH_LONG).show();

    }
}
