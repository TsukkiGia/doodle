package com.example.oneinamillion.Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.AddEventActivity;
import com.example.oneinamillion.HomeMapActivity;
import com.example.oneinamillion.Models.EndlessRecyclerViewScrollListener;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.MergeSort;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.EventAdapter;
import com.facebook.AccessToken;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    RecyclerView rvEvents;
    EventAdapter eventAdapter;
    List<Event> events;
    List<Event> results;
    ImageView ivMap;
    String currentlySelected = "distance";
    ExtendedFloatingActionButton fabDistance;
    ExtendedFloatingActionButton fabDate;
    ExtendedFloatingActionButton fabPrice;
    FloatingActionButton fabCreate;
    public static final String TAG = "HomeFragment";
    private SwipeRefreshLayout swipeContainer;
    ImageView ivProfile;
    ProgressBar pbLoading;
    final String date_string = "date";
    final String distance_string = "distance";
    final String price_string = "price";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    Location lastKnownLocation = new Location("");
    double max_distance = 1000;
    double max_price = 500;
    int count = 0;
    Boolean filterdistance = false;
    Boolean filterprice = false;
    Boolean filtertags = false;
    Boolean filterfriends = false;
    List<String> tags = new ArrayList<>();
    ImageView ivFilter;
    FragmentManager fragmentManager;

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
            if (getArguments().getBoolean("filterdistance")){
                max_distance = Double.valueOf(getArguments().getString("max_distance"));
                filterdistance = true;
            }
            if (getArguments().getBoolean("filterprice")){
                max_price = Double.valueOf(getArguments().getString("max_price"));
                filterprice = true;
            }
            tags = getArguments().getStringArrayList("tags");
            filtertags = true;
            filterfriends = getArguments().getBoolean("friends");
            currentlySelected = getArguments().getString("sort_metric");
            Log.i(TAG,currentlySelected);
        }
        fusedLocationProviderClient = new FusedLocationProviderClient(getContext());
        getLocationPermission();
        getDeviceLocation();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!filtertags) {
            getUsersTags();
            filtertags = true;
        }
        initializeViews(view);
        setClickListeners();
        setupHomeFeed();
    }

    private void setupHomeFeed() {
        events = new ArrayList<>();
        results = new ArrayList<>();
        if (currentlySelected.equals(date_string)) {
            setActiveButton(distance_string,date_string,fabDate,false);
        }
        if (currentlySelected.equals(distance_string)) {
            setActiveButton(price_string,distance_string,fabDistance,false);
        }
        if (currentlySelected.equals(price_string)) {
            setActiveButton(date_string,price_string,fabPrice,false);
        }
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                InitialQuery();
            }
        });
        swipeContainer.setColorSchemeResources(R.color.colorAccent);
        eventAdapter = new EventAdapter(getContext(),events);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvEvents.setLayoutManager(layoutManager);
        rvEvents.setAdapter(eventAdapter);
        rvEvents.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(TAG,"Dismissed Snackbar");
                    }
                };
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    Snackbar.make(getView(),"You have reached the end of your feed",Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.snackbar_dismiss),listener).show();
                }
            }
        });
        if (ParseUser.getCurrentUser().getParseFile("ProfileImage") != null) {
            Glide.with(getContext()).load(ParseUser.getCurrentUser().getParseFile("ProfileImage").getUrl())
                    .circleCrop().into(ivProfile);
        } else {
            Glide.with(getContext()).load(getURLForResource(R.drawable.instagram_user_filled_24))
                    .circleCrop().into(ivProfile);
        }
    }

    public String getURLForResource (int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
    }

    private void initializeViews(View view) {
        fragmentManager = getParentFragmentManager();
        ivMap = view.findViewById(R.id.ivMap);
        ivProfile = view.findViewById(R.id.ivProfile);
        rvEvents = view.findViewById(R.id.rvEvents);
        fabCreate = view.findViewById(R.id.fabCreate);
        ivFilter = view.findViewById(R.id.ivFilter);
        pbLoading = view.findViewById(R.id.pbLoading);
        fabDate = view.findViewById(R.id.extFabDate);
        fabPrice = view.findViewById(R.id.extFabPrice);
        fabDistance = view.findViewById(R.id.extFabDistance);
        swipeContainer = view.findViewById(R.id.swipeContainer);
    }

    private void getUsersTags() {
        JSONArray tagg = ParseUser.getCurrentUser().getJSONArray("Interests");
        for (int i = 0; i < tagg.length(); i++) {
            try {
                tags.add(tagg.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
    }

    private void setClickListeners() {
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), HomeMapActivity.class);
                i.putExtra("results", Parcels.wrap(results));
                startActivity(i);
            }
        });
        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterFragment filterFragment = new FilterFragment();
                Bundle bundle = new Bundle();
                bundle.putString("sort_metric",currentlySelected);
                bundle.putString("max_distance", String.valueOf(max_distance));
                bundle.putString("max_price", String.valueOf(max_price));
                bundle.putBoolean("friends",filterfriends);
                bundle.putStringArrayList("tags", (ArrayList<String>) tags);
                filterFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.flContainer, filterFragment).commit();
            }
        });
        fabDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentlySelected.equals(date_string)) {
                    setActiveButton(currentlySelected,date_string,fabDate,true);
                }
            }
        });
        fabDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentlySelected.equals(distance_string)) {
                    setActiveButton(currentlySelected,distance_string,fabDistance,true);
                }
            }
        });
        fabPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentlySelected.equals(price_string)) {
                    setActiveButton(currentlySelected,price_string,fabPrice,true);
                }
            }
        });
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AddEventActivity.class);
                i.putExtra("function","add");
                startActivity(i);
            }
        });
    }

    private void setActiveButton(String previous, String current, ExtendedFloatingActionButton fabButton, Boolean sort) {
        switch (previous) {
            case date_string:
                fabDate.setBackgroundColor(Color.WHITE);
                fabDate.setTextColor(Color.BLACK);
                break;
            case distance_string:
                fabDistance.setBackgroundColor(Color.WHITE);
                fabDistance.setTextColor(Color.BLACK);
                break;
            case price_string:
                fabPrice.setBackgroundColor(Color.WHITE);
                fabPrice.setTextColor(Color.BLACK);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + previous);
        }
        currentlySelected=current;
        fabButton.setBackgroundColor(getContext().getColor(R.color.colorAccent));
        fabButton.setTextColor(Color.WHITE);
        pbLoading.setVisibility(View.VISIBLE);
        if (sort) {
            sort();
        }
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
                            Log.i(TAG,"location retrieved");
                            lastKnownLocation = task.getResult();
                            InitialQuery();
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
        Log.i(TAG, "Querying");
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_ORGANIZER);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "problem!", e);
                }
                for (Event event : events) {
                    Log.i(TAG,event.getEventName());
                    long now = System.currentTimeMillis();
                    Date datetime = null;
                    try {
                        datetime = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.ENGLISH)
                                .parse(event.getDate()+" "+event.getTime());
                    } catch (java.text.ParseException ex) {
                        ex.printStackTrace();
                    }
                    long dateInMillies = datetime.getTime();
                    if (dateInMillies > now) {
                        event.setDistance(event.getLocation()
                                .distanceInKilometersTo(new ParseGeoPoint(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude())));
                        results.add(event);
                    }
                    else{
                        event.setAttendees(new JSONArray());
                        event.saveInBackground();
                    }
                }
                sort();
                CountActiveEvents();
            }
        });
        swipeContainer.setRefreshing(false);
    }

    private void sort() {
        eventAdapter.clear();
        MergeSort m = new MergeSort(currentlySelected);
        m.mergeSort(results);
        filter();
        eventAdapter.addAll(results);
        eventAdapter.notifyDataSetChanged();
        rvEvents.scheduleLayoutAnimation();
        rvEvents.smoothScrollToPosition(0);
        pbLoading.setVisibility(View.INVISIBLE);
    }

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
        if(filterfriends){
            Log.i(TAG,"filtering by friends");
            List<Event> filtered = filterByFriends(results);
            results = filtered;
        }
    }

    private List<Event> filterByFriends(List<Event> events) {
        List<Event> friendEvents = new ArrayList<>();
        String friends = ParseUser.getCurrentUser().getJSONArray("Friends").toString();
        for (Event event : events){
            JSONArray attendees = event.getAttendees();
            for (int i = 0; i < attendees.length(); i++){
                try {
                    if (friends.contains(attendees.getString(i))){
                        friendEvents.add(event);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return friendEvents;
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
            if (event.getPrice() < max_price) {
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

    private void CountActiveEvents(){
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_ORGANIZER);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "problem!", e);
                }
                for (Event event : events) {
                    JSONArray attendees = event.getAttendees();
                    for (int i = 0; i < attendees.length(); i++) {
                        String userID;
                        try {
                            userID = attendees.getString(i);
                            if (userID.equals(ParseUser.getCurrentUser().getObjectId())) {
                                count++;
                                break;
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                Log.i(TAG,String.valueOf(count));
                ParseUser.getCurrentUser().put("ActiveEvents",count);
                ParseUser.getCurrentUser().saveInBackground();
            }
        });
    }
}