package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.Models.Comment;
import com.example.oneinamillion.Models.Post;
import com.example.oneinamillion.adapters.CommentAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {
    Post post;
    ImageView ivProfilePicture;
    TextView tvUsername;
    TextView tvDescription;
    ImageView ivLike;
    ImageView ivComment;
    Boolean didIlike;
    RecyclerView rvComments;
    CommentAdapter commentAdapter;
    List<Comment> comments;
    TextInputEditText etComment;
    public static final String TAG = "PostDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        ivLike = findViewById(R.id.ivLike);
        rvComments = findViewById(R.id.rvComments);
        ivComment = findViewById(R.id.ivComment);
        etComment = findViewById(R.id.etComment);
        comments = new ArrayList<>();
        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        didIlike = getIntent().getBooleanExtra("didilike",false);
        tvUsername.setText(post.getAuthor().getUsername());
        tvDescription.setText(post.getDescription());
        commentAdapter = new CommentAdapter(PostDetailsActivity.this,comments);
        rvComments.setAdapter(commentAdapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        if (post.getAuthor().getParseFile("ProfileImage")!=null){
            Glide.with(PostDetailsActivity.this).load(post.getAuthor().getParseFile("ProfileImage").getUrl()).circleCrop().into(ivProfilePicture);
        }
        else {
            ivProfilePicture.setImageDrawable(getDrawable(R.drawable.instagram_user_filled_24));
        }
        if (didIlike){
            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ufi_heart_active));
            ivLike.setColorFilter(Color.RED);
        }
        else {
            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ufi_heart));
            ivLike.setColorFilter(Color.BLACK);
        }
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeorunlike();
            }
        });
        queryComments();
        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etComment.getText().length()!=0){
                    Comment comment = new Comment();
                    comment.setCommenter(ParseUser.getCurrentUser());
                    comment.setPost(post.getObjectId());
                    comment.setText(etComment.getText().toString());
                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.i(TAG,"done");
                            etComment.setText("");
                            queryComments();
                        }
                    });
                }
            }
        });
    }

    private void likeorunlike() {
        if (!didIlike) {
            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ufi_heart_active));
            ivLike.setColorFilter(Color.RED);
            JSONArray likers = post.getLikers();
            likers.put(ParseUser.getCurrentUser());
            didIlike=true;
            post.setLikers(likers);
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                }
            });
        } else {
            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ufi_heart));
            ivLike.setColorFilter(Color.BLACK);
            didIlike=false;
            int index = 0;
            JSONArray likers = post.getLikers();
            for (int i = 0; i < likers.length() - 1; i++) {
                try {
                    ParseUser user = (ParseUser) likers.get(i);
                    if (user.equals(ParseUser.getCurrentUser())) {
                        index = i;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            likers.remove(index);
            post.setLikers(likers);
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {

                }
            });
        }
    }

    private void queryComments() {
        ParseQuery<Comment> parseQuery = ParseQuery.getQuery(Comment.class);
        parseQuery.include(Comment.KEY_USER);
        parseQuery.whereEqualTo(Comment.KEY_POST,post.getObjectId());
        parseQuery.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                commentAdapter.clear();
                commentAdapter.addAll(comments);
            }
        });
    }
}