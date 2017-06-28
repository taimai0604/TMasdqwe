package com.tm.environmenttm;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.tm.environmenttm.fragment.DeviceFragment;
import com.tm.environmenttm.fragment.HomeFragment;
import com.tm.environmenttm.map.GPSTracker;
import com.tm.environmenttm.map.TestMapFragment;
import com.tm.environmenttm.model.Account;
import com.tm.environmenttm.model.RealmTM;

import io.realm.Realm;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback {
    private String TAG = this.getClass().getName();

    public Realm realm;
    public Account account;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment frgContent;
    private String frgTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get account
        account = RealmTM.INSTANT.findOneAccountRealm();

        // manager fragment
        fragmentManager = getSupportFragmentManager();

        Fragment first = new HomeFragment();
        addFragment(first, "first");

//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {

//        }

        Log.d(TAG, "onCreate: " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));



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
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
        GPSTracker gps = new GPSTracker(this);

        if (gps.canGetLocation()) {
//            Log.d("toadoLAgps==", "" + latitude);
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
//            Log.d("toadoLONgps==", "" + longitude);
//            String lati = latitude + "";
//            String loti = longitude + "";
//            createSessionVITRIGPS(lati, loti);
            Toast.makeText(
                    this,
                    "Your Location is -\nLat: " + latitude + "\nLong: "
                            + longitude, Toast.LENGTH_LONG).show();
        } else {
            gps.showSettingsAlert();
        }
    }
}
