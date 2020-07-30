package com.example.oneinamillion.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.Post;
import com.example.oneinamillion.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
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
    Button btnBuyTickets;
    List<String> friendsAttendingList = new ArrayList<>();
    String friendsAttending="";
    public static final String TAG = "DetailsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        if (event.getTicketLink()==null){
            btnBuyTickets.setVisibility(View.GONE);
        }
        btnBuyTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event.getTicketLink() != null) {
                    String url = "https://"+event.getTicketLink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });
        setInformationTextViews();
        getFriendsAttending();
    }

    private void initializeViews(View view) {
        tvOrganizer = view.findViewById(R.id.tvOrganizer);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvFriendsAttending = view.findViewById(R.id.tvFriendsAttending);
        btnBuyTickets = view.findViewById(R.id.btnBuyTicket);
    }

    private void setInformationTextViews() {
        organizerName = event.getOrganizer().getString("FirstName")+" "+
                event.getOrganizer().getString("LastName");
        price = String.valueOf(event.getPrice());
        distance = String.valueOf(Math.round(event.distance));
        //Checker if the distance value is 0
        tvOrganizer.setText(getContext().getString(R.string.event_organizer,organizerName));
        tvPrice.setText(getContext().getString(R.string.price,price));
        tvDistance.setText(getContext().getString(R.string.distance_from_user,distance));
    }

    private void getFriendsAttending() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                String attendees = event.getAttendees().toString();
                JSONArray friends = ParseUser.getCurrentUser().getJSONArray("Friends");
                for (int i = 0; i < friends.length(); i++){
                    String friend = null;
                    try {
                        friend = friends.getString(i);
                        if (attendees.contains(friend)){
                            for (ParseUser user : users){
                                if (user.getObjectId().equals(friend)){
                                    friendsAttendingList.add(user.getString("FirstName"));
                                    break;
                                }
                            }
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                if (friendsAttendingList.isEmpty()){
                    tvFriendsAttending.setText(R.string.no_friends);
                }
                if (friendsAttendingList.size()==1){
                    String friendname = friendsAttendingList.get(0);
                    tvFriendsAttending.setText(getContext().getString(R.string.one_friend,friendname));
                }
                if (friendsAttendingList.size()>1){
                    for (String firstname : friendsAttendingList){
                        friendsAttending+=firstname+", ";
                    }
                    Log.i(TAG,friendsAttendingList.toString());
                    tvFriendsAttending.setText(friendsAttending+" are attending");
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = Parcels.unwrap(getArguments().getParcelable("event"));
    }
}