package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.adapters.HorizontalEventAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfilePageActivity extends AppCompatActivity {
    ImageView ivProfilePicture;
    TextView tvUsername;
    TextView tvName;
    Button btnAddFriend;
    Boolean friended=false;
    ParseUser user;
    public static final String TAG = "ProfilePageActivity";
    List<Event> eventsOrganized;
    List<Event> eventsAttended;
    RecyclerView rvOrganized;
    RecyclerView rvAttended;
    HorizontalEventAdapter OrganizedEventAdapter;
    HorizontalEventAdapter AttendedEventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        eventsOrganized = new ArrayList<>();
        eventsAttended = new ArrayList<>();
        rvOrganized = findViewById(R.id.rvOrganized);
        rvAttended = findViewById(R.id.rvAttended);
        LinearLayoutManager OrganizedlayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager AttendedlayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        OrganizedEventAdapter = new HorizontalEventAdapter(ProfilePageActivity.this,eventsOrganized);
        AttendedEventAdapter = new HorizontalEventAdapter(ProfilePageActivity.this,eventsAttended);
        rvOrganized.setLayoutManager(OrganizedlayoutManager);
        rvOrganized.setAdapter(OrganizedEventAdapter);
        rvAttended.setLayoutManager(AttendedlayoutManager);
        rvAttended.setAdapter(AttendedEventAdapter);
        ivProfilePicture = findViewById(R.id.ivProfile);
        tvUsername = findViewById(R.id.tvUsername);
        tvName = findViewById(R.id.tvName);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        queryEvents();
        tvUsername.setText(user.getUsername());
        tvName.setText(user.getString("FirstName") + " "+user.getString("LastName"));
        if (user.getParseFile("ProfileImage") != null) {
            Glide.with(ProfilePageActivity.this).load(user.getParseFile("ProfileImage").getUrl())
                    .circleCrop().into(ivProfilePicture);
        } else {
            ivProfilePicture.setImageDrawable(getDrawable(R.drawable.instagram_user_filled_24));
        }
        JSONArray friends = ParseUser.getCurrentUser().getJSONArray("Friends");
        for (int i = 0; i < friends.length();i++){
            try {

                if (friends.get(i).equals(user.getObjectId())){
                    btnAddFriend.setText("Friends");
                    friended = true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,friended.toString());
                if (friended) {
                    int index = 0;
                    JSONArray friends = ParseUser.getCurrentUser().getJSONArray("Friends");
                    for (int i = 0; i < friends.length();i++){
                        try {
                            if (friends.get(i).equals(user.getObjectId())){
                                index = i;
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    friends.remove(index);
                    ParseUser.getCurrentUser().put("Friends",friends);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i(TAG, "done");
                            }
                            else {
                                Log.e(TAG,"help!",e);
                            }
                        }
                    });
                    btnAddFriend.setText("Add Friend");
                    friended = false;
                }
                else {
                    JSONArray friends = ParseUser.getCurrentUser().getJSONArray("Friends");
                    friends.put(user.getObjectId());
                    ParseUser.getCurrentUser().put("Friends",friends);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i(TAG, "done");
                            }
                            else {
                                Log.e(TAG,"help!",e);
                            }
                        }
                    });
                    btnAddFriend.setText("Friends");
                    friended = true;
                }
            }
        });

    }

    private void queryEvents() {
        ParseQuery<Event> parseQuery = ParseQuery.getQuery(Event.class);
        final List<Event> organized = new ArrayList<>();
        final List<Event> attended = new ArrayList<>();
        parseQuery.include(Event.KEY_ORGANIZER);
        parseQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                for (Event event: events){
                    String attendees = event.getAttendees().toString();
                    if (attendees.contains(user.getObjectId())){
                        attended.add(event);
                    }
                    if (event.getOrganizer().getObjectId().equals(user.getObjectId())){
                        organized.add(event);
                    }
                    Log.i(TAG,"first"+ attended.toString());
                    Log.i(TAG,"first"+organized.toString());
                }
                AttendedEventAdapter.clear();
                AttendedEventAdapter.addAll(attended);
                AttendedEventAdapter.notifyDataSetChanged();
                OrganizedEventAdapter.clear();
                OrganizedEventAdapter.addAll(organized);
                OrganizedEventAdapter.notifyDataSetChanged();
            }
        });
    }

    private void eventsAttendedbyUser(){

    }

    private void eventsOrganizedbyUser(){
        ParseQuery<Event> parseQuery = ParseQuery.getQuery(Event.class);
        parseQuery.include(Event.KEY_ORGANIZER);
        parseQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                for (Event event: events){
                    Log.i(TAG,event.getEventName());
                    if (event.getOrganizer().getObjectId().equals(user.getObjectId())){
                        eventsOrganized.add(event);
                    }
                }
            }
        });
        OrganizedEventAdapter.clear();
        OrganizedEventAdapter.addAll(eventsOrganized);
    }
}