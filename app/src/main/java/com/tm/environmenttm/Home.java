package com.tm.environmenttm;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.fragment.DeviceFragment;
import com.tm.environmenttm.fragment.HomeFragment;
import com.tm.environmenttm.fragment.LoginFragment;
import com.tm.environmenttm.fragment.SettingFragment;
import com.tm.environmenttm.fragment.StatictisFragment;
import com.tm.environmenttm.map.TestMapFragment;
import com.tm.environmenttm.model.Account;
import com.tm.environmenttm.model.RealmTM;

import io.realm.Realm;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = this.getClass().getName();

    public Realm realm;
    public Account account;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment frgContent;
    private String frgTag;

    private TextView tvFullName;
    private TextView tvEmail;

    private boolean refresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        tvFullName = (TextView) header.findViewById(R.id.tvFullName);
        tvEmail = (TextView) header.findViewById(R.id.tvEmail);

        //get account
        account = (Account) RealmTM.INSTANT.findFirst(Account.class);


        tvFullName.setText(account.getFullName());
        tvEmail.setText(account.getEmail());

        // manager fragment
        fragmentManager = getSupportFragmentManager();

        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        frgContent = new HomeFragment();
        addFragment(frgContent, ConstantValue.FRG_HOME);
        refresh = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            frgTag = getResources().getString(R.string.home);
            frgContent = new HomeFragment();
            replaceFragment(frgContent, frgTag);
        } else if (id == R.id.nav_device) {
            frgTag = getResources().getString(R.string.device);
            frgContent = new DeviceFragment();
            replaceFragment(frgContent, frgTag);
        } else if (id == R.id.nav_map) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            frgTag = "map";
            frgContent = new TestMapFragment();
            replaceFragment(frgContent, frgTag);
        } else if (id == R.id.nav_setting) {
            frgTag = "setting";
            frgContent = new SettingFragment();
            replaceFragment(frgContent, frgTag);
        } else if (id == R.id.nav_logout) {
            RealmTM.INSTANT.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    private void addFragment(Fragment fragment, String tag) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frgContent, fragment, tag);
        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment, String tag) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frgContent, fragment, tag);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ConstantFunction.isLogin()) {
            finish();
        } else {
            if (refresh) {
                replaceFragment(new HomeFragment(), ConstantValue.FRG_HOME);
            }
            refresh = true;
        }
    }
}
