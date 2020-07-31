package com.example.oneinamillion.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oneinamillion.EventMapActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.EventAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsFragment extends Fragment {
    RecyclerView rvCreated;
    RecyclerView rvUpcoming;
    TextView tvMyUpcomingEvents;
    TextView tvMyOrganizedEvents;
    public static final String TAG = "EventsFragment";
    EventAdapter eventAdapterForAttending;
    EventAdapter eventAdapterForOrganized;
    List<Event> attendingEvents;
    List<Event> organizedEvents;
    ImageView ivDropdownOrganized;
    ImageView ivDropdownAttending;
    ImageView ivMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setUpViews();
        queryEvents();
    }

    private void setUpViews() {
        attendingEvents = new ArrayList<>();
        organizedEvents = new ArrayList<>();
        eventAdapterForAttending = new EventAdapter(getContext(), attendingEvents);
        eventAdapterForOrganized = new EventAdapter(getContext(), organizedEvents);
        rvUpcoming.setAdapter(eventAdapterForAttending);
        rvCreated.setAdapter(eventAdapterForOrganized);
        rvCreated.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), EventMapActivity.class);
                startActivity(i);
                //getParentFragmentManager().beginTransaction()
                //        .replace(R.id.flContainer,new CalendarFragment()).commit();
            }
        });
        ivDropdownOrganized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleDropdown(rvCreated,ivDropdownOrganized);
            }
        });
        ivDropdownAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleDropdown(rvUpcoming,ivDropdownAttending);
            }
        });
        tvMyOrganizedEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDropdown(rvCreated,ivDropdownOrganized);
            }
        });
        tvMyUpcomingEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDropdown(rvUpcoming,ivDropdownAttending);
            }
        });
    }

    private void initializeViews(View view) {
        rvCreated = view.findViewById(R.id.rvCreated);
        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        ivMap = view.findViewById(R.id.ivMap);
        tvMyOrganizedEvents = view.findViewById(R.id.tvMyOrganizedEvents);
        tvMyUpcomingEvents = view.findViewById(R.id.tvMyUpcomingEvents);
        ivDropdownAttending = view.findViewById(R.id.ivDropdownAttending);
        ivDropdownOrganized = view.findViewById(R.id.ivDropdownOrganizing);
    }

    private void toggleDropdown(RecyclerView recyclerView, ImageView ivArrow) {
        if (recyclerView.getVisibility() == View.GONE) {
            recyclerView.setVisibility(View.VISIBLE);
            ivArrow.setImageDrawable(getContext().getDrawable(R.drawable.close_dropdown));
        } else {
            recyclerView.setVisibility(View.GONE);
            ivArrow.setImageDrawable(getContext().getDrawable(R.drawable.dropdown));
        }
    }

    private void queryEvents() {
        final List<Event> attendingEvents = new ArrayList<>();
        final List<Event> organizedEvents = new ArrayList<>();
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_ORGANIZER);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "problem!", e);
                }
                for (Event event : events) {
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
                        ParseUser parseUser = event.getOrganizer();
                        if (parseUser.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                            organizedEvents.add(event);
                        }
                        JSONArray attendees = event.getAttendees();
                        for (int i = 0; i < attendees.length(); i++) {
                            String userID;
                            try {
                                userID = attendees.getString(i);
                                if (userID.equals(ParseUser.getCurrentUser().getObjectId())) {
                                    attendingEvents.add(event);
                                    break;
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
                eventAdapterForAttending.clear();
                eventAdapterForAttending.addAll(attendingEvents);
                eventAdapterForAttending.notifyDataSetChanged();
                eventAdapterForOrganized.clear();
                eventAdapterForOrganized.addAll(organizedEvents);
                eventAdapterForOrganized.notifyDataSetChanged();
            }
        });
    }
}