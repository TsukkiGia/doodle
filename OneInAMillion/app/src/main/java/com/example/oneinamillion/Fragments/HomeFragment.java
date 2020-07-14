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

import com.example.oneinamillion.AddEventActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.EventAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
//https://icons8.com

public class HomeFragment extends Fragment {
    RecyclerView rvEvents;
    EventAdapter eventAdapter;
    List<Event> events;
    FloatingActionButton fabCreate;
    public static final String TAG = "HomeFragment";

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
        rvEvents = view.findViewById(R.id.rvEvents);
        fabCreate = view.findViewById(R.id.fabCreate);
        events = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(),events);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEvents.setAdapter(eventAdapter);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });
        queryEvents();
    }

    private void showEditDialog() {
        Intent i = new Intent(getContext(), AddEventActivity.class);
        startActivity(i);
    }

    private void queryEvents() {
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
            }
        });
    }
}