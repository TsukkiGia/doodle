package com.example.oneinamillion;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.Post;
import com.example.oneinamillion.Models.Util;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class AddPostActivity extends AppCompatActivity {
    ImageView ivCamera;
    ImageView ivGallery;
    Button btnPost;
    TextInputEditText etPost;
    public static final String TAG = "AddPostActivity";
    public final static int PICK_PHOTO_CODE = 1046;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 24;
    public static final int PICK_VIDEO_CODE = 20;
    public String photoFileName = "finalphoto.jpg";
    File photoFile;
    ImageView ivImage;
    ParseFile file;
    String fromCameraorGallery = "video";
    Event event;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        setContentView(R.layout.activity_add_post);
        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        btnPost = findViewById(R.id.btnPost);
        etPost = findViewById(R.id.etPost);
        ivCamera = findViewById(R.id.ivCamera);
        ivGallery = findViewById(R.id.ivGallery);
        ivImage = findViewById(R.id.ivImage);
    }

    private void setClickListeners() {
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Posting");
                savePost();
            }
        });
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto();
            }
        });
    }

    private void savePost() {
        notifyOrganizer();
        post = new Post();
        post.setAuthor(ParseUser.getCurrentUser());
        post.setDescription(etPost.getText().toString());
        post.setEventID(event.getObjectId());
        if (fromCameraorGallery.equals("camera")){
            post.setImage(new ParseFile(photoFile));
        }
        if(fromCameraorGallery.equals("gallery")){
            post.setImage(file);
        }
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent i = new Intent();
                i.putExtra("post",Parcels.wrap(post));
                setResult(RESULT_OK,i);
                finish();
            }
        });
    }

    private void notifyOrganizer() {
        BackgroundMail.newBuilder(AddPostActivity.this)
                .withUsername("aprilgtropse@gmail.com")
                .withPassword("FinnBalor")
                .withMailto(event.getOrganizer().getString("AltEmail"))
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Someone posted under your event")
                .withProcessVisibility(false)
                .withBody("Hi! "+ParseUser.getCurrentUser().getString("FirstName")+
                        " posted underneath your event. Open One In a Million to see the post!")
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                    }
                })
                .send();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.i(TAG,"camera");
            ivImage.setVisibility(View.VISIBLE);
            fromCameraorGallery = "camera";
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            ivImage.setImageBitmap(takenImage);
        }
        if((data != null) && requestCode==PICK_PHOTO_CODE){
            Log.i(TAG,"gallery");
            Uri photoUri = data.getData();
            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            // Load the selected image into a preview
            ivImage.setImageBitmap(selectedImage);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            ivImage.setVisibility(View.VISIBLE);
            fromCameraorGallery = "gallery";
            // Create the ParseFile
            file = new ParseFile("picture.png", image);
        }
    }

    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public String getPath(Uri uriPath){
        String[] data = {MediaStore.Video.Media.DATA};
        CursorLoader loader = new CursorLoader(AddPostActivity.this,uriPath,data,null,null,null);
        Cursor cursor = loader.loadInBackground();
        int index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }

    public void onPickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setTypeAndNormalize("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Video"),  PICK_VIDEO_CODE);
        /*if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_VIDEO_CODE);
        }*/
    }

    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        // required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(getApplicationContext(), "com.gianna.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
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
}