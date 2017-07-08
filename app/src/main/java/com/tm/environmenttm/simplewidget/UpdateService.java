package com.tm.environmenttm.simplewidget;

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

public class UpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String today = getCurrentDate();
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget_layout);
        view.setTextViewText(R.id.weather, today);


        ComponentName theWidget = new ComponentName(this, SimpleWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        //   manager.updateAppWidget(theWidget, view);
        loadTempAndHumidity(view, manager, theWidget);

        return super.onStartCommand(intent, flags, startId);
    }

    public String getCurrentDate() {
        return DateFormat.format("MMMM dd, yyyy", new Date()).toString();
    }

    private void loadTempAndHumidity(final RemoteViews remoteViews, final AppWidgetManager appWidgetManager, final ComponentName theWidget) {
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
//                            remoteViews.setTextViewText(R.id.temp, String.valueOf(random.nextInt(1000)));
                            remoteViews.setTextViewText(R.id.tvLocation, device.getLocation());
                            remoteViews.setTextViewText(R.id.temp, String.valueOf(environment.getTempC()));
                            remoteViews.setTextViewText(R.id.humidity, String.valueOf(environment.getHumidity()));
                        }

                        appWidgetManager.updateAppWidget(theWidget, remoteViews);
                    }
                }

                @Override
                public void onFailure(Call<List<Environment>> call, Throwable t) {

                }
            });
        }
    }


}