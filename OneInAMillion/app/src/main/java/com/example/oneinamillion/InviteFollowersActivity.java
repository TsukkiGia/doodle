package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.adapters.InviteFollowersAdapter;
import com.example.oneinamillion.adapters.LikersAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InviteFollowersActivity extends AppCompatActivity {
    RecyclerView rvFollowers;
    List<ParseUser> followers;
    InviteFollowersAdapter adapter;
    ParseUser currentUser;
    Event event;
    public static final String TAG = "InviteFollowers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        followers = new ArrayList<>();
        currentUser = ParseUser.getCurrentUser();
        setContentView(R.layout.activity_invite_followers);
        rvFollowers = findViewById(R.id.rvFollowers);
        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        userquery();
    }

    private void userquery() {
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                for (ParseUser user : users) {
                    if (user.getJSONArray("Friends").toString().contains(currentUser.getObjectId())) {
                        followers.add(user);
                    }
                }
                Log.i(TAG,followers.toString());
                adapter = new InviteFollowersAdapter(InviteFollowersActivity.this,followers,event);
                rvFollowers.setAdapter(adapter);
                rvFollowers.setLayoutManager(new LinearLayoutManager(InviteFollowersActivity.this));
            }
        });
    }
}