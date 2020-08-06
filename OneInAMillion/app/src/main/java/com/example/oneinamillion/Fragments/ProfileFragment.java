package com.example.oneinamillion.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.oneinamillion.HostingActivity;
import com.example.oneinamillion.InterestActivity;
import com.example.oneinamillion.LoginActivity;
import com.example.oneinamillion.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    TextView tvName;
    TextView tvUsername;
    ImageView ivProfile;
    Button btnChange;
    Button btnChangePreferences;
    ImageView ivLogOut;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 24;
    public String photoFileName = "bigphoto";
    int counter = 0;
    File photoFile;
    public static final String TAG = "ProfileFragment";
    Button btnHosting;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setUpProfileViews();
        setClickListeners();
    }

    private void setClickListeners() {
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
        btnChangePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), InterestActivity.class);
                i.putExtra("activity","profile");
                startActivity(i);
            }
        });
        ivLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        btnHosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), HostingActivity.class);
                startActivity(i);
            }
        });
    }

    private void setUpProfileViews() {
        tvName.setText(String.format("%s %s", ParseUser.getCurrentUser().getString("FirstName"),
                ParseUser.getCurrentUser().getString("LastName")));
        tvUsername.setText(String.format("@%s", ParseUser.getCurrentUser().getUsername()));
        if (ParseUser.getCurrentUser().getParseFile("ProfileImage") != null) {
            Glide.with(getContext()).load(ParseUser.getCurrentUser().getParseFile("ProfileImage").getUrl())
                    .centerCrop().into(ivProfile);
        } else {
            ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.instagram_user_filled_24));
        }
    }

    private void initializeViews(View view) {
        btnChangePreferences = view.findViewById(R.id.btnChangePref);
        tvName = view.findViewById(R.id.tvName);
        btnChange = view.findViewById(R.id.btnChange);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfile = view.findViewById(R.id.ivProfile);
        ivLogOut = view.findViewById(R.id.ivLogOut);
        btnHosting = view.findViewById(R.id.btnHosting);
    }

    private void logout() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            LoginManager.getInstance().logOut();
        }
        ParseUser.getCurrentUser().logOut();
        Intent i = new Intent(getContext(), LoginActivity.class);
        getContext().startActivity(i);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Glide.with(getContext()).load(photoFile.getAbsolutePath()).centerCrop().into(ivProfile);
                ParseUser.getCurrentUser().put("ProfileImage", new ParseFile(photoFile));
                ParseUser.getCurrentUser().saveInBackground();
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName+counter+".jpg");
        counter++;
        // required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(getActivity(),
                "com.gianna.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }
}