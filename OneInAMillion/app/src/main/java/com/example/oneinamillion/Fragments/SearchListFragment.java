package com.example.oneinamillion.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.SearchResultAdapter;

import java.util.List;

public class SearchListFragment extends Fragment {
    List<Event> events;
    public static final String TAG = "SearchListFragment";
    RecyclerView rvSearchResults;
    SearchResultAdapter searchResultAdapter;

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
        searchResultAdapter = new SearchResultAdapter(events,getContext());
        rvSearchResults.setAdapter(searchResultAdapter);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}