package com.example.my.myapplication.simplewidget;

/**
 * Created by MY on 6/30/2017.
 */


import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

import com.example.my.myapplication.R;

import java.util.Calendar;
import java.util.Date;

public class UpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String time = getCurrentDateTime();
        String today = getCurrentDate();
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget_layout);
        view.setTextViewText(R.id.timestamp, time);
        view.setTextViewText(R.id.weather, today);
        ComponentName theWidget = new ComponentName(this, SimpleWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(theWidget, view);

        return super.onStartCommand(intent, flags, startId);
    }

    private String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int second = c.get(Calendar.SECOND);
        return hour + ":" + minute + ":" + second;
    }

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);
        //return day + "/" + month + "/" + year;

        return  DateFormat.format("MMMM dd, yyyy h:mmaa", new Date()).toString();

    }
}