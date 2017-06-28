package com.tm.environmenttm.map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.tm.environmenttm.fragment.HomeFragment;
import com.tm.environmenttm.model.AddressGeo;
import com.tm.environmenttm.model.Device;

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

public class TestMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener   {
    private View rootView;
    //gps
    GPSTracker gps;
    SharedPreferences sharedPreferences;
    ArrayList<Device> listDevice = null;
    double latitude;
    double longitude;
    //end gps

    //demo2
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton1, infoButton2;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private OnInfoWindowElemTouchListener infoButtonListener1;

    //static final LatLng latlng1 = new LatLng(10.81,106.9);
    // static final LatLng latlng2 = new LatLng(10.81, 106.6);
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
        String strtext = "";
        try {
            strtext = getArguments().getString("message");
            Log.e("TAG", "Inflate exception-----------------" + strtext);
        } catch (Exception x) {
        }

        try {
            rootView = inflater.inflate(R.layout.fragment_test_map, container, false);
        } catch (InflateException e) {
            Log.e("TAG", "Inflate exception");
        }

        //gps
        gps = new GPSTracker(getContext());

        if (gps.canGetLocation()) {
            Log.d("toadoLAgps==", "" + latitude);
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.d("toadoLONgps==", "" + longitude);
            String lati = latitude + "";
            String loti = longitude + "";
//            createSessionVITRIGPS(lati, loti);
            locationGPS = new LatLng(latitude, longitude);
            Toast.makeText(
                    getContext(),
                    "Your Location is -\nLat: " + latitude + "\nLong: "
                            + longitude, Toast.LENGTH_LONG).show();
        } else {
            gps.showSettingsAlert();
        }
        //end gps

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            getActivity().finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapclickmore);

        //test 220617
        //final GoogleMap googleMap = mapFragment.getMap();
     //   googleMap = mapFragment.getMap();

        mapFragment.getMapAsync(this);


        this.infoWindow = (ViewGroup) inflater.inflate(R.layout.custom_infowindow, null);


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

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public static Device getDeviceByName(ArrayList<Device> listDevice, String nameDevice) {
        int len = listDevice.size();
        for (int i = 0; i < len; i++) {
            if (listDevice.get(i).getNameDevice().equals(nameDevice))
                return listDevice.get(i);
        }
        return null;
    }

    public static void reloadIOTGridviewFragment(Context context) {
        Fragment mFragment;
        mFragment = new HomeFragment();
        FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frgContent, mFragment).commit();
    }

//    // tao session vitriGPS
//    public void createSessionVITRIGPS(String laGPS, String loGPS) {
//        sharedPreferences = getActivity().getSharedPreferences(ConfigApp.getVITRIGPS(), Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
////        ViTri vitriGPS = new ViTri(laGPS, loGPS);
//        editor.putString("VITRIGPSla", laGPS);
//        editor.putString("VITRIGPSlo", loGPS);
//        Log.d("toadoLAgps11========", laGPS);
//        Log.d("toadoLONgps====", loGPS);
//        editor.commit();
//    }

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
    public void onResume() {
        super.onResume();
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
    public void onMapReady(GoogleMap googleMaps) {
        googleMap =googleMaps;
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout) rootView.findViewById(R.id.map_relative_layout);

        mapWrapperLayout.init(googleMap, getPixelsFromDp(getContext(), 39 + 20));

        this.infoTitle = (TextView) infoWindow.findViewById(R.id.nameTxt);
        this.infoSnippet = (TextView) infoWindow.findViewById(R.id.addressTxt);
        this.infoButton1 = (Button) infoWindow.findViewById(R.id.btnOne);
        this.infoButton2 = (Button) infoWindow.findViewById(R.id.btnTwo);

        this.infoButtonListener1 = new OnInfoWindowElemTouchListener(infoButton1, getResources().getDrawable(R.color.colorPrimary), getResources().getDrawable(R.color.nliveo_white)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button

                String nameDevice = marker.getTitle();
//                Device device = getDeviceByName(listDevice, nameDevice);
//                IOTDeviceListFragment.createSeesionDeviceInfo(getContext(), sharedPreferences, device);
//                reloadIOTGridviewFragment(getContext());
                Toast.makeText(getContext(), nameDevice, Toast.LENGTH_LONG).show();
            }
        };
        this.infoButton1.setOnTouchListener(infoButtonListener1);

        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton2, getResources().getDrawable(R.color.colorPrimary), getResources().getDrawable(R.color.nliveo_white)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                String nameDevice = marker.getTitle();
//                Device device = getDeviceByName(listDevice, nameDevice);
//                IOTDeviceListFragment.createSeesionDeviceInfo(getContext(), sharedPreferences, device);
//                InfoDeviceFragment.reloadInfoDeviceFragment(getContext());
                Toast.makeText(getContext(), nameDevice, Toast.LENGTH_LONG).show();
            }
        };
        infoButton2.setOnTouchListener(infoButtonListener);

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                infoButtonListener.setMarker(marker);
                infoButtonListener1.setMarker(marker);
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        Log.i("ListDEVICEGPS", locationGPS.latitude + " : " + locationGPS.longitude);

        googleMap.addMarker(new MarkerOptions()
                .position(locationGPS)
                .title("THIS IS MY")
                .snippet(locationGPS.latitude + " : " + locationGPS.longitude)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        //du lieu mau
        Device device = new Device("asdasd", "Phonton1", "bienhoa", true, 10.878792, 106.987793, "des", "asdasd", "asdasd");
        Device device1 = new Device("123", "Phonton2", "bienhoa", false, 0.879792, 106.989793, "des", "asdasd", "asdasd");
        Device device2 = new Device("321", "Phonton3", "bienhoa", true, 0.868792, 106.997793, "des", "asdasd", "asdasd");
        listDevice = new ArrayList<>();
        listDevice.add(device);
        listDevice.add(device1);
        listDevice.add(device2);

        int len = listDevice.size();
        for (int i = 0; i < len; i++) {
            Log.i("ListDEVICEDir", listDevice.get(i).getNameDevice() + " - " + listDevice.get(i).getLatitude() + " : " + listDevice.get(i).getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(listDevice.get(i).getLatitude(), listDevice.get(i).getLongitude()))
                    .title(listDevice.get(i).getNameDevice())
                    .snippet(listDevice.get(i).getLocation())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationGPS, 10));


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


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    /*Lay thong tin dia diem*/
    private static String getUrl(String address) {
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
    private static String downloadUrl(String strUrl) throws IOException {
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

    private static AddressGeo ShowInfoPlacesAddress(List<HashMap<String, String>> addressPlacesList) {
        AddressGeo addressGeo = new AddressGeo();
        for (int i = 0; i < addressPlacesList.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations=====" + i);
            HashMap<String, String> googlePlace = addressPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String formatted_address = googlePlace.get("formatted_address");

            addressGeo = new AddressGeo(lat, lng, formatted_address);
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

    public static AddressGeo getInfoAddress(String address) {
        AddressGeo addressGeo = new AddressGeo();
        // Getting URL to the Google Directions API
        address = "khu phố 6, Thủ Đức, Hồ Chí Minh, Việt Nam";
        // String address = "Khu%20ph%E1%BB%91%206-%20Ph%C6%B0%E1%BB%9Dng%20Linh%20Trung%20-%20Qu%E1%BA%ADn%20Th%E1%BB%A7%20%C4%90%E1%BB%A9c,%20H%E1%BB%93%20Ch%C3%AD%20Minh,%20Vi%E1%BB%87t%20Nam";
        String url = getUrl(address);
        Log.d("getLocation", url.toString());

        // For storing data from web service
        String data = "";
        try {
            // Fetching the data from web service
            data = downloadUrl(url);
            Log.d("Background Task data", data.toString());
            List<HashMap<String, String>> addressPlacesList = null;
            DataParserLocation dataParser = new DataParserLocation();
            addressPlacesList = dataParser.parse(data);
            addressGeo = ShowInfoPlacesAddress(addressPlacesList);
            Log.d("GooglePlacesReadTask", "onPostExecute Exit");

        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }


        return addressGeo;
    }

}
