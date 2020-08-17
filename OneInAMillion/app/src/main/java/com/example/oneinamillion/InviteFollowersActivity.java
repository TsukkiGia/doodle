package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.adapters.InviteFollowersAdapter;
import com.example.oneinamillion.adapters.LikersAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

public class InviteFollowersActivity extends AppCompatActivity {
    RecyclerView rvFollowers;
    List<ParseUser> followers;
    InviteFollowersAdapter adapter;
    ParseUser currentUser;
    Event event;
    ProgressBar pbLoading;
    TextView tvNoFollowers;
    Button btnInvite;
    String address;
    public static final String TAG = "InviteFollowers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        followers = new ArrayList<>();
        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        currentUser = ParseUser.getCurrentUser();
        setContentView(R.layout.activity_invite_followers);
        pbLoading = findViewById(R.id.pbLoading);
        tvNoFollowers = findViewById(R.id.tvNoFollowers);
        rvFollowers = findViewById(R.id.rvFollowers);
        btnInvite = findViewById(R.id.btnInvite);
        btnInvite = findViewById(R.id.btnInvite);
        address = event.getParseAddress();
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> emails = adapter.getFollowersToInvite();
                for (String email:emails) {
                    BackgroundMail.newBuilder(InviteFollowersActivity.this)
                    .withUsername("aprilgtropse@gmail.com")
                    .withPassword("FinnBalor")
                    .withProcessVisibility(false)
                    .withMailto(email)
                    .withType(BackgroundMail.TYPE_PLAIN)
                    .withSubject("Invite to my event")
                    .withBody("Hi! On " + event.getDate() + " at " + event.getTime() + ", I will be hosting " + event.getEventName()
                            + " and I will love for you to join! This event would be taking place at " + address + ". I hope to see you there!")
                    .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                        @Override
                        public void onSuccess() {
                        }
                    })
                    .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                        @Override
                        public void onFail() {
                        }
                    })
                    .send();
                }
                btnInvite.setText("Invited");
                btnInvite.setBackgroundColor(Color.GRAY);
                btnInvite.setTextColor(Color.WHITE);
            }
        });
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
                if (followers.size()==0){
                    tvNoFollowers.setVisibility(View.VISIBLE);
                }
                Log.i(TAG,followers.toString());
                adapter = new InviteFollowersAdapter(InviteFollowersActivity.this,followers,event);
                rvFollowers.setAdapter(adapter);
                rvFollowers.setLayoutManager(new LinearLayoutManager(InviteFollowersActivity.this));
                pbLoading.setVisibility(View.GONE);
            }
        });
    }
}