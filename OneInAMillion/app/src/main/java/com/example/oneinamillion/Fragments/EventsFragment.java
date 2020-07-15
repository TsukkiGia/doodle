package com.example.oneinamillion.Fragments;

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

import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.EventAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

//<a href="https://iconscout.com/icons/dropdown" target="_blank">Dropdown Icon</a> by <a href="https://iconscout.com/contributors/google-inc" target="_blank">Google Inc.</a>
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
        ivDropdownAttending = view.findViewById(R.id.ivDropdownAttending);
        ivDropdownOrganized = view.findViewById(R.id.ivDropdownOrganizing);
        attendingEvents = new ArrayList<>();
        organizedEvents = new ArrayList<>();
        eventAdapterForAttending = new EventAdapter(getContext(), attendingEvents);
        eventAdapterForOrganized = new EventAdapter(getContext(), organizedEvents);
        rvUpcoming.setAdapter(eventAdapterForAttending);
        rvCreated.setAdapter(eventAdapterForOrganized);
        rvCreated.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
        tvMyOrganizedEvents = view.findViewById(R.id.tvMyOrganizedEvents);
        tvMyUpcomingEvents = view.findViewById(R.id.tvMyUpcomingEvents);
        ivDropdownOrganized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rvCreated.getVisibility() == View.GONE) {
                    rvCreated.setVisibility(View.VISIBLE);
                    ivDropdownOrganized.setImageDrawable(getContext().getResources().getDrawable(R.drawable.close_dropdown));
                } else {
                    rvCreated.setVisibility(View.GONE);
                    ivDropdownOrganized.setImageDrawable(getContext().getResources().getDrawable(R.drawable.dropdown));
                }
            }
        });
        ivDropdownAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rvUpcoming.getVisibility() == View.GONE) {
                    rvUpcoming.setVisibility(View.VISIBLE);
                    ivDropdownAttending.setImageDrawable(getContext().getResources().getDrawable(R.drawable.close_dropdown));
                } else {
                    rvUpcoming.setVisibility(View.GONE);
                    ivDropdownAttending.setImageDrawable(getContext().getResources().getDrawable(R.drawable.dropdown));
                }
            }
        });
        tvMyOrganizedEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rvCreated.getVisibility() == View.GONE) {
                    rvCreated.setVisibility(View.VISIBLE);
                    ivDropdownOrganized.setImageDrawable(getContext().getResources().getDrawable(R.drawable.close_dropdown));
                } else {
                    rvCreated.setVisibility(View.GONE);
                    ivDropdownOrganized.setImageDrawable(getContext().getResources().getDrawable(R.drawable.dropdown));
                }
            }
        });
        tvMyUpcomingEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rvUpcoming.getVisibility() == View.GONE) {
                    rvUpcoming.setVisibility(View.VISIBLE);
                    ivDropdownAttending.setImageDrawable(getContext().getResources().getDrawable(R.drawable.close_dropdown));
                } else {
                    rvUpcoming.setVisibility(View.GONE);
                    ivDropdownAttending.setImageDrawable(getContext().getResources().getDrawable(R.drawable.dropdown));
                }
            }
        });
        queryEvents();
    }

    //modify queries
    private void queryEvents() {
        List<Event> attendingEvents = new ArrayList<>();
        List<Event> organizedEvents = new ArrayList<>();
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
                    Log.i(TAG, "Description: " + event.getDescription());
                }
                eventAdapterForAttending.clear();
                eventAdapterForAttending.addAll(events);
                eventAdapterForAttending.notifyDataSetChanged();

                eventAdapterForOrganized.clear();
                eventAdapterForOrganized.addAll(events);
                eventAdapterForOrganized.notifyDataSetChanged();
            }
        });
    }
}