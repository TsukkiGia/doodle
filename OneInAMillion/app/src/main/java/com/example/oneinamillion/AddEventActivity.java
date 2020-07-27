package com.example.oneinamillion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oneinamillion.Fragments.DatePickerFragment;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Fragments.TimePickerFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class AddEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerFragmentListener, TimePickerFragment.TimePickerFragmentListener {
    EditText etEventName;
    EditText etEventDescription;
    Button btnPost;
    Button btnUploadImage;
    ImageView ivUploadedImage;
    ParseFile file;
    Button btnPickATime;
    Button btnPickADate;
    TextView tvDate;
    TextView tvTime;
    ExtendedFloatingActionButton fabSports;
    ExtendedFloatingActionButton fabConcerts;
    ExtendedFloatingActionButton fabAuction;
    ExtendedFloatingActionButton fabRaffle;
    ExtendedFloatingActionButton fabCooking;
    ExtendedFloatingActionButton fabGalas;
    ExtendedFloatingActionButton fabCrafts;
    ExtendedFloatingActionButton fabAthons;
    String raffle_tag = "raffle";
    String thon_tag = "thon";
    String sport_tag = "sport";
    String auctions_tag = "auction";
    String cook_tag = "cook";
    String concert_tag = "music";
    String gala_tag = "gala";
    String craft_tag = "craft";
    JSONArray interests = new JSONArray();
    double longitude = 0;
    double latitude = 0;
    public static final String TAG = "AddEvent";
    public final static int PICK_PHOTO_CODE = 1046;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        placesClient = Places.createClient(this);
        etEventName = findViewById(R.id.etEventName);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        etEventDescription = findViewById(R.id.etEventDescription);
        ivUploadedImage = findViewById(R.id.ivUploadedImage);
        btnPickADate = findViewById(R.id.btnPickADate);
        btnPickADate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment = DatePickerFragment.newInstance(AddEventActivity.this);
                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        btnPickATime = findViewById(R.id.btnPickATime);
        btnPickATime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment fragment = TimePickerFragment.newInstance(AddEventActivity.this);
                fragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        btnPost = findViewById(R.id.btnPost);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, etEventName.getText().toString());
                saveEvent();
            }
        });
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });
        fabSports = findViewById(R.id.extFabSports);
        fabConcerts = findViewById(R.id.extFabConcerts);
        fabAuction = findViewById(R.id.extFabAuction);
        fabRaffle = findViewById(R.id.extFabRaffles);
        fabCooking = findViewById(R.id.extFabCooking);
        fabGalas = findViewById(R.id.extFabGalas);
        fabCrafts = findViewById(R.id.extFabCrafts);
        fabAthons = findViewById(R.id.extFabAthons);
        fabSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabSports,sport_tag,R.color.colorSportButton);
            }
        });
        fabConcerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabConcerts,concert_tag,R.color.colorMusicButton);
            }
        });
        fabAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabAuction,auctions_tag,R.color.colorAuctionsButton);
            }
        });
        fabRaffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabRaffle,raffle_tag,R.color.colorRaffleButton);
            }
        });
        fabCooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabCooking,cook_tag,R.color.colorCookButton);
            }
        });
        fabGalas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabGalas,gala_tag,R.color.colorGalaButton);
            }
        });
        fabCrafts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabCrafts,craft_tag,R.color.colorCraftButton);
            }
        });
        fabAthons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabclicked(fabAthons,thon_tag,R.color.colorThonButton);
            }
        });
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setHint("Pick a location");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
    }

    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    private void saveEvent() {
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "help", e);
                } else {
                    Event event = new Event();
                    event.setEventTags(interests);
                    event.setEventName(etEventName.getText().toString());
                    event.setDescription(etEventDescription.getText().toString());
                    event.setDate(tvDate.getText().toString());
                    event.setTime(tvTime.getText().toString());
                    event.setLocation(new ParseGeoPoint(latitude, longitude));
                    event.setOrganizer(ParseUser.getCurrentUser());
                    event.setImage(file);
                    event.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error while saving", e);
                                Toast.makeText(AddEventActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Log.i(TAG, "Save successful");
                            finish();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            // Load the selected image into a preview
            ivUploadedImage.setImageBitmap(selectedImage);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            // Create the ParseFile
            file = new ParseFile("picture.png", image);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void DateSet(String date) {
        tvDate.setText(date);
    }

    @Override
    public void TimeSet(String time) {
        tvTime.setText(time);
    }

    private void fabclicked(ExtendedFloatingActionButton button, String tag, int color) {
        Boolean inside = false;
        for (int i = 0; i < interests.length(); i++) {
            try {
                if (interests.get(i).equals(tag)) {
                    inside = true;
                    interests.remove(i);
                    button.setTextColor(Color.BLACK);
                    button.setBackgroundColor(Color.WHITE);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!inside) {
            interests.put(tag);
            button.setTextColor(Color.WHITE);
            button.setBackgroundColor(getColor(color));
        }
        Log.i(TAG, interests.toString());
    }
}