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
            sort_metric=getArguments().getString("sort_metric");
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
        JSONArray interests_fromUser = ParseUser.getCurrentUser().getJSONArray("Interests");
        for (int i = 0; i < interests_fromUser.length(); i++) {
            try {
                interests.add(interests_fromUser.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Log.i(TAG,interests.toString());
        for (int i = 0; i <interests.size(); i++) {
            if (interests.get(i).equals("sport")){
                fabSports.setTextColor(Color.WHITE);
                fabSports.setBackgroundColor(Color.parseColor("#F44336"));
            }
            if (interests.get(i).equals("auction")){
                fabAuction.setTextColor(Color.WHITE);
                fabAuction.setBackgroundColor(Color.parseColor("#FF9800"));
            }
            if (interests.get(i).equals("music")){
                fabConcerts.setTextColor(Color.WHITE);
                fabConcerts.setBackgroundColor(Color.parseColor("#009688"));
            }
            if (interests.get(i).equals("gala")){
                fabGalas.setTextColor(Color.WHITE);
                fabGalas.setBackgroundColor(Color.parseColor("#00BCD4"));
            }
            if (interests.get(i).equals("raffle")){
                fabRaffle.setTextColor(Color.WHITE);
                fabRaffle.setBackgroundColor(Color.parseColor("#9C27B0"));
            }
            if (interests.get(i).equals("cook")){
                fabExhibits.setTextColor(Color.WHITE);
                fabExhibits.setBackgroundColor(Color.parseColor("#3F51B5"));
            }
            if (interests.get(i).equals("craft")){
                fabCrafts.setTextColor(Color.WHITE);
                fabCrafts.setBackgroundColor(Color.parseColor("#E91E63"));
            }
            if (interests.get(i).equals("thon")){
                fabAthons.setTextColor(Color.WHITE);
                fabAthons.setBackgroundColor(Color.parseColor("#39b894"));
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
        tvValueDistance.setText(String.valueOf(100));
        tvValuePrice.setText(String.valueOf(100));
        sbPrice = view.findViewById(R.id.sbPrice);
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
                    if (interests.get(i).equals("sport")){
                        inside=true;
                        interests.remove(i);
                        fabSports.setTextColor(Color.BLACK);
                        fabSports.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add("sport");
                    fabSports.setTextColor(Color.WHITE);
                    fabSports.setBackgroundColor(Color.parseColor("#F44336"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabConcerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                        if (interests.get(i).equals("music")){
                            inside=true;
                            interests.remove(i);
                            fabConcerts.setTextColor(Color.BLACK);
                            fabConcerts.setBackgroundColor(Color.WHITE);
                            break;
                        }
                }
                if (!inside) {
                    interests.add("music");
                    fabConcerts.setTextColor(Color.WHITE);
                    fabConcerts.setBackgroundColor(Color.parseColor("#009688"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals("auction")){
                        inside=true;
                        interests.remove(i);
                        fabAuction.setTextColor(Color.BLACK);
                        fabAuction.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add("auction");
                    fabAuction.setTextColor(Color.WHITE);
                    fabAuction.setBackgroundColor(Color.parseColor("#FF9800"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabRaffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals("raffle")){
                        inside=true;
                        interests.remove(i);
                        fabRaffle.setTextColor(Color.BLACK);
                        fabRaffle.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add("raffle");
                    fabRaffle.setTextColor(Color.WHITE);
                    fabRaffle.setBackgroundColor(Color.parseColor("#9C27B0"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabExhibits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals("cook")){
                        inside=true;
                        interests.remove(i);
                        fabExhibits.setTextColor(Color.BLACK);
                        fabExhibits.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add("cook");
                    fabExhibits.setTextColor(Color.WHITE);
                    fabExhibits.setBackgroundColor(Color.parseColor("#3F51B5"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabGalas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals("gala")){
                        inside=true;
                        interests.remove(i);
                        fabGalas.setTextColor(Color.BLACK);
                        fabGalas.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add("gala");
                    fabGalas.setTextColor(Color.WHITE);
                    fabGalas.setBackgroundColor(Color.parseColor("#00BCD4"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabCrafts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals("craft")){
                        inside=true;
                        interests.remove(i);
                        fabCrafts.setTextColor(Color.BLACK);
                        fabCrafts.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add("craft");
                    fabCrafts.setTextColor(Color.WHITE);
                    fabCrafts.setBackgroundColor(Color.parseColor("#E91E63"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabAthons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.size(); i++) {
                    if (interests.get(i).equals("thon")){
                        inside=true;
                        interests.remove(i);
                        fabAthons.setTextColor(Color.BLACK);
                        fabAthons.setBackgroundColor(Color.WHITE);
                        break;
                    }
                }
                if (!inside) {
                    interests.add("thon");
                    fabAthons.setTextColor(Color.WHITE);
                    fabAthons.setBackgroundColor(Color.parseColor("#39b894"));
                }
                Log.i(TAG,interests.toString());
            }
        });
    }
}