package com.example.oneinamillion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.oneinamillion.Fragments.EventsFragment;
import com.example.oneinamillion.Fragments.HomeFragment;
import com.example.oneinamillion.Fragments.ProfileFragment;
import com.example.oneinamillion.Fragments.SearchFragment;
import com.example.oneinamillion.Models.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    String CHANNEL_ID = "120";
    int id = 0;
    //for wrapping to details activity
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //First create notification channel in order to send notification
        createNotificationChannel();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()){
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_search:
                        fragment = new SearchFragment();
                        break;
                    case R.id.action_events:
                        fragment = new EventsFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        queryEvents();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Event Notification";
            String description = "Reminder that an event is upcoming";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void queryEvents() {
        final List<Event> attendingEvents = new ArrayList<>();
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
                    for (int i = 0; i < attendees.length(); i++){
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
                checkifWithinHour(attendingEvents);
            }
        });
    }

    private void checkifWithinHour(List<Event> attendingEvents) {
        long minutesinHour = 60;
        long secondsinMinute = 60;
        long millisecondsinSecond = 1000;
        long millisHour = minutesinHour*secondsinMinute*millisecondsinSecond;
        for (Event event: attendingEvents) {
            try {
                Date DateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.ENGLISH)
                        .parse(event.getDate()+" "+event.getTime());
                long DateTimeinMillis = DateTime.getTime();
                long now = System.currentTimeMillis();
                if (DateTimeinMillis > now && (DateTimeinMillis-now) < millisHour) {
                    makeDetailedNotifications(event);
                }
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeDetailedNotifications(Event event) {
        //Create an explicit intent to define action once notification is tapped
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //Constructing structure of the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.red_marker)
                .setContentTitle("Event Notification")
                .setContentText(event.getEventName()+" is coming up within an hour!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, builder.build());
        id++;
    }
}