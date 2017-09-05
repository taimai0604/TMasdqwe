package com.tm.environmenttm.config;

import android.app.Application;

import com.github.mikephil.charting.utils.Utils;

import io.realm.Realm;


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