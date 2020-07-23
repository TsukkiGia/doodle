package com.example.oneinamillion.Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.example.oneinamillion.Models.MergeSortDate;
import com.example.oneinamillion.Models.MergeSortDistance;
import com.example.oneinamillion.Models.MergeSortPrice;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.EventAdapter;
import com.facebook.AccessToken;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
//https://icons8.com
//<a target="_blank" href="https://icons8.com/icons/set/filter">Filter icon</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>

public class HomeFragment extends Fragment {
    RecyclerView rvEvents;
    EventAdapter eventAdapter;
    List<Event> events;
    List<Event> results;
    ImageView ivMap;
    String currentlySelected;
    ExtendedFloatingActionButton fabDistance;
    ExtendedFloatingActionButton fabDate;
    ExtendedFloatingActionButton fabPrice;
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
    double max_distance=0;
    double max_price=0;
    Boolean filterdistance=false;
    Boolean filterprice=false;
    Boolean filtertags = false;
    List<String> tags=new ArrayList<>();
    ImageView ivFilter;
    FragmentManager fragmentManager;
    String sort_metric = "distance";

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (!getArguments().getString("max_distance").equals("100")){
                max_distance = Double.valueOf(getArguments().getString("max_distance"));
                filterdistance=true;
            }
            if (!getArguments().getString("max_price").equals("100")){
                max_price = Double.valueOf(getArguments().getString("max_price"));
                filterprice=true;
            }
            tags=getArguments().getStringArrayList("tags");
            filtertags=true;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!filtertags) {
            JSONArray tagg = ParseUser.getCurrentUser().getJSONArray("Interests");
            for (int i=0;i<tagg.length();i++) {
                try {
                    tags.add(tagg.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
        }
        filtertags=true;
        results = new ArrayList<>();
        fragmentManager = getParentFragmentManager();
        ivMap = view.findViewById(R.id.ivMap);
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), EventMapActivity.class);
                startActivity(i);
            }
        });
        ivFilter = view.findViewById(R.id.ivFilter);
        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterFragment filterFragment = new FilterFragment();
                Bundle bundle = new Bundle();
                bundle.putString("sort_metric",currentlySelected);
                filterFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.flContainer, filterFragment).commit();
            }
        });
        fusedLocationProviderClient = new FusedLocationProviderClient(getContext());
        getLocationPermission();
        getDeviceLocation();
        currentlySelected = "distance";
        pbLoading = view.findViewById(R.id.pbLoading);
        fabDate = view.findViewById(R.id.extFabDate);
        fabDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentlySelected.equals("date")) {
                    currentlySelected="date";
                    fabDate.setBackgroundColor(Color.parseColor("#39b894"));
                    fabDate.setTextColor(Color.parseColor("#FFFFFF"));
                    fabDistance.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    fabDistance.setTextColor(Color.parseColor("#000000"));
                    fabPrice.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    fabPrice.setTextColor(Color.parseColor("#000000"));
                    pbLoading.setVisibility(View.VISIBLE);
                    queryEventsDate();
                }
            }
        });
        fabDistance = view.findViewById(R.id.extFabDistance);
        fabDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentlySelected.equals("distance")) {
                    currentlySelected="distance";
                    fabDistance.setBackgroundColor(Color.parseColor("#39b894"));
                    fabDistance.setTextColor(Color.parseColor("#FFFFFF"));
                    fabDate.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    fabDate.setTextColor(Color.parseColor("#000000"));
                    fabPrice.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    fabPrice.setTextColor(Color.parseColor("#000000"));
                    pbLoading.setVisibility(View.VISIBLE);
                    queryEventsNearby();
                }
            }
        });
        fabPrice = view.findViewById(R.id.extFabPrice);
        fabPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentlySelected.equals("price")) {
                    currentlySelected="price";
                    fabPrice.setBackgroundColor(Color.parseColor("#39b894"));
                    fabPrice.setTextColor(Color.parseColor("#FFFFFF"));
                    fabDate.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    fabDate.setTextColor(Color.parseColor("#000000"));
                    fabDistance.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    fabDistance.setTextColor(Color.parseColor("#000000"));
                    pbLoading.setVisibility(View.VISIBLE);
                    queryCheaperEvents();
                }
            }
        });
        ivProfile = view.findViewById(R.id.ivProfile);
        rvEvents = view.findViewById(R.id.rvEvents);
        fabCreate = view.findViewById(R.id.fabCreate);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AddEventActivity.class);
                startActivity(i);
            }
        });
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                InitialQuery();
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
        if (ParseUser.getCurrentUser().getParseFile("ProfileImage") != null) {
            Glide.with(getContext()).load(ParseUser.getCurrentUser().getParseFile("ProfileImage").getUrl())
                    .circleCrop().into(ivProfile);
        } else {
            ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.instagram_user_filled_24));
        }
        InitialQuery();
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

    private void InitialQuery() {
        eventAdapter.clear();
        results.clear();
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
                    long now = System.currentTimeMillis();
                    Date firstDate = null;
                    try {
                        firstDate = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(event.getDate());
                    } catch (java.text.ParseException ex) {
                        ex.printStackTrace();
                    }
                    long dateInMillies = firstDate.getTime();
                    if (dateInMillies > now) {
                        event.setDistance(event.getLocation().distanceInKilometersTo(new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));
                        results.add(event);
                    }
                }
                if (currentlySelected.equals("distance")) {
                    queryEventsNearby();
                }
                else if (currentlySelected.equals("date")) {
                    queryEventsDate();
                }
                else {
                    queryCheaperEvents();
                }
            }
        });
        swipeContainer.setRefreshing(false);
    }

    private void queryEventsNearby() {
        eventAdapter.clear();
        MergeSortDistance m = new MergeSortDistance();
        m.mergeSort(results);
        filter();
        eventAdapter.addAll(results);
        eventAdapter.notifyDataSetChanged();
        pbLoading.setVisibility(View.INVISIBLE); }

    private void queryCheaperEvents() {
        eventAdapter.clear();
        MergeSortPrice m = new MergeSortPrice();
        m.mergeSort(results);
        filter();
        eventAdapter.addAll(results);
        eventAdapter.notifyDataSetChanged();
        pbLoading.setVisibility(View.INVISIBLE); }

    private void queryEventsDate() {
        eventAdapter.clear();
        MergeSortDate m = new MergeSortDate();
        m.mergeSort(results);
        filter();
        eventAdapter.addAll(results);
        eventAdapter.notifyDataSetChanged();
        pbLoading.setVisibility(View.INVISIBLE); }

    private void filter () {
        if (filtertags) {
            Log.i(TAG,"filtering interests");
            List<Event> filtered = filterEventsInterests(results);
            results = filtered;
        }
        if (filterprice) {
            Log.i(TAG,"filtering price");
            List<Event> filtered = filterPriceEvents(results);
            results = filtered;
        }
        if(filterdistance) {
            Log.i(TAG,"filtering distance");
            List<Event> filtered = filterCloseEvents(results);
            results = filtered;
        }
    }

    private List<Event> filterCloseEvents (List<Event> events) {
        List<Event> closeEvents = new ArrayList<>();
        ParseGeoPoint currentLocation = new ParseGeoPoint(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
        for (Event event: events) {
            if (event.getLocation().distanceInKilometersTo(currentLocation)<max_distance) {
                closeEvents.add(event);
            }
        }
        return closeEvents;
    }

    private List<Event> filterPriceEvents (List<Event> events) {
        List<Event> priceEvents = new ArrayList<>();
        for (Event event: events) {
            if (Double.valueOf(event.getPrice())<max_price) {
                priceEvents.add(event);
            }
        }
        return priceEvents;
    }

    private List<Event> filterEventsInterests(List<Event> events) {
        List<Event> interestingEvents = new ArrayList<>();
        for (Event event: events) {
            String eventtag = event.getEventTag().toString();
            for (int i = 0; i < tags.size(); i++) {
                if (eventtag.contains(tags.get(i))) {
                    interestingEvents.add(event);
                    break;
                }
            }
        }
        return interestingEvents;
    }
}