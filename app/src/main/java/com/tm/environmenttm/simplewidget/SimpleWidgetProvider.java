package com.tm.environmenttm.simplewidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

import com.tm.environmenttm.R;
import com.tm.environmenttm.notification.MyBroadcastReceiver;

import java.util.Random;

public class SimpleWidgetProvider extends AppWidgetProvider {
    public static String CLOCK_UPDATE = "com.tm.environmenttm.simplewidget.CLOCK_UPDATE";
    public static String UPDATE_PREFERENCES = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS";
    private PendingIntent pendingIntent, pendingIntentNoti;

    AlarmManager alarmManager;
    PendingIntent pendingIntentwakeup;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // Get the widget manager and ids for this widget provider, then call the shared clock update method.
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (UPDATE_PREFERENCES.equals(intent.getAction())) {
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID : ids) {
                updateClock(context, appWidgetManager, appWidgetID);
                Log.i("UPDATE_PREFERENCES", appWidgetID + "==");
            }
        }
        // Clock Update Event
        if (CLOCK_UPDATE.equals(intent.getAction())) {
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID : ids) {
                updateClock(context, appWidgetManager, appWidgetID);
                Log.i("CLOCK_UPDATE", appWidgetID + "==");

            }
        }
    }

    public static void updateClock(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Get a reference to our Remote View
        AppWidgetProviderInfo appInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), appInfo.initialLayout);

        // Update the time text
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        int hour = today.hour;
        int minute = today.minute;
        int second = today.second;
        Log.i("updateClock", second + "==");
        // Apply the time to the views
        views.setTextViewText(R.id.timestamp, "" + hour + ":" + minute + ":" + second);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            String number = String.format("%03d", (new Random().nextInt(900) + 100));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.temp, number);

            Intent intent = new Intent(context, SimpleWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //set event on click
            remoteViews.setOnClickPendingIntent(R.id.temp, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        //B1: cap nhat thoi gian
        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent i = new Intent(context, UpdateService.class);

        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000, pendingIntent);

        //B2: TEST NOTI
        final AlarmManager managerNoti = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent iNoti = new Intent(context, MyBroadcastReceiver.class);
        pendingIntentNoti= PendingIntent.getBroadcast(context,
                0, iNoti, 0);
        managerNoti.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60000, pendingIntentNoti);

       /* //B3: NOTI
        //cho thông bao hang ngay luc 16h40
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        // c.set(Calendar.HOUR_OF_DAY, 11);
        // c.set(Calendar.MINUTE, 26);
        c.set(Calendar.HOUR_OF_DAY, 2);
        c.set(Calendar.MINUTE, 38);
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        pendingIntentwakeup = PendingIntent.getBroadcast(context,
                0, intent, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentwakeup);
    */}
}
