package com.example.oneinamillion.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;

import com.example.oneinamillion.EventMapActivity;
import com.example.oneinamillion.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarFragment extends Fragment {
    CalendarView calendarView;
    public static final String TAG = "CalendarFragment";
    ImageView ivList;
    ImageView ivMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarView = view.findViewById(R.id.calendarView);
        long currentDateTime = System.currentTimeMillis();
        Date currentDate = new Date(currentDateTime);
        String formattedDate = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                .format(currentDate);
        Bundle dates = new Bundle();
        dates.putString("date", formattedDate);
        Fragment fragment = new CalendarEventFragment();
        fragment.setArguments(dates);
        ivList = view.findViewById(R.id.ivList);
        ivMap = view.findViewById(R.id.ivMap);
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), EventMapActivity.class);
                startActivity(i);
            }
        });
        ivList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, new EventsFragment()).commit();
            }
        });
        getChildFragmentManager().beginTransaction().replace(R.id.flEvents, fragment).commit();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                if (day < 10 && month < 9) {
                    String date = "0" + String.valueOf(month + 1) + "/0" + String.valueOf(day) + "/" + String.valueOf(year);
                    Bundle dates = new Bundle();
                    dates.putString("date", date);
                    Fragment fragment = new CalendarEventFragment();
                    fragment.setArguments(dates);
                    getChildFragmentManager().beginTransaction().replace(R.id.flEvents, fragment).commit();
                } else if (day < 10 && month >= 9) {
                    Log.i(TAG, String.valueOf(month));
                    String date = String.valueOf(month + 1) + "/0" + String.valueOf(day) + "/" + String.valueOf(year);
                    Bundle dates = new Bundle();
                    dates.putString("date", date);
                    Fragment fragment = new CalendarEventFragment();
                    fragment.setArguments(dates);
                    getChildFragmentManager().beginTransaction().replace(R.id.flEvents, fragment).commit();
                } else if (day >= 10 && month < 9) {
                    String date = "0" + String.valueOf(month + 1) + "/" + String.valueOf(day) + "/" + String.valueOf(year);
                    Bundle dates = new Bundle();
                    dates.putString("date", date);
                    Fragment fragment = new CalendarEventFragment();
                    fragment.setArguments(dates);
                    getChildFragmentManager().beginTransaction().replace(R.id.flEvents, fragment).commit();
                } else {
                    String date = String.valueOf(month + 1) + "/" + String.valueOf(day) + "/" + String.valueOf(year);
                    Bundle dates = new Bundle();
                    dates.putString("date", date + 4);
                    Fragment fragment = new CalendarEventFragment();
                    fragment.setArguments(dates);
                    getChildFragmentManager().beginTransaction().replace(R.id.flEvents, fragment).commit();
                }
            }
        });
    }
}