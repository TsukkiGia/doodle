package com.example.oneinamillion.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.R;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

    TextView tvName;
    TextView tvUsername;
    ImageView ivProfile;

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
        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfile = view.findViewById(R.id.ivProfile);
        tvName.setText(String.valueOf(ParseUser.getCurrentUser().getString("FirstName") + " " + String.valueOf(ParseUser.getCurrentUser().getString("LastName"))));
        tvUsername.setText("@"+ParseUser.getCurrentUser().getUsername());
        if (ParseUser.getCurrentUser().getParseFile("ProfilePicture") != null) {
            Glide.with(getContext()).load(ParseUser.getCurrentUser().getParseFile("ProfilePicture").getUrl());
        }
        else {
            ivProfile.setImageDrawable(getContext().getResources().getDrawable(R.drawable.instagram_user_filled_24));
        }
    }
}