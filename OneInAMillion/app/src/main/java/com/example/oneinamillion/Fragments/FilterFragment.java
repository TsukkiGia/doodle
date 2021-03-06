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
import android.widget.CheckBox;
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
    ExtendedFloatingActionButton fabCooking;
    ExtendedFloatingActionButton fabGalas;
    ExtendedFloatingActionButton fabCrafts;
    ExtendedFloatingActionButton fabAthons;
    TextView tvValuePrice;
    final String raffle_tag = "raffle";
    final String thon_tag= "thon";
    final String sport_tag = "sport";
    final String auctions_tag = "auction";
    final String cook_tag = "cook";
    final String concert_tag = "music";
    final String gala_tag = "gala";
    final String craft_tag = "craft";
    double max_distance = 1000;
    double max_price = 500;
    TextView tvValueDistance;
    TextView tvClearFilters;
    SeekBar sbDistance;
    SeekBar sbPrice;
    String sort_metric;
    Boolean isChecked=false;
    CheckBox cbFriendsAttending;
    Boolean filterdistance = false;
    Boolean filterprice = false;
    List<String> interests = new ArrayList<>();
    public static final String TAG = "FilterFragment";

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            Log.i(TAG, getArguments().toString());
            sort_metric = getArguments().getString("sort_metric");
            Log.i(TAG,sort_metric);
            max_distance = Double.valueOf(getArguments().getString("max_distance"));
            max_price = Double.valueOf(getArguments().getString("max_price"));
            interests = getArguments().getStringArrayList("tags");
            isChecked = getArguments().getBoolean("friends");
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
        initializeViews(view);
        setActiveTags();
        setClickListeners();
        setUpFilterMetrics();
    }

    private void setUpFilterMetrics() {
        tvClearFilters.setVisibility(View.INVISIBLE);
        if (max_distance != 1000){
            filterdistance = true;
        }
        if (max_price != 500){
            filterprice = true;
        }
        cbFriendsAttending.setChecked(isChecked);
        tvValueDistance.setText(String.valueOf((int)max_distance)+"km");
        tvValuePrice.setText("$"+String.valueOf((int)max_price));
        sbDistance.setProgress((int) max_distance);
        sbPrice.setProgress((int) max_price);
        sbPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvValuePrice.setText("$"+String.valueOf(i));
                max_price = i;
                filterprice = true;
                tvClearFilters.setVisibility(View.VISIBLE);
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
                tvValueDistance.setText(String.valueOf(i)+"km");
                max_distance = i;
                filterdistance = true;
                tvClearFilters.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setClickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("sort_metric", sort_metric);
                bundle.putString("max_distance", String.valueOf(max_distance));
                bundle.putString("max_price", String.valueOf(max_price));
                bundle.putBoolean("friends",cbFriendsAttending.isChecked());
                bundle.putBoolean("filterprice",filterprice);
                bundle.putBoolean("filterdistance",filterdistance);
                bundle.putStringArrayList("tags", (ArrayList<String>) interests);
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(bundle);
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, homeFragment).commit();
            }
        });
        fabSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabSports,sport_tag,R.color.colorThonButton);
            }
        });
        fabConcerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabConcerts,concert_tag,R.color.colorThonButton);
            }
        });
        fabAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabAuction,auctions_tag,R.color.colorThonButton);
            }
        });
        fabRaffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabRaffle,raffle_tag,R.color.colorThonButton);
            }
        });
        fabCooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabCooking,cook_tag,R.color.colorThonButton);
            }
        });
        fabGalas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabGalas,gala_tag,R.color.colorThonButton);
            }
        });
        fabCrafts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabCrafts,craft_tag,R.color.colorThonButton);
            }
        });
        fabAthons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabAthons,thon_tag,R.color.colorThonButton);
            }
        });
        tvClearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                max_distance = 1000;
                max_price = 500;
                sbPrice.setProgress((int)max_price);
                sbDistance.setProgress((int)max_distance);
                tvValueDistance.setText(String.valueOf((int)max_distance)+"km");
                tvValuePrice.setText("$"+String.valueOf((int)max_price));
                cbFriendsAttending.setChecked(false);
                setInactiveTags();
                interests = new ArrayList<>();
                tvClearFilters.setVisibility(View.INVISIBLE);
                Log.i(TAG,interests.toString());
            }
        });
    }

    private void setInactiveTags() {
        Log.i(TAG,interests.toString());
        for (int i = 0; i < interests.size(); i++) {
            String interest = interests.get(i);
            switch (interest) {
                case sport_tag:
                    fabSports.setTextColor(Color.BLACK);
                    fabSports.setBackgroundColor(Color.WHITE);
                    break;
                case auctions_tag:
                    fabAuction.setTextColor(Color.BLACK);
                    fabAuction.setBackgroundColor(Color.WHITE);
                    break;
                case concert_tag:
                    fabConcerts.setTextColor(Color.BLACK);
                    fabConcerts.setBackgroundColor(Color.WHITE);
                    break;
                case gala_tag:
                    fabGalas.setTextColor(Color.BLACK);
                    fabGalas.setBackgroundColor(Color.WHITE);
                    break;
                case raffle_tag:
                    fabRaffle.setTextColor(Color.BLACK);
                    fabRaffle.setBackgroundColor(Color.WHITE);
                    break;
                case cook_tag:
                    fabCooking.setTextColor(Color.BLACK);
                    fabCooking.setBackgroundColor(Color.WHITE);
                    break;
                case craft_tag:
                    fabCrafts.setTextColor(Color.BLACK);
                    fabCrafts.setBackgroundColor(Color.WHITE);
                    break;
                case thon_tag:
                    fabAthons.setTextColor(Color.BLACK);
                    fabAthons.setBackgroundColor(Color.WHITE);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + interests.get(i));
            }
        }
    }

    private void setActiveTags() {
        Log.i(TAG,interests.toString());
        for (int i = 0; i < interests.size(); i++) {
            String interest = interests.get(i);
            switch (interest) {
                case sport_tag:
                    fabSports.setTextColor(Color.WHITE);
                    fabSports.setBackgroundColor(getContext().getColor(R.color.colorSportButton));
                    break;
                case auctions_tag:
                    fabAuction.setTextColor(Color.WHITE);
                    fabAuction.setBackgroundColor(getContext().getColor(R.color.colorAuctionsButton));
                    break;
                case concert_tag:
                    fabConcerts.setTextColor(Color.WHITE);
                    fabConcerts.setBackgroundColor(getContext().getColor(R.color.colorMusicButton));
                    break;
                case gala_tag:
                    fabGalas.setTextColor(Color.WHITE);
                    fabGalas.setBackgroundColor(getContext().getColor(R.color.colorGalaButton));
                    break;
                case raffle_tag:
                    fabRaffle.setTextColor(Color.WHITE);
                    fabRaffle.setBackgroundColor(getContext().getColor(R.color.colorRaffleButton));
                    break;
                case cook_tag:
                    fabCooking.setTextColor(Color.WHITE);
                    fabCooking.setBackgroundColor(getContext().getColor(R.color.colorCookButton));
                    break;
                case craft_tag:
                    fabCrafts.setTextColor(Color.WHITE);
                    fabCrafts.setBackgroundColor(getContext().getColor(R.color.colorCraftButton));
                    break;
                case thon_tag:
                    fabAthons.setTextColor(Color.WHITE);
                    fabAthons.setBackgroundColor(getContext().getColor(R.color.colorThonButton));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + interests.get(i));
            }
        }
    }

    private void initializeViews(View view) {
        cbFriendsAttending = view.findViewById(R.id.cbFriendsAttending);
        fabSports = view.findViewById(R.id.extFabSports);
        fabConcerts = view.findViewById(R.id.extFabConcerts);
        fabAuction = view.findViewById(R.id.extFabAuction);
        fabRaffle = view.findViewById(R.id.extFabRaffles);
        fabCooking = view.findViewById(R.id.extFabCooking);
        fabGalas = view.findViewById(R.id.extFabGalas);
        fabCrafts = view.findViewById(R.id.extFabCrafts);
        fabAthons = view.findViewById(R.id.extFabAthons);
        tvValueDistance = view.findViewById(R.id.tvValueDistance);
        tvValuePrice = view.findViewById(R.id.tvValuePrice);
        sbDistance = view.findViewById(R.id.sbDistance);
        sbPrice = view.findViewById(R.id.sbPrice);
        ivBack = view.findViewById(R.id.ivBack);
        tvClearFilters = view.findViewById(R.id.tvClearFilter);
    }

    private void fabclicked(ExtendedFloatingActionButton button, String tag, int color) {
        Boolean inside = false;
        for (int i = 0; i < interests.size(); i++) {
            if (interests.get(i).equals(tag)){
                inside = true;
                interests.remove(i);
                button.setTextColor(Color.BLACK);
                button.setBackgroundColor(Color.WHITE);
                break;
            }
        }
        if (!inside) {
            interests.add(tag);
            button.setTextColor(Color.WHITE);
            button.setBackgroundColor(getContext().getColor(color));
        }
        Log.i(TAG,interests.toString());
        tvClearFilters.setVisibility(View.VISIBLE);
    }
}