package com.example.oneinamillion.Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.AddEventActivity;
import com.example.oneinamillion.EventMapActivity;
import com.example.oneinamillion.InterestActivity;
import com.example.oneinamillion.InterestActivityMotion;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.MergeSort;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.EventAdapter;
import com.facebook.AccessToken;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
//https://icons8.com
//<a target="_blank" href="https://icons8.com/icons/set/filter">Filter icon</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>

public class HomeFragment extends Fragment {
    RecyclerView rvEvents;
    EventAdapter eventAdapter;
    List<Event> events;
    ImageView ivMap;
    FloatingActionButton fabCreate;
    public static final String TAG = "HomeFragment";
    private SwipeRefreshLayout swipeContainer;
    ImageView ivProfile;
    ProgressBar pbLoading;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivMap = view.findViewById(R.id.ivMap);
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), EventMapActivity.class);
                startActivity(i);
            }
        });
        fusedLocationProviderClient = new FusedLocationProviderClient(getContext());
        getLocationPermission();
        getDeviceLocation();
        ivProfile = view.findViewById(R.id.ivProfile);
        rvEvents = view.findViewById(R.id.rvEvents);
        fabCreate = view.findViewById(R.id.fabCreate);
        pbLoading = view.findViewById(R.id.pbLoading);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage();
            }
        });
        swipeContainer.setColorSchemeResources(R.color.colorAccent);
        events = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(),events);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvEvents.setLayoutManager(layoutManager);
        rvEvents.setAdapter(eventAdapter);
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), InterestActivityMotion.class);
                startActivity(i);
            }
        });
        if (ParseUser.getCurrentUser().getParseFile("ProfileImage") != null) {
            Glide.with(getContext()).load(ParseUser.getCurrentUser().getParseFile("ProfileImage").getUrl())
                    .circleCrop().into(ivProfile);
        } else {
            ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.instagram_user_filled_24));
        }
        queryEventsNearby();
    }

    private void refreshPage() {
        queryEventsNearby();
        swipeContainer.setRefreshing(false);
    }

    private void queryEventsInterests() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_ORGANIZER);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "problem!", e);
                }
                for (Event event : events) {
                    Log.i(TAG,"Description: "+event.getDescription());
                }
                eventAdapter.clear();
                eventAdapter.addAll(events);
                eventAdapter.notifyDataSetChanged();
                pbLoading.setVisibility(View.INVISIBLE);
            }
        });
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
                        }
                        else {
                            Log.e(TAG, "Exception: %s", task.getException());
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

    private void queryEventsNearby() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        final List<Event> eventsWithDistances = new ArrayList<>();
        query.include(Event.KEY_ORGANIZER);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "problem!", e);
                }
                for (Event event : events) {
                    double dist = calculateDistance(event.getLocation());
                    event.setDistance(event.getLocation().distanceInKilometersTo(new ParseGeoPoint(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude())));
                    eventsWithDistances.add(event);
                }

                MergeSort m = new MergeSort();
                m.mergeSort(eventsWithDistances);
                eventAdapter.clear();
                eventAdapter.addAll(eventsWithDistances);
                eventAdapter.notifyDataSetChanged();
                pbLoading.setVisibility(View.INVISIBLE);
            }
        });
    }

    private double calculateDistance(ParseGeoPoint location) {
        double lon1 = lastKnownLocation.getLongitude();
        double lat1 = lastKnownLocation.getLatitude();
        double lon2 = location.getLongitude();
        double lat2 = location.getLatitude();
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat/2),2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2),2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371;
        return(c * r);
    }
}