package com.tm.environmenttm.config;

import android.app.Application;

import com.github.mikephil.charting.utils.Utils;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.RealmTM;
import com.tm.environmenttm.model.Type;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfigActivity  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //init realm
        Realm.init(this);

        // initialize the utilities
        Utils.init(this);

    }


}