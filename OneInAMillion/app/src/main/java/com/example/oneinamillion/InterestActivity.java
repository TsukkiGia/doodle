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
    ExtendedFloatingActionButton fabExhibits;
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
    JSONArray interests = new JSONArray();
    Button btnConfirm;
    public static final String TAG = "InterestActivityy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        btnConfirm = findViewById(R.id.btnConfirm);
        fabSports = findViewById(R.id.extFabSports);
        fabConcerts = findViewById(R.id.extFabConcerts);
        fabAuction = findViewById(R.id.extFabAuction);
        fabRaffle = findViewById(R.id.extFabRaffles);
        fabExhibits = findViewById(R.id.extFabArtExhibits);
        fabGalas = findViewById(R.id.extFabGalas);
        fabCrafts = findViewById(R.id.extFabCrafts);
        fabAthons = findViewById(R.id.extFabAthons);
        fabSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside = false;
                for (int i = 0; i < interests.length(); i++) {
                    try {
                        if (interests.get(i).equals(sport_tag)){
                            inside = true;
                            interests.remove(i);
                            fabSports.setTextColor(Color.BLACK);
                            fabSports.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put(sport_tag);
                    fabSports.setTextColor(Color.WHITE);
                    fabSports.setBackgroundColor(getColor(R.color.colorSportButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabConcerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside = false;
                for (int i = 0; i < interests.length(); i++) {
                    try {
                        if (interests.get(i).equals(concert_tag)){
                            inside = true;
                            interests.remove(i);
                            fabConcerts.setTextColor(Color.BLACK);
                            fabConcerts.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put(concert_tag);
                    fabConcerts.setTextColor(Color.WHITE);
                    fabConcerts.setBackgroundColor(getColor(R.color.colorMusicButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside = false;
                for (int i = 0; i < interests.length(); i++) {
                    try {
                        if (interests.get(i).equals(auctions_tag)){
                            inside = true;
                            interests.remove(i);
                            fabAuction.setTextColor(Color.BLACK);
                            fabAuction.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put(auctions_tag);
                    fabAuction.setTextColor(Color.WHITE);
                    fabAuction.setBackgroundColor(getColor(R.color.colorAuctionsButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabRaffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside = false;
                for (int i = 0; i < interests.length(); i++) {
                    try {
                        if (interests.get(i).equals(raffle_tag)){
                            inside = true;
                            interests.remove(i);
                            fabRaffle.setTextColor(Color.BLACK);
                            fabRaffle.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put(raffle_tag);
                    fabRaffle.setTextColor(Color.WHITE);
                    fabRaffle.setBackgroundColor(getColor(R.color.colorRaffleButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabExhibits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside = false;
                for (int i = 0; i < interests.length(); i++) {
                    try {
                        if (interests.get(i).equals(cook_tag)){
                            inside = true;
                            interests.remove(i);
                            fabExhibits.setTextColor(Color.BLACK);
                            fabExhibits.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put(cook_tag);
                    fabExhibits.setTextColor(Color.WHITE);
                    fabExhibits.setBackgroundColor(getColor(R.color.colorCookButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabGalas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside = false;
                for (int i = 0; i < interests.length(); i++) {
                    try {
                        if (interests.get(i).equals(gala_tag)){
                            inside = true;
                            interests.remove(i);
                            fabGalas.setTextColor(Color.BLACK);
                            fabGalas.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put(gala_tag);
                    fabGalas.setTextColor(Color.WHITE);
                    fabGalas.setBackgroundColor(getColor(R.color.colorGalaButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabCrafts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside = false;
                for (int i = 0; i < interests.length(); i++) {
                    try {
                        if (interests.get(i).equals(craft_tag)){
                            inside = true;
                            interests.remove(i);
                            fabCrafts.setTextColor(Color.BLACK);
                            fabCrafts.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put(craft_tag);
                    fabCrafts.setTextColor(Color.WHITE);
                    fabCrafts.setBackgroundColor(getColor(R.color.colorCraftButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabAthons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside = false;
                for (int i = 0; i <interests.length(); i++) {
                    try {
                        if (interests.get(i).equals(thon_tag)){
                            inside = true;
                            interests.remove(i);
                            fabAthons.setTextColor(Color.BLACK);
                            fabAthons.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put(thon_tag);
                    fabAthons.setTextColor(Color.WHITE);
                    fabAthons.setBackgroundColor(getColor(R.color.colorThonButton));
                }
                Log.i(TAG,interests.toString());
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
                        Intent i = new Intent(InterestActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        });
    }
}