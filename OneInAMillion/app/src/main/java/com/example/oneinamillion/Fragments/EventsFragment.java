package com.example.oneinamillion.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oneinamillion.R;


public class EventsFragment extends Fragment {

    RecyclerView rvCreated;
    RecyclerView rvUpcoming;
    TextView tvMyUpcomingEvents;
    TextView tvMyOrganizedEvents;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCreated = view.findViewById(R.id.rvCreated);
        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        tvMyOrganizedEvents = view.findViewById(R.id.tvMyOrganizedEvents);
        tvMyUpcomingEvents = view.findViewById(R.id.tvMyUpcomingEvents);
        tvMyOrganizedEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvCreated.setVisibility(View.VISIBLE);
    }
        });
        tvMyUpcomingEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvUpcoming.setVisibility(View.VISIBLE);
            }
        });

        queryEvents();
    }

    private void queryEvents() {

    }
}
