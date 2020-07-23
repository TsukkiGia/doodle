package com.example.oneinamillion.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.Post;
import com.example.oneinamillion.R;
import com.example.oneinamillion.adapters.PostAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class UserPostFragment extends Fragment {
    List<Post> posts;
    RecyclerView rvUserPosts;
    PostAdapter postAdapter;

    public UserPostFragment() {
        // Required empty public constructor
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
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts,getContext());
        rvUserPosts.setAdapter(postAdapter);
        rvUserPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
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