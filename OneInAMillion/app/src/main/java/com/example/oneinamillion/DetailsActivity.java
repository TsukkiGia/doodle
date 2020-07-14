package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.Models.Event;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {
    ImageView ivEventImage;
    TextView tvEventName;
    TextView tvLocation;
    TextView tvDescription;
    TextView tvDateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Event event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        ivEventImage = findViewById(R.id.ivEventImage);
        tvEventName = findViewById(R.id.tvEventName);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvEventDescription);
        tvDateTime = findViewById(R.id.tvDateTime);
        Glide.with(DetailsActivity.this).load(event.getImage().getUrl()).into(ivEventImage);
        tvEventName.setText(event.getEventName());
        tvDateTime.setText(event.getDate()+" at "+event.getTime());
        tvLocation.setText("Boston Museum");
        tvDescription.setText(event.getDescription());
    }
}