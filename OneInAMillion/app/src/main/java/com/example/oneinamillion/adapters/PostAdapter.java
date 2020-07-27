package com.example.oneinamillion.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.Models.Post;
import com.example.oneinamillion.R;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    List<Post> posts;
    Context context;
    public static final String TAG = "PostAdapter";

    public PostAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivProfilePicture;
        TextView tvUsername;
        TextView tvDescription;
        ImageView ivLike;
        ImageView ivComment;
        ImageView ivPost;
        TextView tvLikes;
        int likes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivPost = itemView.findViewById(R.id.ivPost);
            tvLikes = itemView.findViewById(R.id.tvLikesAndComments);
            ivLike.setOnClickListener(this);
            ivComment.setOnClickListener(this);
        }

        public void bind(Post post) {
            tvUsername.setText(post.getAuthor().getUsername());
            tvDescription.setText(post.getDescription());
            if (post.getAuthor().getParseFile("ProfileImage")!=null){
                Glide.with(context).load(post.getAuthor().getParseFile("ProfileImage").getUrl()).circleCrop().into(ivProfilePicture);
            }
            else {
                ivProfilePicture.setImageDrawable(context.getDrawable(R.drawable.instagram_user_filled_24));
            }
            JSONArray likers = post.getLikers();
            Boolean didILike = false;
            for (int i = 0; i < likers.length(); i++) {
                try {
                    JSONObject user = (JSONObject) likers.get(i);
                    String userID = user.getString("objectId");
                    if (userID.equals(ParseUser.getCurrentUser().getObjectId())) {
                        didILike = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            likes = likers.length();
            if (didILike){
                ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ufi_heart_active));
                ivLike.setTag(R.drawable.ufi_heart_active);
                ivLike.setColorFilter(Color.RED);
            }
            else {
                ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ufi_heart));
                ivLike.setTag(R.drawable.ufi_heart);
                ivLike.setColorFilter(Color.BLACK);
            }
            /*if (likes!=1) {
                tvLikes.setText(String.valueOf(likes) + " likes");
            }
            else {
                tvLikes.setText("1 like");
            }*/
        }

        @Override
        public void onClick(View view) {
            if ((Integer) ivLike.getTag()==R.drawable.ufi_heart) {
                ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ufi_heart_active));
                ivLike.setTag(R.drawable.ufi_heart_active);
                ivLike.setColorFilter(Color.RED);
                likes++;
                if (likes != 1) {
                    tvLikes.setText(String.valueOf(likes) + " likes");
                }
                else {
                    tvLikes.setText("1 like");
                }
                Post post = posts.get(getAdapterPosition());
                JSONArray likers = post.getLikers();
                likers.put(ParseUser.getCurrentUser());
                post.setLikers(likers);
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                    }
                });
            }
            else {
                ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ufi_heart));
                ivLike.setTag(R.drawable.ufi_heart);
                ivLike.setColorFilter(Color.BLACK);
                likes--;
                if (likes!=1) {
                    tvLikes.setText(String.valueOf(likes) + " likes");
                }
                else {
                    tvLikes.setText("1 like");
                }
                Post post = posts.get(getAdapterPosition());
                int index = 0;
                JSONArray likers = post.getLikers();
                for (int i = 0; i < likers.length()-1; i++) {
                    try {
                        ParseUser user = (ParseUser) likers.get(i);
                        if (user.equals(ParseUser.getCurrentUser())) {
                            index=i;
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
                        Log.i("try","try");
                    }
                });
            }
        }
    }
}
