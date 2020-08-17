// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.oneinamillion;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.oneinamillion.Models.CustomWindowAdapter;
import com.example.oneinamillion.Models.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class HomeMapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final String TAG = HomeMapActivity.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;
    private PlacesClient mPlacesClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    List<Event> homevents;
    final String raffle_tag = "raffle";
    final String thon_tag= "thon";
    final String sport_tag = "sport";
    final String auctions_tag = "auction";
    final String cook_tag = "cook";
    final String concert_tag = "music";
    final String gala_tag = "gala";
    final String craft_tag = "craft";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_home_map);
        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        mPlacesClient = Places.createClient(this);
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        homevents = Parcels.unwrap(getIntent().getParcelableExtra("results"));
    }

    //Saves the state of the map when the activity is paused.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    /*
     Manipulates the map when it's available.
     This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (final Event event : homevents) {
                    if (event.getObjectId().equals(marker.getTag())){
                        Intent i = new Intent(HomeMapActivity.this, EventDetailsActivity.class);
                        i.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                        i.putExtra("address",event.getParseAddress());
                        i.putExtra("activity","HomeFragment");
                        startActivity(i);
                    }
                }
            }
        });
        map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));
        for (Event event : homevents) {
            double lat = event.getLocation().getLatitude();
            double longitude = event.getLocation().getLongitude();
            Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, longitude))
                    .title(event.getEventName()).snippet(event.getDescription()));
            marker.setTag(event.getObjectId());
            try {
                String firsttag = event.getEventTag().getString(0);
                switch(firsttag){
                    case sport_tag:
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sportmarker));
                        break;
                    case auctions_tag:
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.auctionmarker));
                        break;
                    case concert_tag:
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.concertmarker));
                        break;
                    case gala_tag:
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.galamarker));
                        break;
                    case raffle_tag:
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.rafflemarker));
                        break;
                    case cook_tag:
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cookmarker));
                        break;
                    case craft_tag:
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.craftmarker));
                        break;
                    case thon_tag:
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.thonmarker));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + firsttag);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
            // Prompt the user for permission.
            getLocationPermission();
            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();
            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
    }

    /*
     Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         Get the best and most recent location of the device, which may be null in rare
         cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    //Prompts the user for permission to use the device location.
    private void getLocationPermission() {
        /*
         Request location permission, so that we can get the location of the
         device. The result of the permission request is handled by a callback,
         onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //Handles the result of the request for location permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    //Updates the map's UI settings based on whether the user has granted location permission.
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
