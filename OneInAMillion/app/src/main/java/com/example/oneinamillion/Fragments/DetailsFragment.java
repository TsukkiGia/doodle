package com.example.oneinamillion.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.Post;
import com.example.oneinamillion.R;

import org.parceler.Parcels;

import java.util.List;

public class DetailsFragment extends Fragment {
    String organizerName;
    TextView tvOrganizer;
    TextView tvPrice;
    TextView tvDistance;
    TextView tvFriendsAttending;
    Event event;
    String price;
    String distance;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }
//organizer, price, distance and friends that are attending
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvOrganizer = view.findViewById(R.id.tvOrganizer);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvFriendsAttending = view.findViewById(R.id.tvFriendsAttending);
        organizerName = event.getOrganizer().getString("FirstName")+" "+
                event.getOrganizer().getString("LastName");
        price = String.valueOf(event.getPrice());
        distance = String.valueOf(Math.round(event.distance));
        //Checker if the value is 0
        tvOrganizer.setText("Organized by "+organizerName);
        tvPrice.setText("$"+price);
        tvDistance.setText("This event is "+distance+" kilometers away from you");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = Parcels.unwrap(getArguments().getParcelable("event"));
    }
}