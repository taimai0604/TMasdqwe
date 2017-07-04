package com.tm.environmenttm.model;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by taima on 06/27/2017.
 */

public class Type extends RealmObject implements Serializable{
    private String id;
    private String nameType;

    public Type(){

    }

    public Type(String id, String nameType) {
        this.id = id;
        this.nameType = nameType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameType() {
        return nameType;
    }

    public void setNameType(String nameType) {
        this.nameType = nameType;
    }
}
