package com.example.oneinamillion;

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
import com.google.android.gms.maps.GoogleMap;
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

public class AddEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerFragmentListener, TimePickerFragment.TimePickerFragmentListener {
    EditText etEventName;
    EditText etEventDescription;
    Button btnPickAPlace;
    Button btnPost;
    Button btnUploadImage;
    ImageView ivUploadedImage;
    ParseFile file;
    TextView tvLocation;
    Button btnPickATime;
    Button btnPickADate;
    TextView tvDate;
    TextView tvTime;
    ExtendedFloatingActionButton fabSports;
    ExtendedFloatingActionButton fabConcerts;
    ExtendedFloatingActionButton fabAuction;
    ExtendedFloatingActionButton fabRaffle;
    ExtendedFloatingActionButton fabExhibits;
    ExtendedFloatingActionButton fabGalas;
    ExtendedFloatingActionButton fabCrafts;
    ExtendedFloatingActionButton fabAthons;
    JSONArray interests = new JSONArray();
    double longitude = 0;
    double latitude = 0;
    public static final String TAG = "AddEvent";
    public final static int PICK_PHOTO_CODE = 1046;
    public final static int PICK_LOCATION_CODE = 20020;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        etEventName = findViewById(R.id.etEventName);
        tvLocation = findViewById(R.id.tvLocation);
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
        btnPickAPlace = findViewById(R.id.btnPickAPlace);
        btnPickAPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEventActivity.this, PickALocationActivity.class);
                startActivityForResult(intent, PICK_LOCATION_CODE);
            }
        });
        btnPost = findViewById(R.id.btnPost);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,etEventName.getText().toString());
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
        fabExhibits = findViewById(R.id.extFabArtExhibits);
        fabGalas = findViewById(R.id.extFabGalas);
        fabCrafts = findViewById(R.id.extFabCrafts);
        fabAthons = findViewById(R.id.extFabAthons);
        fabSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.length(); i++) {
                    try {
                        if (interests.get(i).equals("sport")){
                            inside=true;
                            interests.remove(i);
                            fabSports.setTextColor(Color.BLACK);
                            fabSports.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put("sport");
                    fabSports.setTextColor(Color.WHITE);
                    fabSports.setBackgroundColor(Color.parseColor("#F44336"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabConcerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.length(); i++) {
                    try {
                        if (interests.get(i).equals("music")){
                            inside=true;
                            interests.remove(i);
                            fabConcerts.setTextColor(Color.BLACK);
                            fabConcerts.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put("music");
                    fabConcerts.setTextColor(Color.WHITE);
                    fabConcerts.setBackgroundColor(Color.parseColor("#009688"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.length(); i++) {
                    try {
                        if (interests.get(i).equals("auction")){
                            inside=true;
                            interests.remove(i);
                            fabAuction.setTextColor(Color.BLACK);
                            fabAuction.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put("auction");
                    fabAuction.setTextColor(Color.WHITE);
                    fabAuction.setBackgroundColor(Color.parseColor("#FF9800"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabRaffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.length(); i++) {
                    try {
                        if (interests.get(i).equals("raffle")){
                            inside=true;
                            interests.remove(i);
                            fabRaffle.setTextColor(Color.BLACK);
                            fabRaffle.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put("raffle");
                    fabRaffle.setTextColor(Color.WHITE);
                    fabRaffle.setBackgroundColor(Color.parseColor("#9C27B0"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabExhibits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.length(); i++) {
                    try {
                        if (interests.get(i).equals("cook")){
                            inside=true;
                            interests.remove(i);
                            fabExhibits.setTextColor(Color.BLACK);
                            fabExhibits.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put("cook");
                    fabExhibits.setTextColor(Color.WHITE);
                    fabExhibits.setBackgroundColor(Color.parseColor("#3F51B5"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabGalas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.length(); i++) {
                    try {
                        if (interests.get(i).equals("gala")){
                            inside=true;
                            interests.remove(i);
                            fabGalas.setTextColor(Color.BLACK);
                            fabGalas.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put("gala");
                    fabGalas.setTextColor(Color.WHITE);
                    fabGalas.setBackgroundColor(Color.parseColor("#00BCD4"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabCrafts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.length(); i++) {
                    try {
                        if (interests.get(i).equals("craft")){
                            inside=true;
                            interests.remove(i);
                            fabCrafts.setTextColor(Color.BLACK);
                            fabCrafts.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put("craft");
                    fabCrafts.setTextColor(Color.WHITE);
                    fabCrafts.setBackgroundColor(Color.parseColor("#E91E63"));
                }
                Log.i(TAG,interests.toString());
            }
        });
        fabAthons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean inside=false;
                for (int i = 0; i <interests.length(); i++) {
                    try {
                        if (interests.get(i).equals("thon")){
                            inside=true;
                            interests.remove(i);
                            fabAthons.setTextColor(Color.BLACK);
                            fabAthons.setBackgroundColor(Color.WHITE);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!inside) {
                    interests.put("thon");
                    fabAthons.setTextColor(Color.WHITE);
                    fabAthons.setBackgroundColor(Color.parseColor("#39b894"));
                }
                Log.i(TAG,interests.toString());
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
                if (e!=null) {
                    Log.e(TAG,"help",e);
                }
                else {
                    Event event = new Event();
                    event.setEventTags(interests);
                    event.setEventName(etEventName.getText().toString());
                    event.setDescription(etEventDescription.getText().toString());
                    event.setDate(tvDate.getText().toString());
                    event.setTime(tvTime.getText().toString());
                    event.setLocation(new ParseGeoPoint(latitude,longitude));
                    event.setOrganizer(ParseUser.getCurrentUser());
                    event.setImage(file);
                    event.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e!=null) {
                                Log.e(TAG,"Error while saving",e);
                                Toast.makeText(AddEventActivity.this, "Error while saving",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Log.i(TAG,"Save successful");
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
        if (requestCode == PICK_LOCATION_CODE && resultCode == Activity.RESULT_OK) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            btnPickAPlace.setText("Repick");
            tvLocation.setVisibility(View.VISIBLE);
            String name = data.getStringExtra("name");
            tvLocation.setText(name);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
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
}