package com.example.shadowstorm.metrocabv2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.security.auth.login.LoginException;

public class MapsActivity extends FragmentActivity {

    GPSTracker gps;
    double longitude1, latitude1;
    Thread thread;

    user_database userDb;


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //this publicLatLng is for universal use which has been initialized in the section below at location manager
    public LatLng publicLatLng;
    Marker marker = null;
    GPSTracker gps1= new GPSTracker(MapsActivity.this);





    //setUpMap onCreate mathi xa coz crash hunxa ntra coz we need publicLatLng initialized

    private void setUpMap() {

        userDb= new user_database(this);
        final ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        // This schedules a task to run every 10 seconds:

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    //Looper.prepare();
                    // gps = new GPSTracker(MapsActivity.this);
//                    latitude1 = gps.getLatitude();
//                    longitude1 = gps.getLongitude();

                    LocationManager locationManager = (LocationManager)
                            getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();

                    Location location = locationManager.getLastKnownLocation(locationManager
                            .getBestProvider(criteria, false));
                    //i changed it from location.getLatitude() to gps.getLatitude cause the earlier one returned null and crashed but
                    //works fine with location as well now
                    latitude1 = location.getLatitude();
                    longitude1 = location.getLongitude();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Cursor res = userDb.getUserData();
                            if (res.getCount() > 0) {
                                while (res.moveToNext()) {
                                    String user = res.getString(9);//this will get the user name from sqlite db and update lat long in xampp
                                    //sqlite bata 10 janako liyera ani k garxa?
                                    //run app and see
                                    //ok w8

                                    Toast.makeText(MapsActivity.this, "Lat: " + latitude1 + "Lon: " + longitude1, Toast.LENGTH_SHORT).show();

                                    Utils.updateLocationInDatabase(MapsActivity.this, user, latitude1 + "", longitude1 + "");
                                    //Marker marker= new MarkerOptions().position(new LatLng(latitude1, longitude1).title("current position"));
                                    // mMap.addMarker(marker));
                                    //MarkerOptions marker = new MarkerOptions();
                                    // Marker marker ;
                                    if (marker == null) {
                                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("current position"));
                                    } else {
                                        marker.remove();
                                        // lol google ko blue marker le wrong  dekhaxa mero marker le accurate dekhaxa lol

                                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("current position"));
                                    }
                                }
                            }
                        }
                    });
                    //Looper.loop();
                }
            }
        });
        thread.start();
        //current loc
        // latitude1 = gps.getLatitude();
        // longitude1 = gps.getLongitude();
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        //i changed it from location.getLatitude() to gps.getLatitude cause the earlier one returned null and crashed but
        //works fine with location as well now
        latitude1 = location.getLatitude();
        longitude1 = location.getLongitude();
        //yo marker le mathi ko current bhanne marker lai override garxa coz both return the same value
        Toast.makeText(this, "Lat: " + latitude1 + "Lon: " + longitude1, Toast.LENGTH_LONG).show();
        Cursor res = userDb.getUserData();
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                String user = res.getString(9);
                Utils.updateLocationInDatabase(this, user, latitude1 + "", longitude1 + "");
            }
        }




        //mMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("current position"));


        // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        //setMyLocation lai false gardiye hunxa paxi overlap nabhyera dekhauna mann nabhaye


        mMap.setMyLocationEnabled(true);


        publicLatLng = new LatLng(latitude1, longitude1);




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        gps = new GPSTracker(this);

        setUpMapIfNeeded();




        GPSTracker gps = new GPSTracker(this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        //mMap.addMarker(new MarkerOptions()
        //       .position(new LatLng(latitude, longitude))
        //       .title("current"));
        int carsCount1=0;
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude + carsCount1 + -0.05, carsCount1 + longitude + -0.05))
                .title("car No " + carsCount1));



        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        //i changed it from location.getLatitude() to gps.getLatitude cause the earlier one returned null and crashed but
        //works fine with location as well now
        latitude1 = location.getLatitude();
        longitude1 = location.getLongitude();

        //zoomlevel
        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(latitude1,longitude1));
        CameraUpdate zoom =CameraUpdateFactory.zoomTo(18);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);





        //  LatLng myLatLng = new LatLng(latitude,longitude);
        // Location myLocation = new Location(String.valueOf(myLatLng));

        // float acc = myLocation.getAccuracy();


        //Toast.makeText(this, acc+"" ,Toast.LENGTH_SHORT).show();



        //this below  doesnt work so commented
        //current location marker by marker button
      /*  double latitudeGoogle = mMap.getMyLocation().getLatitude();
        double longitudeGoogle = mMap .getMyLocation().getLongitude();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitudeGoogle, longitudeGoogle))
                .title("Current Position "));
        */
        //for marking different cars on map

        int carsCount;
        for(carsCount= 0;carsCount<=10;carsCount++){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(carsCount, carsCount))
                    .title("Hello world"));
        }

        //polyline between two points
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0))
                .width(5)
                .color(Color.BLUE));

        //suppose nearest cars to be around
    }




     public void getMyLocationAddress(View v){

                String lat= Double.toString(publicLatLng.latitude);
                String lon= Double.toString(publicLatLng.longitude);
                String type = "search";
         user_database userDb = new user_database(this);
                Cursor res = userDb.getUserData();
                if (res.getCount() > 0) {
                    while (res.moveToNext()) {
                        String userName = res.getString(9);
                        findFriends backgroundWorkers = new findFriends(MapsActivity.this);
                        backgroundWorkers.execute(type, userName, lat, lon);
                    }
                }

               /* Intent intent;
                intent = new Intent(MapsActivity.this, MyAddressLocationActivity.class);
                startActivity(intent);*/


                // request your webservice here. Possible use of AsyncTask and ProgressDialog
                // show the result here - dialog or Toast

        /*public void getMyLocationAddress(View v){

            String lat= Double.toString(publicLatLng.latitude);
            String lon= Double.toString(publicLatLng.longitude);
            String type = "search";
            String userName="admin";
            FindFriends backgroundWorkers = new FindFriends(MapsActivity.this);
            backgroundWorkers.execute(type, userName, lat, lon);
        }*/
    }



    public void findDrivers(View v){

        String lat= Double.toString(publicLatLng.latitude);
        String lon= Double.toString(publicLatLng.longitude);
        String type = "search";
        Cursor res = userDb.getUserData();
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                String userName = res.getString(9);
                findFriends backgroundWorkers = new findFriends(MapsActivity.this);
                backgroundWorkers.execute(type, userName, lat, lon);
            }
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    @Override
    protected void onPause() {
        super.onPause();
        thread.interrupt();
    }



    public void onZoomIn(View v){

        if (v.getId() == R.id.idZoomInButton){
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }

    }

    public void onZoomOut(View v){
        if (v.getId() == R.id.idZoomOutButton) {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());

        }
    }
    public void changeType(View v){
        if(mMap.getMapType()== GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        }
    }
    public void onSearch(View v){
        EditText searchBar = (EditText)findViewById(R.id.idSearchBar);
        String Location = searchBar.getText().toString();

        List <Address> addressList = null;




        if(Location != null || Location != ""){
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList =  geocoder.getFromLocationName(Location, 1);



            }
            catch (IOException e){
                e.printStackTrace();
            }



         /*   Address address = addressList.get(0);

            LatLng latlng = new LatLng(address.getLatitude(),address.getLongitude());
            Location location = new Location(String.valueOf(latlng));

            if(location.getAccuracy()<=5){
                mMap.addMarker(new MarkerOptions().position(latlng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));


            }


        */








            /*if (!location.hasAccuracy()) {
                return;
            }
            if (location.getAccuracy() > 5) {
                return;
            }*/
        }


    }

    public void streetSearchBar(Location location) {
        if (!location.hasAccuracy()) {
            return;
        }
        if (location.getAccuracy() > 5) {
            return;
        }
        // do something with location accurate to 5 meters here.
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */










}
