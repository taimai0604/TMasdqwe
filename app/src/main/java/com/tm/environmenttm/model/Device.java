package com.tm.environmenttm.model;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by taima on 06/27/2017.
 */

public class Device extends RealmObject implements Serializable {
    private String id;
    private String deviceId;
    private String nameDevice;
    private String location;
    private boolean active;
    private double latitude;
    private double longitude;
    private float channelID;
    private String KeyThingspeak;
    private String description;
    private String typeId;

    public Device() {

    }

    public Device(String id, String deviceId, String nameDevice, String location, boolean active, double latitude, double longitude, float channelID, String keyThingspeak, String description, String typeId) {
        this.id = id;
        this.deviceId = deviceId;
        this.nameDevice = nameDevice;
        this.location = location;
        this.active = active;
        this.latitude = latitude;
        this.longitude = longitude;
        this.channelID = channelID;
        this.KeyThingspeak = keyThingspeak;
        this.description = description;
        this.typeId = typeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNameDevice() {
        return nameDevice;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getKeyThingspeak() {
        return KeyThingspeak;
    }

    public void setKeyThingspeak(String keyThingspeak) {
        KeyThingspeak = keyThingspeak;
    }

    public float getChannelID() {
        return channelID;
    }

    public void setChannelID(float channelID) {
        this.channelID = channelID;
    }
}
