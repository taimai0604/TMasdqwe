package com.tm.environmenttm.simplewidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.Environment;
import com.tm.environmenttm.model.RealmTM;

import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleWidgetProvider extends AppWidgetProvider {
    public static String UPDATE_PREFERENCES = "android.appwidget.action.APPWIDGET_UPDATE";
    private PendingIntent pendingIntent, pendingIntentNoti;

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
                Log.i("UPDATE_PREFERENCES", "=" + appWidgetID);
            }
        }
    }

    public static void updateClock(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Get a reference to our Remote View
        AppWidgetProviderInfo appInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), appInfo.initialLayout);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.i("updateClock", "=");

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[0];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            Intent intent = new Intent(context, SimpleWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //set event on click
            remoteViews.setOnClickPendingIntent(R.id.temp, pendingIntent);
            //load date
            loadDate(remoteViews);
            // load temp and humidity
            loadTempAndHumidity(remoteViews, appWidgetManager, widgetId);
        }
//        //B1: cap nhat thoi gian
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, UpdateService.class);
        pendingIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                1000, pendingIntent);
    }

    private void loadDate(RemoteViews remoteViews) {
        remoteViews.setTextViewText(R.id.weather, getCurrentDate());
    }

    public String getCurrentDate() {
        return DateFormat.format("MMMM dd, yyyy", new Date()).toString();
    }

    private void loadTempAndHumidity(final RemoteViews remoteViews, final AppWidgetManager appWidgetManager, final int widgetId) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        String query = "{ \"order\": \"datedCreated DESC\" ,  \"limit\": 1 }";
        Device device = (Device) RealmTM.INSTANT.findFirst(Device.class);
        if (device != null) {
            Call<List<Environment>> call = iServices.getInfoEnvironmentByDevice(device.getId(), query);
            call.enqueue(new Callback<List<Environment>>() {

                @Override
                public void onResponse(Call<List<Environment>> call, Response<List<Environment>> response) {
                    Environment environment = response.body().get(0);
                    if (response.code() == 200 && environment != null) {
                        Device device = (Device) RealmTM.INSTANT.findFirst(Device.class);
                        if (device != null) {
//                            Random random = new Random();
//                            int randomvalue = random.nextInt(1000);
//                            remoteViews.setTextViewText(R.id.temp, "r" + String.valueOf(randomvalue));
                            remoteViews.setTextViewText(R.id.tvLocation, device.getLocation());
                            remoteViews.setTextViewText(R.id.temp, String.valueOf(environment.getTempC()));
                            remoteViews.setTextViewText(R.id.humidity, String.valueOf(environment.getHumidity()));
                        }

                        appWidgetManager.updateAppWidget(widgetId, remoteViews);
                    }
                }

                @Override
                public void onFailure(Call<List<Environment>> call, Throwable t) {

                }
            });
        } else {
        }
    }
}
