package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.oneinamillion.adapters.LikersAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LikersActivity extends AppCompatActivity {
    RecyclerView rvLikers;
    List<ParseUser> likers;
    String likersString;
    LikersAdapter likersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likers);
        rvLikers = findViewById(R.id.rvLikers);
        likers = new ArrayList<>();
        likersString = getIntent().getStringExtra("likers");
        userquery();
    }

    private void userquery() {
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                for (ParseUser user : users){
                    if (likersString.contains(user.getObjectId())){
                        likers.add(user);
                    }
                }
                likersAdapter = new LikersAdapter(LikersActivity.this,likers);
                rvLikers.setAdapter(likersAdapter);
                rvLikers.setLayoutManager(new LinearLayoutManager(LikersActivity.this));
            }
        });
    }
}