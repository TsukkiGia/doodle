package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class InterestActivity extends AppCompatActivity {
    ExtendedFloatingActionButton fabSports;
    ExtendedFloatingActionButton fabConcerts;
    ExtendedFloatingActionButton fabAuction;
    ExtendedFloatingActionButton fabRaffle;
    ExtendedFloatingActionButton fabCooking;
    ExtendedFloatingActionButton fabGalas;
    ExtendedFloatingActionButton fabCrafts;
    ExtendedFloatingActionButton fabAthons;
    String raffle_tag = "raffle";
    String thon_tag= "thon";
    String sport_tag = "sport";
    String auctions_tag = "auction";
    String cook_tag = "cook";
    String concert_tag = "music";
    String gala_tag = "gala";
    String craft_tag = "craft";
    String previousactivity;
    JSONArray interests = new JSONArray();
    Button btnConfirm;
    public static final String TAG = "InterestActivityy";
    String string_signup = "signup";
    String string_profile = "profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        btnConfirm = findViewById(R.id.btnConfirm);
        fabSports = findViewById(R.id.extFabSports);
        fabConcerts = findViewById(R.id.extFabConcerts);
        fabAuction = findViewById(R.id.extFabAuction);
        fabRaffle = findViewById(R.id.extFabRaffles);
        fabCooking = findViewById(R.id.extFabCooking);
        fabGalas = findViewById(R.id.extFabGalas);
        fabCrafts = findViewById(R.id.extFabCrafts);
        fabAthons = findViewById(R.id.extFabAthons);
        previousactivity = getIntent().getStringExtra("activity");
        if (previousactivity.equals(string_profile)) {
            JSONArray tagg = ParseUser.getCurrentUser().getJSONArray("Interests");
            for (int i = 0; i < tagg.length(); i++) {
                try {
                    interests.put(tagg.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
            for (int i = 0; i < interests.length(); i++) {
                try {
                    if (interests.get(i).equals(sport_tag)){
                        fabSports.setTextColor(Color.WHITE);
                        fabSports.setBackgroundColor(getColor(R.color.colorSportButton));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (interests.get(i).equals(auctions_tag)){
                        fabAuction.setTextColor(Color.WHITE);
                        fabAuction.setBackgroundColor(getColor(R.color.colorAuctionsButton));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (interests.get(i).equals(concert_tag)){
                        fabConcerts.setTextColor(Color.WHITE);
                        fabConcerts.setBackgroundColor(getColor(R.color.colorMusicButton));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (interests.get(i).equals(gala_tag)){
                        fabGalas.setTextColor(Color.WHITE);
                        fabGalas.setBackgroundColor(getColor(R.color.colorGalaButton));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (interests.get(i).equals(raffle_tag)){
                        fabRaffle.setTextColor(Color.WHITE);
                        fabRaffle.setBackgroundColor(getColor(R.color.colorRaffleButton));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (interests.get(i).equals(cook_tag)){
                        fabCooking.setTextColor(Color.WHITE);
                        fabCooking.setBackgroundColor(getColor(R.color.colorCookButton));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (interests.get(i).equals(craft_tag)){
                        fabCrafts.setTextColor(Color.WHITE);
                        fabCrafts.setBackgroundColor(getColor(R.color.colorCraftButton));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (interests.get(i).equals(thon_tag)){
                        fabAthons.setTextColor(Color.WHITE);
                        fabAthons.setBackgroundColor(getColor(R.color.colorThonButton));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        fabSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabSports,sport_tag,R.color.colorSportButton);
            }
        });
        fabConcerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabConcerts,concert_tag,R.color.colorMusicButton);
            }
        });
        fabAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabAuction,auctions_tag,R.color.colorAuctionsButton);
            }
        });
        fabRaffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabRaffle,raffle_tag,R.color.colorRaffleButton);
            }
        });
        fabCooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabCooking,cook_tag,R.color.colorCookButton);
            }
        });
        fabGalas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabGalas,gala_tag,R.color.colorGalaButton);
            }
        });
        fabCrafts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabCrafts,craft_tag,R.color.colorCraftButton);
            }
        });
        fabAthons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabAthons,thon_tag,R.color.colorThonButton);
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser parseUser = ParseUser.getCurrentUser();
                parseUser.put("Interests",interests);
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.i(TAG, "Done!");
                        if (previousactivity.equals(string_signup)) {
                            Intent i = new Intent(InterestActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else {
                            finish();
                        }
                    }
                });
            }
        });
    }
    private void fabclicked(ExtendedFloatingActionButton button, String tag, int color) {
        Boolean inside = false;
        for (int i = 0; i < interests.length(); i++) {
            try {
                if (interests.get(i).equals(tag)) {
                    inside = true;
                    interests.remove(i);
                    button.setTextColor(Color.BLACK);
                    button.setBackgroundColor(Color.WHITE);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!inside) {
            interests.put(tag);
            button.setTextColor(Color.WHITE);
            button.setBackgroundColor(getColor(color));
        }
        Log.i(TAG, interests.toString());
    }
}