package com.tm.environmenttm.model;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by taima on 06/23/2017.
 */

public class RealmTM {
    public static RealmTM INSTANT = new RealmTM();
    private Realm realm;

    private RealmTM() {
        this.realm = Realm.getDefaultInstance();
    }

    public void addRealm(RealmObject object) {
        realm.beginTransaction();
        realm.copyToRealm(object);
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
//    public void addAccountRealm(Account account) {
//        realm.beginTransaction();
//        realm.copyToRealm(account);
//        realm.commitTransaction();
//    }
//
//    public Account findOneAccountRealm() {
//        realm.beginTransaction();
//        Account account = realm.where(Account.class).findFirst();
//        realm.commitTransaction();
//        return account;
//    }
//
    public void logout() {
        realm.beginTransaction();
        RealmResults<Account> results = realm.where(Account.class).findAll();
        results.deleteFirstFromRealm();
        realm.commitTransaction();
    }
//
//    public Device findOneDeviceRealm() {
//        realm.beginTransaction();
//        Device result = realm.where(Device.class).findFirst();
//        realm.commitTransaction();
//        return result;
//    }
//
//    public void deleteDeviceAll(){
//        realm.beginTransaction();
//        RealmResults<Device> results = realm.where(Device.class).findAll();
//        results.deleteAllFromRealm();
//        realm.commitTransaction();
//    }
//
//    //    type
//    public List<Type> findTypeDevice() {
//        realm.beginTransaction();
//        List<Type> result = realm.where(Type.class)
//                .findAll();
//        realm.commitTransaction();
//        return result;
//    }
//
//    public void addTypeDevice(List<Type> list){
//        realm.beginTransaction();
//        realm.copyToRealm(list);
//        realm.commitTransaction();
//    }
//
//    public void deleteTypeDeviceAll(){
//        realm.beginTransaction();
//        RealmResults<Type> results = realm.where(Type.class).findAll();
//        results.deleteAllFromRealm();
//        realm.commitTransaction();
//    }
}
