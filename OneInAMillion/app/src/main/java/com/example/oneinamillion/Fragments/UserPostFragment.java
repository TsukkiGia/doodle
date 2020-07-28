package com.example.oneinamillion.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.oneinamillion.AddPostActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.Post;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.PostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UserPostFragment extends Fragment {
    List<Post> posts;
    RecyclerView rvUserPosts;
    PostAdapter postAdapter;
    TextInputEditText etPost;
    TextInputLayout tfInputLayout;
    Event event;
    public static final String TAG = "UserPostFragment";

    public UserPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = Parcels.unwrap(getArguments().getParcelable("event"));
        Log.i(TAG,event.getDescription());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200 && resultCode == RESULT_OK) {
            Post post = Parcels.unwrap(data.getParcelableExtra("post"));
            posts.add(0, post);
            postAdapter.notifyItemInserted(0);
            rvUserPosts.smoothScrollToPosition(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvUserPosts = view.findViewById(R.id.rvUserPosts);
        etPost = view.findViewById(R.id.etPost);
        tfInputLayout = view.findViewById(R.id.tfPost);
        etPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AddPostActivity.class);
                i.putExtra(Event.class.getSimpleName(),Parcels.wrap(event));
                startActivityForResult(i,200);
            }
        });
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts,getContext());
        rvUserPosts.setAdapter(postAdapter);
        rvUserPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.whereEqualTo(Post.KEY_EVENT,event.getObjectId());
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                postAdapter.clear();
                postAdapter.addAll(posts);
                postAdapter.notifyDataSetChanged();
            }
        });
    }
}