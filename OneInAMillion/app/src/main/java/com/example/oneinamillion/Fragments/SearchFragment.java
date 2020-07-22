package com.example.oneinamillion.Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.oneinamillion.DetailsActivity;
import com.example.oneinamillion.Models.CustomWindowAdapter;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Headers;


public class SearchFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    public static final String TAG = "SearchFragment";
    private SearchView searchView = null;
    Toolbar toolbar;
    List<Event> results;
    Event detailevent;
    private SearchView.OnQueryTextListener queryTextListener;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_frag, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG,query);
                filterEvents(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        results =  new ArrayList<>();
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        // Build the map.
        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;
        map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i(TAG,"clicked");
                Log.i(TAG,marker.getTag().toString());
                Log.i(TAG,results.toString());
                for (final Event event : results) {
                    if (event.getObjectId().equals(marker.getTag().toString())) {
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.get("https://maps.googleapis.com/maps/api/geocode/json?latlng="+
                                String.valueOf(event.getLocation().getLatitude())+","+String.valueOf(event.getLocation().getLongitude())+
                                "&key=AIzaSyAHhqNOmXH6jPO42U89s12nJNAQucTvw40", new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess");
                                JSONObject jsonObject = json.jsonObject;
                                try {
                                    String address = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                    Intent i = new Intent(getContext(), DetailsActivity.class);
                                    i.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                                    i.putExtra("address",address);
                                    getContext().startActivity(i);
                                }
                                catch (JSONException e) {
                                    Log.e(TAG, "Hit JSON exception",e);
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i(TAG, "Failed");
                            }
                        });

                    }
                }
            }
        });
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_ORGANIZER);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                for (Event event : events) {
                    Double lat = event.getLocation().getLatitude();
                    Double longitude = event.getLocation().getLongitude();
                    Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, longitude)).snippet(event.getDescription()).title(event.getEventName()));
                    marker.setTag(event.getObjectId());
                    results.add(event);
                }
            }
        });
        // Prompt the user for permission.
        getLocationPermission();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private Event getEvent(String objectID) {
        Event dummy = new Event();
        for (Event event : results) {
            if (event.getObjectId().equals(objectID)) {
                return event;
            }
        }
        return dummy;
    }

    //Gets the current location of the device, and positions the map's camera.
    private void getDeviceLocation() {
        /*
          Get the best and most recent location of the device, which may be null in rare
          cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
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
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
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
    private void filterEvents(String query) {
        results.clear();
        String[] querywords = query.split(" ");
        List<String> keywords = Arrays.asList(querywords);
        for (int i = 0; i<keywords.size(); i++) {
            String keyword = keywords.get(i);
            ParseQuery<Event> parseQuery = ParseQuery.getQuery(Event.class);
            parseQuery.include(Event.KEY_ORGANIZER);
            parseQuery.whereMatches(Event.KEY_DESCRIPTION, "("+keyword+")", "i");
            parseQuery.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> events, ParseException e) {
                    Log.i(TAG,events.toString());
                    for(Event event: events) {
                        if (!isInside(event,results)) {
                            results.add(event);
                            Log.i(TAG,"results "+results.toString());
                        }
                    }
                    map.clear();
                    for (Event event: results) {
                        Double lat = event.getLocation().getLatitude();
                        Double longitude = event.getLocation().getLongitude();
                        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, longitude)).snippet(event.getDescription()+". Click to see details!").title(event.getEventName()));
                        marker.setTag(event.getObjectId());
                    }
                }
            });
        }
    }

    private boolean isInside(Event event, List<Event> results) {
        Boolean amIhere=false;
        for (Event ev: results) {
            if (event.getObjectId().equals(ev.getObjectId())) {
                amIhere=true;
            }
        }
        return amIhere;
    }
}
