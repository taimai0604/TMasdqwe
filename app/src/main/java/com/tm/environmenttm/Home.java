package com.tm.environmenttm;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tm.environmenttm.Services.ServiceCallNotification;
import com.tm.environmenttm.config.ConfigApp;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.fragment.DeviceFragment;
import com.tm.environmenttm.fragment.HomeFragment;
import com.tm.environmenttm.fragment.SettingFragment;
import com.tm.environmenttm.map.TestMapFragment;
import com.tm.environmenttm.model.Account;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.PubnubTM;
import com.tm.environmenttm.model.RealmTM;
import com.tm.environmenttm.notification.MyBroadcastReceiver;

import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private String TAG = this.getClass().getName();

    public Realm realm;
    public Account account;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment frgContent;
    private String frgTag;

    private TextView tvFullName;
    private TextView tvEmail;
    private ImageView imgAvatar;

    private Context context = this;

    public static boolean refresh = false;

    private PendingIntent pendingIntent;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //get account
        account = (Account) RealmTM.INSTANT.findFirst(Account.class);
        if(account.isRule() == false){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_home_client_drawer);
        }

        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        tvFullName = (TextView) header.findViewById(R.id.tvFullName);

        tvEmail = (TextView) header.findViewById(R.id.tvEmail);
        imgAvatar = (ImageView) header.findViewById(R.id.imgAvatar);

        device = (Device) RealmTM.INSTANT.findFirst(Device.class);


        tvFullName.setText(account.getFullName());
        tvEmail.setText(account.getEmail());

        tvFullName.setOnClickListener(this);
        tvEmail.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);

        // manager fragment
        fragmentManager = getSupportFragmentManager();

        //init pubnub
        ConfigApp configApp = (ConfigApp) RealmTM.INSTANT.findFirst(ConfigApp.class);
        if (device != null && configApp.isNotificationTemp()) {
            PubnubTM.INSTANT.initPubnub(getApplicationContext(), device);
        }

        if(device != null){
            checkErrorDevice();
        }
        frgContent = new HomeFragment();
        addFragment(frgContent, ConstantValue.FRG_HOME);
        refresh = false;
    }

    private void checkErrorDevice() {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<Device> call = iServices.getDeviceById(device.getId());
        // show it
        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                if(response.body() == null){
                    RealmTM.INSTANT.deleteAll(Device.class);
                }
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {
            }
        });
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
//        ConstantFunction.cleanFragment(fragmentManager);
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            frgTag = ConstantValue.FRG_HOME;
            frgContent = new HomeFragment();
            ConstantFunction.replaceFragment(fragmentManager, R.id.frgContent, frgContent, frgTag);
        } else if (id == R.id.nav_device) {
            frgTag = ConstantValue.FRG_DEVICE;
            frgContent = new DeviceFragment();
            ConstantFunction.replaceFragment(fragmentManager, R.id.frgContent, frgContent, frgTag);
        } else if (id == R.id.nav_map) {
            frgTag = ConstantValue.FRG_MAP;
            frgContent = new TestMapFragment();

            ConstantFunction.replaceFragment(fragmentManager, R.id.frgContent, frgContent, frgTag);
        } else if (id == R.id.nav_setting) {
            frgTag = ConstantValue.FRG_SETTING;
            frgContent = new SettingFragment(getApplicationContext());
            ConstantFunction.replaceFragment(fragmentManager, R.id.frgContent, frgContent, frgTag);
        } else if (id == R.id.nav_logout) {
            // unsub pubnub
            device = (Device) RealmTM.INSTANT.findFirst(Device.class);
            if(device != null){
                Device deviceTmp = new Device();
                deviceTmp.setDeviceId(device.getDeviceId());
                PubnubTM.INSTANT.unsubChannel(this, deviceTmp, ConstantValue.CHANNEL_NOTIFICATION_TEMP + "-" + deviceTmp.getDeviceId());
            }

            RealmTM.INSTANT.logout();
            Intent intent = new Intent(context, LoginActivity.class);
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

//    private void replaceFragment(Fragment fragment, String tag) {
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frgContent, fragment, tag);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ConstantFunction.isLogin()) {
            finish();
        } else {
            if (refresh) {
                ConstantFunction.replaceFragmentHasBackStack(fragmentManager, R.id.frgContent, new HomeFragment(), ConstantValue.FRG_HOME);
                refresh = false;
            }
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }

                //gps
                //end gps

            }
        }
    }

    //    Kiểm tra map
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("GPS is settings")
                        .setMessage("GPS is not enabled. Do you want to go to settings menu?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(Home.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }

                } else {
                }
                return;
            }

        }
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getApplicationContext());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvFullName:
            case R.id.imgAvatar:
            case R.id.tvEmail:
                Intent intent = new Intent(this, PersonalActivity.class);
                startActivity(intent);
                break;
        }
    }
}
