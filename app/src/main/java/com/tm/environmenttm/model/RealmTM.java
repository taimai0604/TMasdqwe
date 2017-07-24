package com.tm.environmenttm.model;

import com.tm.environmenttm.config.ConfigApp;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by taima on 06/23/2017.
 */

public class RealmTM {
    public static RealmTM INSTANT = new RealmTM();
    public Realm realm;

    private RealmTM() {
        this.realm = Realm.getDefaultInstance();
    }

    public void addRealm(RealmObject object) {
        realm.beginTransaction();
        realm.copyToRealm(object);
        realm.commitTransaction();
    }

    public void updateRealm(RealmObject object){
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();
    }

    public void addListRealm(List list){
        realm.beginTransaction();
        realm.copyToRealm(list);
        realm.commitTransaction();
    }
    public Object findFirst(Class cls){
        realm.beginTransaction();
        Object account = realm.where(cls).findFirst();
        realm.commitTransaction();
        return account;
    }

    public List findAll(Class cls){
        realm.beginTransaction();
        List result = realm.where(cls)
                .findAll();
        realm.commitTransaction();
        return result;
    }

    public void deleteAll(Class cls){
        realm.beginTransaction();
        RealmResults results = realm.where(cls).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }
    public void logout() {
        deleteAll(Account.class);
        deleteAll(Device.class);
        deleteAll(ConfigApp.class);
    }
}
