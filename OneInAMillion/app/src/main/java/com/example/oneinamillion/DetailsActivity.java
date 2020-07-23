package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.Models.Event;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {
    ImageView ivEventImage;
    TextView tvEventName;
    TextView tvLocation;
    TextView tvDescription;
    TextView tvDateTime;
    MaterialButton btnRSVP;
    Event event;
    Boolean amIattending = false;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        address = getIntent().getStringExtra("address");
        ivEventImage = findViewById(R.id.ivEventImage);
        tvEventName = findViewById(R.id.tvEventName);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvEventDescription);
        tvDateTime = findViewById(R.id.tvDateTime);
        JSONArray attendees = event.getAttendees();
        for(int i = 0; i < attendees.length(); i++){
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
                    for(int i = 0; i<attendees.length();i++ ){
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
                    amIattending=false;
                }
                else {
                    JSONArray attendees = event.getAttendees();
                    attendees.put(ParseUser.getCurrentUser().getObjectId());
                    event.setAttendees(attendees);
                    event.saveInBackground();
                    btnRSVP.setText("Attending");
                    btnRSVP.setIcon(getResources().getDrawable(R.drawable.checkmark));
                    amIattending=true;
                }
            }
        });
        Glide.with(DetailsActivity.this).load(event.getImage().getUrl()).into(ivEventImage);
        tvEventName.setText(event.getEventName());
        tvDateTime.setText(event.getDate()+" at "+event.getTime());
        tvLocation.setText(address);
        tvDescription.setText(event.getDescription());
    }
}