package com.example.oneinamillion.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.oneinamillion.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

//<a target="_blank" href="https://icons8.com/icons/set/back">Back icon</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>

public class FilterFragment extends Fragment {
    ImageView ivBack;
    ExtendedFloatingActionButton fabSports;
    ExtendedFloatingActionButton fabConcerts;
    ExtendedFloatingActionButton fabAuction;
    ExtendedFloatingActionButton fabRaffle;
    ExtendedFloatingActionButton fabExhibits;
    ExtendedFloatingActionButton fabGalas;
    ExtendedFloatingActionButton fabCrafts;
    ExtendedFloatingActionButton fabAthons;
    TextView tvValuePrice;
    String raffle_tag = "raffle";
    String thon_tag= "thon";
    String sport_tag = "sport";
    String auctions_tag = "auction";
    String cook_tag = "cook";
    String concert_tag = "music";
    String gala_tag = "gala";
    String craft_tag = "craft";
    double max_distance=100;
    double max_price=100;
    TextView tvValueDistance;
    SeekBar sbDistance;
    SeekBar sbPrice;
    String sort_metric;
    List<String> interests = new ArrayList<>();
    public static final String TAG = "FilterFragment";

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            Log.i(TAG, getArguments().toString());
            sort_metric=getArguments().getString("sort_metric");
            Log.i(TAG,sort_metric);
            max_distance=Double.valueOf(getArguments().getString("max_distance"));
            max_price=Double.valueOf(getArguments().getString("max_price"));
            interests=getArguments().getStringArrayList("tags");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabSports = view.findViewById(R.id.extFabSports);
        fabConcerts = view.findViewById(R.id.extFabConcerts);
        fabAuction = view.findViewById(R.id.extFabAuction);
        fabRaffle = view.findViewById(R.id.extFabRaffles);
        fabExhibits = view.findViewById(R.id.extFabArtExhibits);
        fabGalas = view.findViewById(R.id.extFabGalas);
        fabCrafts = view.findViewById(R.id.extFabCrafts);
        fabAthons = view.findViewById(R.id.extFabAthons);
        Log.i(TAG,interests.toString());
        for (int i = 0; i <interests.size(); i++) {
            if (interests.get(i).equals(sport_tag)){
                fabSports.setTextColor(Color.WHITE);
                fabSports.setBackgroundColor(getContext().getColor(R.color.colorSportButton));
            }
            if (interests.get(i).equals(auctions_tag)){
                fabAuction.setTextColor(Color.WHITE);
                fabAuction.setBackgroundColor(getContext().getColor(R.color.colorAuctionsButton));
            }
            if (interests.get(i).equals(concert_tag)){
                fabConcerts.setTextColor(Color.WHITE);
                fabConcerts.setBackgroundColor(getContext().getColor(R.color.colorMusicButton));
            }
            if (interests.get(i).equals(gala_tag)){
                fabGalas.setTextColor(Color.WHITE);
                fabGalas.setBackgroundColor(getContext().getColor(R.color.colorGalaButton));
            }
            if (interests.get(i).equals(raffle_tag)){
                fabRaffle.setTextColor(Color.WHITE);
                fabRaffle.setBackgroundColor(getContext().getColor(R.color.colorRaffleButton));
            }
            if (interests.get(i).equals(cook_tag)){
                fabExhibits.setTextColor(Color.WHITE);
                fabExhibits.setBackgroundColor(getContext().getColor(R.color.colorCookButton));
            }
            if (interests.get(i).equals(craft_tag)){
                fabCrafts.setTextColor(Color.WHITE);
                fabCrafts.setBackgroundColor(getContext().getColor(R.color.colorCraftButton));
            }
            if (interests.get(i).equals(thon_tag)){
                fabAthons.setTextColor(Color.WHITE);
                fabAthons.setBackgroundColor(getContext().getColor(R.color.colorThonButton));
            }
        }
        ivBack = view.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("sort_metric", sort_metric);
                bundle.putString("max_distance", tvValueDistance.getText().toString());
                bundle.putString("max_price", tvValuePrice.getText().toString());
                bundle.putStringArrayList("tags", (ArrayList<String>) interests);
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(bundle);
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, homeFragment).commit();
            }
        });
        tvValueDistance = view.findViewById(R.id.tvValueDistance);
        tvValuePrice = view.findViewById(R.id.tvValuePrice);
        sbDistance = view.findViewById(R.id.sbDistance);
        tvValueDistance.setText(String.valueOf(max_distance));
        tvValuePrice.setText(String.valueOf(max_price));
        sbPrice = view.findViewById(R.id.sbPrice);
        sbPrice.setX((float) max_price);
        sbDistance.setX((float) max_distance);
        sbPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvValuePrice.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvValueDistance.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        fabSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals(sport_tag)){
                        inside=true;
                        interests.remove(i);
                        fabSports.setTextColor(Color.BLACK);
                        fabSports.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add(sport_tag);
                    fabSports.setTextColor(Color.WHITE);
                    fabSports.setBackgroundColor(getContext().getColor(R.color.colorSportButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabConcerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals(concert_tag)){
                        inside=true;
                        interests.remove(i);
                        fabConcerts.setTextColor(Color.BLACK);
                        fabConcerts.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add(concert_tag);
                    fabConcerts.setTextColor(Color.WHITE);
                    fabConcerts.setBackgroundColor(getContext().getColor(R.color.colorMusicButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals(auctions_tag)){
                        inside=true;
                        interests.remove(i);
                        fabAuction.setTextColor(Color.BLACK);
                        fabAuction.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add(auctions_tag);
                    fabAuction.setTextColor(Color.WHITE);
                    fabAuction.setBackgroundColor(getContext().getColor(R.color.colorAuctionsButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabRaffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i < interests.size(); i++) {
                    if (interests.get(i).equals(raffle_tag)){
                        inside=true;
                        interests.remove(i);
                        fabRaffle.setTextColor(Color.BLACK);
                        fabRaffle.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add(raffle_tag);
                    fabRaffle.setTextColor(Color.WHITE);
                    fabRaffle.setBackgroundColor(getContext().getColor(R.color.colorRaffleButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabExhibits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i < interests.size(); i++) {
                    if (interests.get(i).equals(cook_tag)){
                        inside=true;
                        interests.remove(i);
                        fabExhibits.setTextColor(Color.BLACK);
                        fabExhibits.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add(cook_tag);
                    fabExhibits.setTextColor(Color.WHITE);
                    fabExhibits.setBackgroundColor(getContext().getColor(R.color.colorCookButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabGalas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals(gala_tag)){
                        inside=true;
                        interests.remove(i);
                        fabGalas.setTextColor(Color.BLACK);
                        fabGalas.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add(gala_tag);
                    fabGalas.setTextColor(Color.WHITE);
                    fabGalas.setBackgroundColor(getContext().getColor(R.color.colorGalaButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabCrafts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals(craft_tag)){
                        inside=true;
                        interests.remove(i);
                        fabCrafts.setTextColor(Color.BLACK);
                        fabCrafts.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add(craft_tag);
                    fabCrafts.setTextColor(Color.WHITE);
                    fabCrafts.setBackgroundColor(getContext().getColor(R.color.colorCraftButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabAthons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals(thon_tag)){
                        inside=true;
                        interests.remove(i);
                        fabAthons.setTextColor(Color.BLACK);
                        fabAthons.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add(thon_tag);
                    fabAthons.setTextColor(Color.WHITE);
                    fabAthons.setBackgroundColor(getContext().getColor(R.color.colorThonButton));
                }
                Log.i(TAG,interests.toString());
            }
        });
    }
}