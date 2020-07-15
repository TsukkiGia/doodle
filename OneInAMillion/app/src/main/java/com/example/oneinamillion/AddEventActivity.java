package com.example.oneinamillion;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oneinamillion.Models.Event;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity {
    EditText etEventName;
    EditText etEventDescription;
    Button btnPickAPlace;
    EditText etEventTime;
    EditText etEventDate;
    Button btnPost;
    Button btnUploadImage;
    ImageView ivUploadedImage;
    ParseFile file;
    TextView tvLocation;
    Button btnRepickAPlace;

    public static final String TAG = "AddEvent";
    public final static int PICK_PHOTO_CODE = 1046;
    public final static int PICK_LOCATION_CODE = 20020;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        etEventName = findViewById(R.id.etEventName);
        tvLocation = findViewById(R.id.tvLocation);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventTime = findViewById(R.id.etEventTime);
        etEventDate = findViewById(R.id.etEventDate);
        ivUploadedImage = findViewById(R.id.ivUploadedImage);
        btnPickAPlace = findViewById(R.id.btnPickAPlace);
        btnPickAPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEventActivity.this,MapActivity.class);
                startActivityForResult(intent, PICK_LOCATION_CODE);
            }
        });
        btnRepickAPlace = findViewById(R.id.btnRepickAPlace);
        btnRepickAPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEventActivity.this,MapActivity.class);
                startActivityForResult(intent, PICK_LOCATION_CODE);
            }
        });
        btnPost = findViewById(R.id.btnPost);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEvent();
            }
        });
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });
    }
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
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
                    event.setEventName(etEventName.getText().toString());
                    event.setDescription(etEventDescription.getText().toString());
                    event.setDate(etEventDate.getText().toString());
                    event.setTime(etEventTime.getText().toString());
                    event.setLocation(new ParseGeoPoint(10,2));
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
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            btnPickAPlace.setVisibility(View.GONE);
            btnRepickAPlace.setVisibility(View.VISIBLE);
            tvLocation.setVisibility(View.VISIBLE);
            String name = data.getStringExtra("name");
            tvLocation.setText(name);
            //Toast.makeText(AddEventActivity.this,name,Toast.LENGTH_SHORT).show();
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
}