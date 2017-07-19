package com.tm.environmenttm.config;


import io.realm.RealmObject;

/**
 * Created by taima on 06/29/2017.
 */

public class ConfigApp extends RealmObject{
    private boolean notificationTemp;
    private boolean notification;

    public boolean isNotificationTemp() {
        return notificationTemp;
    }

    public void setNotificationTemp(boolean notificationTemp) {
        this.notificationTemp = notificationTemp;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}
