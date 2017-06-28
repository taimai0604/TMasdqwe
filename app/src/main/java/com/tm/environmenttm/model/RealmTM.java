package com.tm.environmenttm.model;

import io.realm.Realm;
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

    public void addAccountRealm(Account account) {
        realm.beginTransaction();
        realm.copyToRealm(account);
        realm.commitTransaction();
    }

    public Account findOneAccountRealm() {
        realm.beginTransaction();
        Account account = realm.where(Account.class)
                .findFirst();
        realm.commitTransaction();
        return account;
    }

    public void logout() {
        realm.beginTransaction();
        RealmResults<Account> results = realm.where(Account.class).findAll();
        results.deleteFirstFromRealm();
        realm.commitTransaction();
    }
}
