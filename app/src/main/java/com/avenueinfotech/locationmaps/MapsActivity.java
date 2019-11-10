package com.avenueinfotech.locationmaps;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.avenueinfotech.locationmaps.utils.GPSTracker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlacePicker;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.google.android.libraries.places.api.Places;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int REQUEST_LOCATION = 0;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private static final String TAG = "";
    private GoogleMap mMap;
    private int markerCount;

    LocationManager locationManager;
    String provider;

    private ProgressDialog dialog;

    private static GPSTracker gps;

    Marker marker;



//    private AdView mAdView;
////
//    private InterstitialAd mInterstitialAd;

    public final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


    String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

    private FusedLocationProviderClient mFusedLocationClient;

    private StartAppAd startAppAd = new StartAppAd(this);

    PlacesClient placesClient;
    List<Place.Field> placeFields = Arrays.asList (Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS);
    AutocompleteSupportFragment places_fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppSDK.init(this, "202149329", true);

//        StartAppAd.disableSplash();

        StartAppSDK.init(this, "202149329", false);



        setContentView(R.layout.activity_maps);
//        setContentView(R.layout.activity_maps);
//        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1183672799205641~2611322257");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);






//

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        MapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);


        //Check If Google Services Is Available
        if (getServicesAvailable()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
//            Toast.makeText(this, "Google Service Is Available!!", Toast.LENGTH_SHORT).show();
        }

        gps = new GPSTracker(this);

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_ID_MULTIPLE_PERMISSIONS);
        } else {

            if (!gps.canGetLocation()) {
                switchGPS();
            }

//            GeneralUtils.createDirectory();


        }

        dialog = new ProgressDialog(MapsActivity.this);
        dialog.setCancelable(false);

//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
////               destination = place.getName().toString();
//                final LatLng latLngLoc = place.getLatLng();
//
//                if(marker!=null){
//                    marker.remove();
//                }
//                marker = mMap.addMarker(new MarkerOptions().position(latLngLoc).title(place.getName().toString()));
////                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
//                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLngLoc, 18);
//                mMap.moveCamera(update);
//            }
//
//            @Override
//            public void onError(Status status) {
//
//            }
//        });




//        initPlaces();
//
//        setupPlaceAutoComplete();

    }

//    private void initPlaces(){
//        Places.initialize (this,getString (R.string.google_maps_key));
//        placesClient = Places.createClient (this);
//
////        if (!Places.isInitialized()) {
////            Places.initialize(getApplicationContext(), "AIzaSyC5lB-7uF7TG6AZkQ4OersABEQ2dKyl-bE");
////        }
//    }
//
//    private void setupPlaceAutoComplete(){
//        places_fragment = (AutocompleteSupportFragment)getSupportFragmentManager ()
//                .findFragmentById (R.id.place_autocomplete_fragment);
//        places_fragment.setPlaceFields (placeFields);
//        places_fragment.setOnPlaceSelectedListener (new PlaceSelectionListener () {
//            @SuppressWarnings("ConstantConditions")
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                Toast.makeText (MapsActivity.this, ""+place.getName (), Toast.LENGTH_SHORT).show ();
//
//                final LatLng latLngLoc = place.getLatLng();
//
//                if(marker!=null){
//                    marker.remove();
//                }
////                marker = mMap.addMarker(new MarkerOptions().position(latLngLoc).title(place.getName().toString()));
//////                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
////                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLngLoc, 18);
////                mMap.moveCamera(update);
//
//                marker = mMap.addMarker(new MarkerOptions().position(latLngLoc)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.iconww))
//                        .title(place.getName().toString()));
////                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
//                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLngLoc, 18);
//                mMap.moveCamera(update);
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//                Toast.makeText (MapsActivity.this, ""+status.getStatusMessage (), Toast.LENGTH_SHORT).show ();
//            }
//        });
//
////        places_fragment = (AutocompleteSupportFragment)
////                getSupportFragmentManager ().findFragmentById (R.id.place_autocomplete_fragment);
////
////        places_fragment.setPlaceFields (Arrays.asList (Place.Field.ID, Place.Field.NAME));
////
////        places_fragment.setOnPlaceSelectedListener (new PlaceSelectionListener () {
////            @Override
////            public void onPlaceSelected(@NonNull Place place) {
////                Toast.makeText (MapsActivity.this, ""+place.getName (), Toast.LENGTH_SHORT).show ();
//////
////                final LatLng latLngLoc = place.getLatLng();
////
////                if(marker!=null){
////                    marker.remove();
////                }
//////                marker = mMap.addMarker(new MarkerOptions().position(latLngLoc).title(place.getName().toString()));
//////                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
//////                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLngLoc, 18);
//////                mMap.moveCamera(update);
////
////                marker = mMap.addMarker(new MarkerOptions().position(latLngLoc)
////                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.iconww))
////                        .title(place.getName().toString()));
////                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
////                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLngLoc, 18);
////                mMap.moveCamera(update);
////            }
////
////            @Override
////            public void onError(@NonNull Status status) {
////                Toast.makeText (MapsActivity.this, ""+status.getStatusMessage (), Toast.LENGTH_SHORT).show ();
////            }
////        });
//
//
//    }

    public void privacy (View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/avenueinfotechprivacypolicy/home"));
        startActivity(browserIntent);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void switchGPS() {
        {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MapsActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(MapsActivity.this, 0, this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        displayLocation();




        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().isCompassEnabled();
        mMap.getUiSettings().isZoomControlsEnabled();
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


    }

    Marker mk = null;
    // Add A Map Pointer To The MAp
    public void addMarker(GoogleMap googleMap, double lat, double lon) {

        if(markerCount==1){
            animateMarker(mLastLocation,mk);
        }

        else if (markerCount==0){
            //Set Custom BitMap for Pointer
            int height = 110;
            int width = 50;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.icon_car);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            mMap = googleMap;

            LatLng latlong = new LatLng(lat, lon);
            mk= mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3))
                    .icon(BitmapDescriptorFactory.fromBitmap((smallMarker))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 18));

            //Set Marker Count to 1 after first marker is created
            markerCount=1;

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                // TODO: Consider calling
                return;
            }
            //mMap.setMyLocationEnabled(true);
            startLocationUpdates();

        }
    }

    @Override
    public void onInfoWindowClick (Marker marker){
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
    }


    public boolean getServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {

            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "No Play Services detected", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
//        startLocationUpdates();

        displayLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAppAd.onResume();


        getServicesAvailable();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

    //Method to display the location on UI
    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
            // TODO: Consider calling
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {


            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                String loc = "" + latitude + " ," + longitude + " ";
                Toast.makeText(this,loc, Toast.LENGTH_SHORT).show();

                //Add pointer to the map at location
                addMarker(mMap,latitude,longitude);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

            } else {

//                Toast.makeText(this, "Enabling GPS for location tracking",
//                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(AppConstants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(AppConstants.FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(AppConstants.DISPLACEMENT);
    }



    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest,  this);

        }
    }


    protected void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(
//                mGoogleApiClient, this);

    }



    @Override
    public void onConnected(@Nullable Bundle arg0) {
        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

//        Toast.makeText(this, "Click GPS Icon for location tracking",
//                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
//        mLastLocation = location;
//
//        if(location == null) {
//            Toast.makeText(getApplicationContext(), "can't get current Location",
//                    Toast.LENGTH_SHORT).show();
//        } else {
//            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 18);
//            mMap.animateCamera(update);
//
//        }

        // Displaying the new location on UI
//        displayLocation();
    }

    public static void animateMarker(final Location destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    public void StartGPS(View view) {
        displayLocation();
        startLocationUpdates();
    }

    public void geoLocate(View view) {

        EditText et  = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = null;
        try {
            list = gc.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace ();
        }
        Address address = list.get(0);
        String locality = address.getLocality();
//        String area = address.getLocality();


        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();


        double lat = address.getLatitude();
        double lng = address.getLongitude();
        goToLocationZoom(lat, lng, 10);

        setMarker(locality, lat, lng);
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 19);
        mMap.animateCamera(update);


    }

    private void setMarker(String locality, double lat, double lng) {
        if (marker != null) {
            marker.remove();
        }

        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iconww))
                .position(new LatLng(lat, lng))
                .snippet("I am here");

        marker = mMap.addMarker(options);
    }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

//    private void loadPlacePicker() {
//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder(); // 25
//
//        try {
//            startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);
//        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    public void onBackPressed() {
//
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        } else {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
//            builder.setMessage("Do you want to Quit?");
//            builder.setCancelable(true);
//            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.cancel();
//                }
//            });
//            builder.setPositiveButton("Quit!", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    finish();
//                }
//            });
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//        }
//    }





    /**
     * Part of the activity's life cycle, StartAppAd should be integrated here
     * for the back button exit ad integration.
     */
//    @Override
//    public void onBackPressed() {
//
//
////        startAppAd.onBackPressed();
//
//        showInterstitial();
//
//        super.onBackPressed ();
//
//
//
//    }
//
//    private void showInterstitial() {
//
////        if (startAppAd != null) {
////            startAppAd.show();
////        } else
//        showExitDialog();
//    }

//    private void shoeExitDialog() {
//        new AlertDialog.Builder(MapsActivity.this)
//                .setCancelable(false)
//                .setMessage("You sure to Exit App?")
//                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//
//                        dialog.dismiss();
//                        finish();
//                    }
//                })
//                .setNegativeButton("Stay", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //If the User chooses to Stay in the App & the Ad instance is not Loaded
//                        //We load the Interstitial Ad again!
////                        if(!mInterstitialAd.isLoaded()) mInterstitialAd.loadAd(new AdRequest.Builder().build());
////                        dialog.dismiss();
//                        startAppAd.show();
//                    }
//                })
//                .show();
//    }


//
//    public void showExitDialog() {
//        new AlertDialog.Builder(MapsActivity.this)
//                .setCancelable(false)
//                .setMessage("You sure to Exit App?")
//                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        dialog.dismiss();
//                        finish();
//                    }
//                })
//                .setNegativeButton("Stay", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //If the User chooses to Stay in the App & the Ad instance is not Loaded
//                        //We load the Interstitial Ad again!
//                        if(startAppAd != null) {
//                            startAppAd.show ();
//                        }
////                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                           else
//
//
////                        dialog.dismiss();
//                    }
//                })
//                .show();
//    }

    @Override
    public void onBackPressed() {
//        if (startAppAd != null) {
//            startAppAd.show();
//        } else {
// if your using fragment then you can do this way
//            int fragments = getSupportFragmentManager ().getBackStackEntryCount ();
//            if (fragments == 1) {
//                new AlertDialog.Builder (this)
//                        .setMessage ("Are you sure you want to exit?")
//                        .setCancelable (true)
//                        .setPositiveButton ("Yes", new DialogInterface.OnClickListener () {
//                            public void onClick(DialogInterface dialog, int id) {
//                                finish ();
//                            }
//                        })
//                        .setNegativeButton ("No", null)
//                        .show ();
//
//
//            } else {
//                if (getFragmentManager ().getBackStackEntryCount () > 1) {
//                    getFragmentManager ().popBackStack ();
//                } else {
//
//                    super.onBackPressed ();
//                }
//            }
//        if (startAppAd != null) {
//            startAppAd.show ();
//        } else {


            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder (MapsActivity.this);
            builder.setMessage ("Do you want to Quit?");
            builder.setCancelable (true);
            builder.setNegativeButton ("NO", new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel ();
                    startAppAd.show ();
                }
            });
            builder.setPositiveButton ("Quit!", new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    startAppAd.show ();
                    finish ();
                }
            });
            AlertDialog alertDialog = builder.create ();
            alertDialog.show ();

        }




    }