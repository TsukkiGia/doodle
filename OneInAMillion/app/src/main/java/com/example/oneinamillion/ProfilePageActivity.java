package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

public class ProfilePageActivity extends AppCompatActivity {
    ImageView ivProfilePicture;
    TextView tvUsername;
    TextView tvName;
    Button btnAddFriend;
    Boolean friended=false;
    ParseUser user;
    public static final String TAG = "ProfilePageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        ivProfilePicture = findViewById(R.id.ivProfile);
        tvUsername = findViewById(R.id.tvUsername);
        tvName = findViewById(R.id.tvName);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        tvUsername.setText(user.getUsername());
        tvName.setText(user.getString("FirstName") + " "+user.getString("LastName"));
        if (user.getParseFile("ProfileImage") != null) {
            Glide.with(ProfilePageActivity.this).load(user.getParseFile("ProfileImage").getUrl())
                    .circleCrop().into(ivProfilePicture);
        } else {
            ivProfilePicture.setImageDrawable(getDrawable(R.drawable.instagram_user_filled_24));
        }
        JSONArray friends = ParseUser.getCurrentUser().getJSONArray("Friends");
        for (int i = 0; i < friends.length();i++){
            try {

                if (friends.get(i).equals(user.getObjectId())){
                    btnAddFriend.setText("Friends");
                    friended = true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,friended.toString());
                if (friended) {
                    int index = 0;
                    JSONArray friends = ParseUser.getCurrentUser().getJSONArray("Friends");
                    for (int i = 0; i < friends.length();i++){
                        try {
                            if (friends.get(i).equals(user.getObjectId())){
                                index = i;
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    friends.remove(index);
                    ParseUser.getCurrentUser().put("Friends",friends);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i(TAG, "done");
                            }
                            else {
                                Log.e(TAG,"help!",e);
                            }
                        }
                    });
                    btnAddFriend.setText("Add Friend");
                    friended = false;
                }
                else {
                    JSONArray friends = ParseUser.getCurrentUser().getJSONArray("Friends");
                    friends.put(user.getObjectId());
                    ParseUser.getCurrentUser().put("Friends",friends);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i(TAG, "done");
                            }
                            else {
                                Log.e(TAG,"help!",e);
                            }
                        }
                    });
                    btnAddFriend.setText("Friends");
                    friended = true;
                }
            }
        });
    }
}