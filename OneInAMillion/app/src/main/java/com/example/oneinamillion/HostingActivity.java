package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.MergeSort;
import com.example.oneinamillion.adapters.HostViewAdapter;
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

public class HostingActivity extends AppCompatActivity {
    public static final String TAG = "HostingActivity";
    HostViewAdapter adapter;
    List<Event> events;
    RecyclerView rvHostEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosting);
        rvHostEvents = findViewById(R.id.rvHostEvents);
        rvHostEvents.setLayoutManager(new LinearLayoutManager(HostingActivity.this));
        events = new ArrayList<>();
        adapter = new HostViewAdapter(HostingActivity.this,events);
        rvHostEvents.setAdapter(adapter);
        queryEvents();
    }

    private void queryEvents() {
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
                                .parse(event.getDate() + " " + event.getTime());
                    } catch (java.text.ParseException ex) {
                        ex.printStackTrace();
                    }
                    long dateInMillies = datetime.getTime();
                    if (dateInMillies > now) {
                        ParseUser parseUser = event.getOrganizer();
                        if (parseUser.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                            organizedEvents.add(event);
                        }
                    }
                }
                adapter.clear();
                MergeSort m = new MergeSort("date");
                m.mergeSort(organizedEvents);
                adapter.addAll(organizedEvents);
                adapter.notifyDataSetChanged();
            }
        });
    }
}