package com.tm.environmenttm.model;

import java.io.Serializable;

/**
 * Created by taima on 06/27/2017.
 */

public class ChartThingspeak implements Serializable{
    private String id;
    private String name;
    private String content;
    private String description;
    private boolean active;
    private String deviceId;

    public ChartThingspeak(){

    }

    public ChartThingspeak(String id, String name, String content, String description, boolean active, String deviceId) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.description = description;
        this.active = active;
        this.deviceId = deviceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
