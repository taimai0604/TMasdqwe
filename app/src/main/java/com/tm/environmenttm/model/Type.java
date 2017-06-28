package com.tm.environmenttm.model;

/**
 * Created by taima on 06/27/2017.
 */

public class Type {
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
