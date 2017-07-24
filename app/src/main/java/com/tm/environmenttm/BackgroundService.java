package com.tm.environmenttm;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tm.environmenttm.config.ConfigApp;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;
import com.tm.environmenttm.model.ResponeNumber;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by taima on 07/24/2017.
 */

public class BackgroundService extends IntentService {
    private Device device;
    private String TAG = this.getClass().getName();

    public BackgroundService(Device device) {
        super("BackgroundService");
        this.device = device;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ResponeNumber> call = iServices.getLowTemp(device.getDeviceId());
        try {
            Log.d(TAG, "onHandleIntent: --------------- ");
            float result = call.execute().body().getResult();
            ConfigApp configApp = (ConfigApp) RealmTM.INSTANT.findFirst(ConfigApp.class);
            RealmTM.INSTANT.realm.beginTransaction();
            configApp.setLowerTemp(result);
            RealmTM.INSTANT.realm.commitTransaction();
            Log.d(TAG, "onHandleIntent: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}