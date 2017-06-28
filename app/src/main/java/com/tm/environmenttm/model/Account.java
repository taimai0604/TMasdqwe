package com.tm.environmenttm.model;

import io.realm.RealmObject;

/**
 * Created by taima on 06/23/2017.
 */

public class Account extends RealmObject {
    private String id;
    private String email;
    private String password;
    private String fullName;
    private boolean active;
    private boolean rule;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRule() {
        return rule;
    }

    public void setRule(boolean rule) {
        this.rule = rule;
    }
}
