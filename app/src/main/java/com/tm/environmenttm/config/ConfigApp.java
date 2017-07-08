package com.tm.environmenttm.config;


import io.realm.RealmObject;

/**
 * Created by taima on 06/29/2017.
 */

public class ConfigApp extends RealmObject{
    private boolean notificationTemp;

    public boolean isNotificationTemp() {
        return notificationTemp;
    }

    public void setNotificationTemp(boolean notificationTemp) {
        this.notificationTemp = notificationTemp;
    }
}
