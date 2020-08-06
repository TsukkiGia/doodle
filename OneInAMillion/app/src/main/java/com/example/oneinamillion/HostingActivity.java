package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
    Boolean shown = false;
    ProgressBar pbLoading;
    Button btnInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosting);
        rvHostEvents = findViewById(R.id.rvHostEvents);
        pbLoading = findViewById(R.id.pbLoading);
        rvHostEvents.setLayoutManager(new LinearLayoutManager(HostingActivity.this));
        events = new ArrayList<>();
        adapter = new HostViewAdapter(HostingActivity.this,events);
        rvHostEvents.setAdapter(adapter);
        queryEvents();
        setupSwipeForOptions();
    }

    private void setupSwipeForOptions() {
        if (!shown) {
            ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.colorAccent));

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    adapter.showMenu(viewHolder.getAdapterPosition());
                    shown = true;
                    setupSwipeForOptions();
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    View itemView = viewHolder.itemView;
                    if (dX > 0) {
                        background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                    } else if (dX < 0) {
                        background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    } else {
                        background.setBounds(0, 0, 0, 0);
                    }

                    background.draw(c);
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
            itemTouchHelper.attachToRecyclerView(rvHostEvents);
        }
        else{
            ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.colorAccent));

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    adapter.closeMenu();
                    shown = false;
                    setupSwipeForOptions();
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
            itemTouchHelper.attachToRecyclerView(rvHostEvents);
        }
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
                pbLoading.setVisibility(View.GONE);
            }
        });
    }
}