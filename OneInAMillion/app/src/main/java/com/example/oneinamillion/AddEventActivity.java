package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oneinamillion.Models.Event;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity {
    EditText etEventName;
    EditText etEventDescription;
    EditText etEventLocation;
    EditText etEventTime;
    EditText etEventDate;
    Button btnPost;
    Button btnUploadImage;
    public static final String TAG = "AddEvent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        etEventName = findViewById(R.id.etEventName);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventTime = findViewById(R.id.etEventTime);
        etEventDate = findViewById(R.id.etEventDate);
        btnPost = findViewById(R.id.btnPost);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEvent();
            }
        });
    }

    private void saveEvent() {
        Event event = new Event();
        event.setEventName(etEventName.getText().toString());
        event.setDescription(etEventDescription.getText().toString());
        event.setDate(etEventDate.getText().toString());
        event.setTime(etEventTime.getText().toString());
        event.setLocation(new ParseGeoPoint(10,2));
        event.setOrganizer(ParseUser.getCurrentUser());
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e!=null) {
                    Log.e(TAG,"Error while saving",e);
                    Toast.makeText(AddEventActivity.this, "Error while saving",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG,"Save successful");
            }
        });
    }
}