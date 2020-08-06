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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.oneinamillion.EventMapActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.EventAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarEventFragment extends Fragment {
    String date;
    TextView tvHeader;
    RecyclerView rvCalendarEvents;
    public static final String TAG = "CalendarEvents";
    EventAdapter adapter;
    List<Event> results;
    TextView tvNoEvents;
    ProgressBar pbLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            date = getArguments().getString("date");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setUpViews();
        InitialQuery();
    }

    private void setUpViews() {
        results = new ArrayList<>();
        adapter = new EventAdapter(getContext(),results);
        rvCalendarEvents.setAdapter(adapter);
        rvCalendarEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        Date currentDate = new Date(System.currentTimeMillis());
        String currentDateinString = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(currentDate);
        if (date.equals(currentDateinString)){
            tvHeader.setText(R.string.events_today);
        }
        else {
            tvHeader.setText("Events for "+date);
        }
    }

    private void initializeViews(View view) {
        tvHeader = view.findViewById(R.id.tvHeader);
        rvCalendarEvents = view.findViewById(R.id.rvCalendarEvents);
        tvNoEvents = view.findViewById(R.id.tvNoEvents);
        pbLoading = view.findViewById(R.id.pbLoading);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_event, container, false);
    }

    private void InitialQuery() {
        adapter.clear();
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
                    if (event.getDate().equals(date)) {
                        results.add(event);
                    }
                }
                if (results.size()==0) {
                    tvNoEvents.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                pbLoading.setVisibility(View.GONE);
            }
        });
    }
}