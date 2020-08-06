package com.example.oneinamillion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerFragmentListener, TimePickerFragment.TimePickerFragmentListener {
    EditText etEventName;
    EditText etEventDescription;
    EditText etPrice;
    EditText etTicketLink;
    TextInputLayout tfEventName;
    TextInputLayout tfEventDescription;
    TextInputLayout tfPrice;
    TextInputLayout tfTicketLink;
    Button btnPost;
    Button btnUploadImage;
    ImageView ivUploadedImage;
    ImageView ivUploadedImage2;
    ImageView ivUploadedImage3;
    TextView tvAddMore;
    TextView tvAddMore2;
    ParseFile file;
    ParseFile file2;
    ParseFile file3;
    Button btnPickATime;
    Button btnPickADate;
    TextView tvDate;
    TextView tvTime;
    Spinner spTicketsAvailable;
    int counter = 0;
    ExtendedFloatingActionButton fabSports;
    ExtendedFloatingActionButton fabConcerts;
    ExtendedFloatingActionButton fabAuction;
    ExtendedFloatingActionButton fabRaffle;
    ExtendedFloatingActionButton fabCooking;
    ExtendedFloatingActionButton fabGalas;
    ExtendedFloatingActionButton fabCrafts;
    ExtendedFloatingActionButton fabAthons;
    Boolean ticketsforsale = false;
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
        initializeViews();
        setClickListeners();
        setUpLocationAutoComplete();
    }

    private void setUpLocationAutoComplete() {
        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        placesClient = Places.createClient(this);
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
                Log.i(TAG,status.getStatusMessage());
            }
        });
    }

    private void initializeViews() {
        ivUploadedImage2 = findViewById(R.id.ivUploadedImage2);
        ivUploadedImage3 = findViewById(R.id.ivUploadedImage3);
        tvAddMore = findViewById(R.id.tvAddmore);
        tvAddMore2 = findViewById(R.id.tvAddmore2);
        etEventName = findViewById(R.id.etEventName);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tfEventName = findViewById(R.id.tfEventName);
        tfEventDescription = findViewById(R.id.tfEventDescription);
        tfPrice = findViewById(R.id.tfPrice);
        tfTicketLink = findViewById(R.id.tfTicketLink);
        etEventDescription = findViewById(R.id.etEventDescription);
        etPrice = findViewById(R.id.etPrice);
        ivUploadedImage = findViewById(R.id.ivUploadedImage);
        btnPickADate = findViewById(R.id.btnPickADate);
        fabSports = findViewById(R.id.extFabSports);
        fabConcerts = findViewById(R.id.extFabConcerts);
        fabAuction = findViewById(R.id.extFabAuction);
        fabRaffle = findViewById(R.id.extFabRaffles);
        fabCooking = findViewById(R.id.extFabCooking);
        fabGalas = findViewById(R.id.extFabGalas);
        fabCrafts = findViewById(R.id.extFabCrafts);
        fabAthons = findViewById(R.id.extFabAthons);
        btnPickATime = findViewById(R.id.btnPickATime);
        btnPost = findViewById(R.id.btnPost);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        spTicketsAvailable = findViewById(R.id.spTickets);
        etTicketLink = findViewById(R.id.etTicketLink);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tickets_available, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTicketsAvailable.setAdapter(adapter);
        spTicketsAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = String.valueOf(adapterView.getItemAtPosition(i));
                Log.i(TAG,item);
                if (item.equals("Yes")){
                    tfPrice.setVisibility(View.VISIBLE);
                    tfTicketLink.setVisibility(View.VISIBLE);
                    ticketsforsale = true;
                }
                else{
                    tfPrice.setVisibility(View.GONE);
                    tfTicketLink.setVisibility(View.GONE);
                    ticketsforsale = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setClickListeners() {
        btnPickADate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment = DatePickerFragment.newInstance(AddEventActivity.this);
                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        btnPickATime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment fragment = TimePickerFragment.newInstance(AddEventActivity.this);
                fragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
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
                onPickPhoto(view,PICK_PHOTO_CODE);
            }
        });
        tvAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view,PICK_PHOTO_CODE);
            }
        });
        tvAddMore2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view,PICK_PHOTO_CODE);
            }
        });
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
    }

    public void onPickPhoto(View view, int code) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, code);
        }
    }

    private void saveEvent() {
        Log.i(TAG, "Saving File");
        if (etEventName.getText().toString().matches("")){
            tfEventName.setErrorEnabled(true);
            tfEventName.setError("Enter an Event Name");
        }
        if (etEventDescription.getText().toString().matches("")){
            tfEventDescription.setErrorEnabled(true);
            tfEventDescription.setError("Enter an Event Description");
        }
        if (etPrice.getText().toString().matches("")){
            tfPrice.setErrorEnabled(true);
            tfPrice.setError("Enter a ticket price");
        }
        if (etTicketLink.getText().toString().matches("")){

        }
        if (tvDate.getText().toString().matches("")){

        }
        if (tvTime.getText().toString().matches("")){

        }
        if (latitude==0){

        }
        else {
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "help", e);
                    } else {
                        Log.i(TAG, "Saving");
                        Event event = new Event();
                        event.setEventTags(interests);
                        event.setEventName(etEventName.getText().toString());
                        event.setDescription(etEventDescription.getText().toString());
                        event.setDate(tvDate.getText().toString());
                        event.setTime(tvTime.getText().toString());
                        event.setLocation(new ParseGeoPoint(latitude, longitude));
                        event.setOrganizer(ParseUser.getCurrentUser());
                        event.setImage(file);
                        if (file2!=null) {
                            event.setImage2(file2);
                        }
                        if (file3!=null) {
                            event.setImage3(file3);
                        }
                        if (ticketsforsale) {
                            event.setPrice(Double.parseDouble(etPrice.getText().toString()));
                            event.setTicketLink(etTicketLink.getText().toString());
                        } else {
                            event.setPrice(0);
                        }
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            ClipData mClipData = data.getClipData();
            for (int i = 0; i < mClipData.getItemCount(); i++) {
                if (i <= 2) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    Bitmap bitmap = loadFromUri(uri);
                    switch (counter) {
                        case 0:
                            ivUploadedImage.setImageBitmap(bitmap);
                            tvAddMore.setVisibility(View.VISIBLE);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] image = stream.toByteArray();
                            file = new ParseFile("picture.png", image);
                            break;
                        case 1:
                            ivUploadedImage2.setImageBitmap(bitmap);
                            tvAddMore.setVisibility(View.GONE);
                            tvAddMore2.setVisibility(View.VISIBLE);
                            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream1);
                            byte[] image1 = stream1.toByteArray();
                            file2 = new ParseFile("picture.png", image1);
                            break;
                        case 2:
                            tvAddMore2.setVisibility(View.GONE);
                            ivUploadedImage3.setVisibility(View.VISIBLE);
                            ivUploadedImage3.setImageBitmap(bitmap);
                            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream2);
                            byte[] image2 = stream2.toByteArray();
                            file3 = new ParseFile("picture.png", image2);
                            break;
                    }
                    counter++;
                }
            }
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
        tvDate.setVisibility(View.VISIBLE);
        tvDate.setText(date);
    }

    @Override
    public void TimeSet(String time) {
        tvTime.setVisibility(View.VISIBLE);
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