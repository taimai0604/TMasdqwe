package com.tm.environmenttm.CustomModel;

import com.tm.environmenttm.model.Account;

import java.io.Serializable;

/**
 * Created by taima on 06/29/2017.
 */

public class ResponeUserLogin implements Serializable {
    private String id;
    private Account user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }
}
