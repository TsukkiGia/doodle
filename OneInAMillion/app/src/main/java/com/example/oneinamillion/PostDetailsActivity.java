package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.Models.Post;

import org.parceler.Parcels;

public class PostDetailsActivity extends AppCompatActivity {
    Post post;
    ImageView ivProfilePicture;
    TextView tvUsername;
    TextView tvDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvUsername.setText(post.getAuthor().getUsername());
        tvDescription.setText(post.getDescription());
        if (post.getAuthor().getParseFile("ProfileImage")!=null){
            Glide.with(PostDetailsActivity.this).load(post.getAuthor().getParseFile("ProfileImage").getUrl()).circleCrop().into(ivProfilePicture);
        }
        else {
            ivProfilePicture.setImageDrawable(getDrawable(R.drawable.instagram_user_filled_24));
        }
    }
}