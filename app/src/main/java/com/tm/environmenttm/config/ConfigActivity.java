package com.tm.environmenttm.config;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by taima on 06/23/2017.
 */


public class ConfigActivity  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}