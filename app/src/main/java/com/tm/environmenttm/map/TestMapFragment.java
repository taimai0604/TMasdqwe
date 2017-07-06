package com.tm.environmenttm.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.fragment.HomeFragment;
import com.tm.environmenttm.fragment.InfoDeviceFragment;
import com.tm.environmenttm.model.AddressGeo;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class TestMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private View rootView;
    private ViewGroup infoWindow;
    private ViewGroup infoWindowGPS;
    private TextView infoTitle;
    private TextView infoSnippet;
    private TextView infoTitleGPS;
    private TextView infoSnippetGPS;
    private Button infoButton1, infoButton2;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private OnInfoWindowElemTouchListener infoButtonListener1;

    //gps
    GPSTracker gps;
    List<Device> listDevice = null;
    double latitude;
    double longitude;
    //end gps
    LatLng locationGPS;
    //zoom
    GoogleApiClient mGoogleApiClient;
    private GoogleMap googleMap;
    LocationRequest mLocationRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstantFunction.changeTitleBar(getActivity(), ConstantValue.TITLE_MAP);
        String strtext = "";
        try {
            strtext = getArguments().getString("message");
            Log.e("TAG", strtext);
        } catch (Exception x) {
        }

        try {
            rootView = inflater.inflate(R.layout.fragment_test_map, container, false);
        } catch (InflateException e) {
            Log.e("TAG", "Inflate exception");
        }

        if (ActivityCompat.checkSelfPermission(getContext()
                , android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("A", "------------------");
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.i("B", "------------------");
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapclickmore);
        mapFragment.getMapAsync(this);
        this.infoWindow = (ViewGroup) inflater.inflate(R.layout.custom_infowindow, null);
        this.infoWindowGPS = (ViewGroup) inflater.inflate(R.layout.custom_info_gps, null);
        return rootView;

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getContext());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public static Device getDeviceByName(List<Device> listDevice, String nameDevice) {
        int len = listDevice.size();
        for (int i = 0; i < len; i++) {
            if (listDevice.get(i).getNameDevice().equals(nameDevice))
                return listDevice.get(i);
        }
        return null;
    }


    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume", "onResume");

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }
                //gps
                gps = new GPSTracker(getContext());

                if (gps.canGetLocation()) {
                    Log.d("toadoLAgps==", "" + latitude);
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    Log.d("toadoLONgps==", "" + longitude);
                    locationGPS = new LatLng(latitude, longitude);
                    Toast.makeText(
                            getContext(),
                            "Your Location is -\nLat: " + latitude + "\nLong: "
                                    + longitude, Toast.LENGTH_LONG).show();
                } else {
                    gps.showSettingsAlert();
                }
                //end gps

            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMapready) {
        googleMap = googleMapready;

        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout) rootView.findViewById(R.id.map_relative_layout);
        mapWrapperLayout.init(googleMap, getPixelsFromDp(getContext(), 39 + 20));
        this.infoTitle = (TextView) infoWindow.findViewById(R.id.nameTxt);
        this.infoSnippet = (TextView) infoWindow.findViewById(R.id.addressTxt);

        this.infoTitleGPS = (TextView) infoWindowGPS.findViewById(R.id.nameTxt);
        this.infoSnippetGPS = (TextView) infoWindowGPS.findViewById(R.id.addressTxt);

        this.infoButton1 = (Button) infoWindow.findViewById(R.id.btnOne);
        this.infoButton2 = (Button) infoWindow.findViewById(R.id.btnTwo);

        this.infoButtonListener1 = new OnInfoWindowElemTouchListener(infoButton1, getResources().getDrawable(R.color.colorPrimary), getResources().getDrawable(R.color.nliveo_white)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                String nameDevice = marker.getTitle();
                Device device = getDeviceByName(listDevice, nameDevice);
                //remove
                RealmTM.INSTANT.deleteAll(Device.class);
                //add new
                RealmTM.INSTANT.addRealm(device);

                ConstantFunction.replaceFragment(getFragmentManager(),R.id.frgContent,new HomeFragment(),ConstantValue.FRG_HOME);

                //set dia diem moi
//                String address = "khu phố 6, Thủ Đức, Hồ Chí Minh, Việt Nam";
//                String url = getUrl(address);
//                Log.d("getLocation", url.toString());
//                FetchUrl FetchUrl = new FetchUrl();
//                FetchUrl.execute(url);
            }
        };
        this.infoButton1.setOnTouchListener(infoButtonListener1);

        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton2, getResources().getDrawable(R.color.colorPrimary), getResources().getDrawable(R.color.nliveo_white)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                String nameDevice = marker.getTitle();
                Device device = getDeviceByName(listDevice, nameDevice);

                Bundle bundle = new Bundle();

                bundle.putSerializable("device", device);
                bundle.putBoolean("active",(device.isActive()));
                Fragment fragment = new InfoDeviceFragment();
                fragment.setArguments(bundle);

                ConstantFunction.replaceFragment(getFragmentManager(),R.id.frgContent, fragment, ConstantValue.FRG_INFO_DEVICE);
            }
        };
        infoButton2.setOnTouchListener(infoButtonListener);
        googleMap.addMarker(new MarkerOptions()
                .position(locationGPS)
                .title("THIS IS MY")
                .snippet(locationGPS.latitude + " : " + locationGPS.longitude)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                if(marker.getPosition().latitude == locationGPS.latitude &&
                        marker.getPosition().longitude == locationGPS.longitude){
                    infoTitleGPS.setText(marker.getTitle());
                    infoSnippetGPS.setText(marker.getSnippet());
                    mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindowGPS);
                    return infoWindowGPS;
                }else{
                    infoTitle.setText(marker.getTitle());
                    infoSnippet.setText(marker.getSnippet());
                    infoButtonListener.setMarker(marker);
                    infoButtonListener1.setMarker(marker);
                    mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                    return infoWindow;
                }
            }
        });
        //load du lieu

        loadDevice();

        ///Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void loadDevice() {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<List<Device>> call = iServices.getAllDevice();
        // Set up progress before call
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
        // show it
        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                if(getContext() != null){
                    listDevice = response.body();
                    for (int i = 0; i < listDevice.size(); i++) {
                        Log.i("ListDEVICEDir", listDevice.get(i).getNameDevice() + " - " + listDevice.get(i).getLatitude() + " : " + listDevice.get(i).getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(listDevice.get(i).getLatitude(), listDevice.get(i).getLongitude()))
                                .title(listDevice.get(i).getNameDevice())
                                .snippet(listDevice.get(i).getLocation())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationGPS, 10));
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //error
            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /*Lay thong tin dia diem*/
    public static String getUrl(String address) {
        //  https://maps.googleapis.com/maps/api/geocode/json?address=Khu%20ph%E1%BB%91%206-%20Ph%C6%B0%E1%BB%9Dng%20Linh%20Trung%20-%20Qu%E1%BA%ADn%20Th%E1%BB%A7%20%C4%90%E1%BB%A9c,%20H%E1%BB%93%20Ch%C3%AD%20Minh,%20Vi%E1%BB%87t%20Nam&key=AIzaSyDD9PhWNv_d8TEczH0l5HjkwNcmj90JwWM
        String urladdress = "";
        try {
            urladdress = URLEncoder.encode(address, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String key = "&key=AIzaSyDD9PhWNv_d8TEczH0l5HjkwNcmj90JwWM";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + urladdress + key;
        return url;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            List<HashMap<String, String>> addressPlacesList = null;
            DataParserLocation dataParser = new DataParserLocation();
            addressPlacesList = dataParser.parse(result);
            AddressGeo addressGeo = ShowInfoPlacesAddress(addressPlacesList);
            addPlaceAddress(addressGeo);
            Log.d("GooglePlacesReadTask", "onPostExecute Exit");
        }
    }

    /**
     * A method to download json data from url
     */
    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public static AddressGeo ShowInfoPlacesAddress(List<HashMap<String, String>> addressPlacesList) {
        AddressGeo addressGeo = null;
        for (int i = 0; i < addressPlacesList.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations=====" + i);
            HashMap<String, String> googlePlace = addressPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String formatted_address = googlePlace.get("formatted_address");

            addressGeo = new AddressGeo(lat, lng, formatted_address);
            return addressGeo;
        }
        return addressGeo;
    }

    private void addPlaceAddress(AddressGeo addressGeo) {
        final LatLng latLng = new LatLng(addressGeo.getLa(), addressGeo.getLn());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(addressGeo.getAddress());
        markerOptions.snippet(addressGeo.getLa() + " : " + addressGeo.getLn());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        Marker marker = googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
}
