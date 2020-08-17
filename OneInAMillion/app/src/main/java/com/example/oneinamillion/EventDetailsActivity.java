package com.example.oneinamillion;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.Fragments.DetailsFragment;
import com.example.oneinamillion.Fragments.UserPostFragment;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.EventForSaving;
import com.example.oneinamillion.Models.Post;
import com.example.oneinamillion.adapters.CustomSlideshowAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

public class EventDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    ImageView ivEventImage;
    TextView tvEventName;
    TextView tvLocation;
    TextView tvDescription;
    TextView tvDateTime;
    MaterialButton btnRSVP;
    ImageView ivNavigation;
    ProgressBar progressBar;
    CustomSlideshowAdapter slideshowAdapter;
    ViewPager pager;
    Event event;
    EventForSaving savedEvent;
    Boolean amIattending = false;
    String address;
    MaterialButton extFabDetails;
    MaterialButton extFabPosts;
    int count = 0;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    public static final String TAG = "EventDetails";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        initializeViews();
        if (isConnected()) {
            address = getIntent().getStringExtra("address");
            event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
            showDetailsFragment();
            setClickListeners();
            setInformationViews();
        }
        else {
            savedEvent = Parcels.unwrap(getIntent().getParcelableExtra(EventForSaving.class.getSimpleName()));
            btnRSVP.setText(R.string.attending);
            progressBar.setVisibility(View.INVISIBLE);
            btnRSVP.setIcon(getResources().getDrawable(R.drawable.checkmark));
            tvLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q="+savedEvent.getEventLatitude()
                            +","+savedEvent.getEventLongitude()+"("+savedEvent.getEventName()+")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            });
            ivNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+savedEvent.getEventLatitude()
                            +","+savedEvent.getEventLongitude());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            });
            tvEventName.setText(savedEvent.getEventName());
            tvDateTime.setText(getString(R.string.event_setting,savedEvent.getEventDate(),savedEvent.getEventTime()));
            tvLocation.setText(savedEvent.getEventAddress());
            tvDescription.setText(savedEvent.getEventDescription());
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void showDetailsFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("event",Parcels.wrap(event));
        Fragment fragment = new DetailsFragment();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
    }

    private void setInformationViews() {
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
        if (amIattending) {
            btnRSVP.setText(R.string.attending);
            btnRSVP.setIcon(getResources().getDrawable(R.drawable.checkmark));
        }
        else {
            btnRSVP.setText(R.string.attend);
            btnRSVP.setIcon(getResources().getDrawable(R.drawable.icons_plus));
        }
        if (event.getImage() != null){
            count++;
        }
        if (event.getImage2() != null){
            count++;
        }
        if (event.getImage3() != null){
            count++;
        }
        final String images[] = new String[count];
        if (event.getImage() != null){
            images[0]=event.getImage().getUrl();
            Log.i(TAG,images[0]);
        }
        if (event.getImage2() != null){
            images[1]=event.getImage2().getUrl();
            Log.i(TAG,images[1]);
        }
        if (event.getImage3() != null){
            images[2]=event.getImage3().getUrl();
            Log.i(TAG,images[2]);
        }
        slideshowAdapter = new CustomSlideshowAdapter(EventDetailsActivity.this, images);
        pager.setAdapter(slideshowAdapter);
        progressBar.setMax(count);
        if (count==1){
            progressBar.setVisibility(View.GONE);
        }
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                progressBar.setProgress(position+1);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setCurrentItem(0,true);
        pager.getCurrentItem();
        tvEventName.setText(event.getEventName());
        tvDateTime.setText(getString(R.string.event_setting,event.getDate(),event.getTime()));
        tvLocation.setText(address);
        tvDescription.setText(event.getDescription());
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapPreview);
        mapFragment.getMapAsync(this);
    }

    private void setClickListeners() {
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
        btnRSVP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amIattending) {
                    removeRSVPforEvent();
                }
                else {
                    RSVPforEvent();
                }
            }
        });
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Uri gmmIntentUri = Uri.parse("geo:0,0?q="+event.getLocation().getLatitude()
                        +","+event.getLocation().getLongitude()+"("+event.getEventName()+")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+event.getLocation().getLatitude()
                      +","+event.getLocation().getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }

    private void initializeViews() {
        extFabDetails = findViewById(R.id.extFabDetails);
        extFabPosts = findViewById(R.id.extFabPosts);
        ivEventImage = findViewById(R.id.ivEventImage);
        tvEventName = findViewById(R.id.tvEventName);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvEventDescription);
        tvDateTime = findViewById(R.id.tvDateTime);
        btnRSVP = findViewById(R.id.btnRSVP);
        ivNavigation = findViewById(R.id.ivNavigation);
        pager = findViewById(R.id.vpSlideshow);
        progressBar = findViewById(R.id.progressBar);
    }

    private void RSVPforEvent() {
        JSONArray attendees = event.getAttendees();
        attendees.put(ParseUser.getCurrentUser().getObjectId());
        event.setAttendees(attendees);
        event.saveInBackground();
        btnRSVP.setText(R.string.attending);
        btnRSVP.setIcon(getResources().getDrawable(R.drawable.checkmark));
        amIattending = true;
    }

    private void removeRSVPforEvent() {
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
        btnRSVP.setText(R.string.attend);
        btnRSVP.setIcon(getResources().getDrawable(R.drawable.icons_plus));
        amIattending = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng eventLocation = new LatLng(event.getLocation()
                .getLatitude(),event.getLocation().getLongitude());
        googleMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(eventLocation, 15));
        googleMap.addMarker(new MarkerOptions().position(eventLocation).title(event.getEventName()));
    }
}