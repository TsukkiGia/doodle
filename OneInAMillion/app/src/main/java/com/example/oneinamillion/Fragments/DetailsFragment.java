package com.example.oneinamillion.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oneinamillion.MainActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.Post;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.HorizontalEventAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    RecyclerView rvSimilarEvents;
    ImageView ivAlarm;
    Boolean alarmon = false;
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
        setInformationTextViews();
        getFriendsAttending();
        queryForSimilarEvents();
    }

    private void queryForSimilarEvents() {
        final List<Event> similarEvents = new ArrayList<>();
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                String tags = event.getEventTag().toString();
                for (Event ev : events){
                    for (int i = 0; i < ev.getEventTag().length(); i++){
                        try {
                            String tag = ev.getEventTag().getString(i);
                            if (tags.contains(tag)){
                                similarEvents.add(ev);
                                break;
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                HorizontalEventAdapter eventAdapter = new HorizontalEventAdapter(getContext(),similarEvents);
                rvSimilarEvents.setAdapter(eventAdapter);
                rvSimilarEvents.setLayoutManager(new LinearLayoutManager(getContext()
                        ,LinearLayoutManager.HORIZONTAL,false));
            }
        });
    }

    private void initializeViews(View view) {
        tvOrganizer = view.findViewById(R.id.tvOrganizer);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvFriendsAttending = view.findViewById(R.id.tvFriendsAttending);
        btnBuyTickets = view.findViewById(R.id.btnBuyTicket);
        rvSimilarEvents = view.findViewById(R.id.rvSimilarEvents);
        ivAlarm = view.findViewById(R.id.ivAlarm);
    }

    private void setInformationTextViews() {
        try {
            organizerName = event.getOrganizer().fetchIfNeeded().getString("FirstName")+" "+
                    event.getOrganizer().fetchIfNeeded().getString("LastName");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (event.getPrice()==0.0){
            tvPrice.setText("Free entry");
        }
        else {
            price = String.valueOf(event.getPrice());
            tvPrice.setText(getContext().getString(R.string.price, price));
        }
        distance = String.valueOf(Math.round(event.distance));
        //Checker if the distance value is 0
        tvOrganizer.setText(getContext().getString(R.string.event_organizer,organizerName));
        tvDistance.setText(getContext().getString(R.string.distance_from_user,distance));
        if (event.getTicketLink()==null){
            btnBuyTickets.setVisibility(View.GONE);
        }
        ivAlarm.setTag(1);
        ivAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!alarmon) {
                    ivAlarm.setImageResource(R.drawable.alarmfilled);
                    ivAlarm.setColorFilter(getResources().getColor(R.color.colorAccent));
                    alarmon = true;
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),0,intent,0);
                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    try {
                        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.ENGLISH)
                                .parse(event.getDate() + " " + event.getTime());
                        long fifteenbefore = date.getTime()-900000;
                        alarmManager.set(AlarmManager.RTC_WAKEUP,fifteenbefore,pendingIntent);

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    ivAlarm.setImageResource(R.drawable.alarmoutline);
                    ivAlarm.setColorFilter(Color.BLACK);
                    alarmon = false;
                }
            }
        });
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