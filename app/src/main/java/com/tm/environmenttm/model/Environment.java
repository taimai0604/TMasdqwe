package com.tm.environmenttm.model;

import java.util.Date;

/**
 * Created by taima on 06/27/2017.
 */

public class Environment {
    private String id;
    private int tempC;
    private int tempF;
    private int tempK;
    private int dewPoint;
    private int heatIndex;
    private int humidity;
    private int pressure;
    private int lightLevel;
    private Date datedCreated;
    private String deviceIdParticle;
    private String deviceId;

    public Environment() {

    }

    public Environment(String id, int tempC, int tempF, int tempK, int dewPoint, int heatIndex, int humidity, int pressure, int lightLevel, Date datedCreated, String deviceIdParticle, String deviceId) {
        this.id = id;
        this.tempC = tempC;
        this.tempF = tempF;
        this.tempK = tempK;
        this.dewPoint = dewPoint;
        this.heatIndex = heatIndex;
        this.humidity = humidity;
        this.pressure = pressure;
        this.lightLevel = lightLevel;
        this.datedCreated = datedCreated;
        this.deviceIdParticle = deviceIdParticle;
        this.deviceId = deviceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTempC() {
        return tempC;
    }

    public void setTempC(int tempC) {
        this.tempC = tempC;
    }

    public int getTempF() {
        return tempF;
    }

    public void setTempF(int tempF) {
        this.tempF = tempF;
    }

    public int getTempK() {
        return tempK;
    }

    public void setTempK(int tempK) {
        this.tempK = tempK;
    }

    public int getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(int dewPoint) {
        this.dewPoint = dewPoint;
    }

    public int getHeatIndex() {
        return heatIndex;
    }

    public void setHeatIndex(int heatIndex) {
        this.heatIndex = heatIndex;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getLightLevel() {
        return lightLevel;
    }

    public void setLightLevel(int lightLevel) {
        this.lightLevel = lightLevel;
    }

    public Date getDatedCreated() {
        return datedCreated;
    }

    public void setDatedCreated(Date datedCreated) {
        this.datedCreated = datedCreated;
    }

    public String getDeviceIdParticle() {
        return deviceIdParticle;
    }

    public void setDeviceIdParticle(String deviceIdParticle) {
        this.deviceIdParticle = deviceIdParticle;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
