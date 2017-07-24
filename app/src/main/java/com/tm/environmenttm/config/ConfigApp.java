package com.tm.environmenttm.config;


import io.realm.RealmObject;

/**
 * Created by taima on 06/29/2017.
 */

public class ConfigApp extends RealmObject {
    private boolean notificationTemp;
    private boolean notification;

    private float upperTemp;
    private float lowerTemp;

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

    public float getUpperTemp() {
        return upperTemp;
    }

    public void setUpperTemp(float upperTemp) {
        this.upperTemp = upperTemp;
    }

    public float getLowerTemp() {
        return lowerTemp;
    }

    public void setLowerTemp(float lowerTemp) {
        this.lowerTemp = lowerTemp;
    }
}
 