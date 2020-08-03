package com.example.oneinamillion.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.EventAdapter;
import com.example.oneinamillion.adapters.SearchResultAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchListFragment extends Fragment {
    List<Event> events;
    public static final String TAG = "SearchListFragment";
    RecyclerView rvSearchResults;
    EventAdapter searchResultAdapter;
    ImageView ivBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        events = getArguments().getParcelableArrayList("result");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        ivBack = view.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("result", (ArrayList<? extends Parcelable>) events);
                Fragment fragment = new SearchFragment();
                fragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.flContainer,fragment).commit();
            }
        });
        searchResultAdapter = new EventAdapter(getContext(),events);
        rvSearchResults.setAdapter(searchResultAdapter);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}