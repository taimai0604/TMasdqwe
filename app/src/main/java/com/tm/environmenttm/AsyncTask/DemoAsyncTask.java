package com.tm.environmenttm.AsyncTask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.tm.environmenttm.Services.ServiceCallNotification;
import com.tm.environmenttm.config.ConfigApp;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.ResponeNumber;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by taima on 09/05/2017.
 */

public class DemoAsyncTask extends AsyncTask<Device, Boolean, ConfigApp> {
    private Context context;
    public DemoAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ConfigApp doInBackground(Device... params) {
        ConfigApp result = new ConfigApp();
        result.setLowerTemp(ConstantValue.LOWER_TEMP);
        result.setLowerTemp(ConstantValue.UPPER_TEMP);
        Device device = params[0];

        context.stopService(new Intent(context, ServiceCallNotification.class));

        try {
            IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
            Call<ResponeNumber> call = iServices.getLowTemp(device.getDeviceId());
            result.setLowerTemp(call.execute().body().getResult());
            call = iServices.getHeightTemp(device.getDeviceId());
            result.setUpperTemp(call.execute().body().getResult());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}