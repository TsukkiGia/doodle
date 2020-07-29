package com.example.oneinamillion;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.Fragments.DetailsFragment;
import com.example.oneinamillion.Fragments.UserPostFragment;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.Post;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

public class EventDetailsActivity extends AppCompatActivity {
    ImageView ivEventImage;
    TextView tvEventName;
    TextView tvLocation;
    TextView tvDescription;
    TextView tvDateTime;
    MaterialButton btnRSVP;
    Event event;
    Boolean amIattending = false;
    String address;
    ExtendedFloatingActionButton extFabDetails;
    ExtendedFloatingActionButton extFabPosts;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    public static final String TAG = "EventDetails";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getStringExtra("activity").equals("SearchFragment")) {
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        setContentView(R.layout.activity_event_details);
        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        address = getIntent().getStringExtra("address");
        extFabDetails = findViewById(R.id.extFabDetails);
        extFabPosts = findViewById(R.id.extFabPosts);
        Bundle bundle = new Bundle();
        bundle.putParcelable("event",Parcels.wrap(event));
        Fragment fragment = new DetailsFragment();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
        extFabDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("event",Parcels.wrap(event));
                Fragment fragment = new DetailsFragment();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
                extFabDetails.setBackgroundColor(getColor(R.color.colorAccent));
                extFabDetails.setTextColor(Color.WHITE);
                extFabPosts.setBackgroundColor(Color.WHITE);
                extFabPosts.setTextColor(Color.BLACK);
            }
        });
        extFabPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("event",Parcels.wrap(event));
                Fragment fragment = new UserPostFragment();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
                extFabPosts.setBackgroundColor(getColor(R.color.colorAccent));
                extFabPosts.setTextColor(Color.WHITE);
                extFabDetails.setBackgroundColor(Color.WHITE);
                extFabDetails.setTextColor(Color.BLACK);
            }
        });
        ivEventImage = findViewById(R.id.ivEventImage);
        tvEventName = findViewById(R.id.tvEventName);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvEventDescription);
        tvDateTime = findViewById(R.id.tvDateTime);
        JSONArray attendees = event.getAttendees();
        for (int i = 0; i < attendees.length(); i++){
            try {
                String userID = attendees.getString(i);
                if (userID.equals(ParseUser.getCurrentUser().getObjectId())) {
                    amIattending = true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        btnRSVP = findViewById(R.id.btnRSVP);
        if (amIattending) {
            btnRSVP.setText("Attending");
            btnRSVP.setIcon(getResources().getDrawable(R.drawable.checkmark));
        }
        else {
            btnRSVP.setText("Attend");
            btnRSVP.setIcon(getResources().getDrawable(R.drawable.icons_plus));
        }
        btnRSVP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amIattending) {
                    JSONArray attendees = event.getAttendees();
                    for (int i = 0;  i < attendees.length();i++){
                        try {
                            String userID = attendees.getString(i);
                            if (userID.equals(ParseUser.getCurrentUser().getObjectId())) {
                               attendees.remove(i);
                               break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    event.setAttendees(attendees);
                    event.saveInBackground();
                    btnRSVP.setText("Attend");
                    btnRSVP.setIcon(getResources().getDrawable(R.drawable.icons_plus));
                    amIattending = false;
                }
                else {
                    JSONArray attendees = event.getAttendees();
                    attendees.put(ParseUser.getCurrentUser().getObjectId());
                    event.setAttendees(attendees);
                    event.saveInBackground();
                    btnRSVP.setText("Attending");
                    btnRSVP.setIcon(getResources().getDrawable(R.drawable.checkmark));
                    amIattending = true;
                }
            }
        });
        Glide.with(EventDetailsActivity.this).load(event.getImage().getUrl()).into(ivEventImage);
        tvEventName.setText(event.getEventName());
        tvDateTime.setText(event.getDate()+" at "+event.getTime());
        tvLocation.setText(address);
        tvDescription.setText(event.getDescription());
        getKnownAttendees();
    }

    private void getKnownAttendees() {
        JSONArray attendees = event.getAttendees();
    }
}