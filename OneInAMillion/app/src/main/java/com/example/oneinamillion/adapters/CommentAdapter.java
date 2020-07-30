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
import com.example.oneinamillion.Models.Comment;
import com.example.oneinamillion.R;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Comment> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivProfilePicture;
        TextView tvUsername;
        TextView tvDescription;
        ImageView ivLike;
        int likes;
        TextView tvLikes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ivLike.setOnClickListener(this);
        }

        public void bind(Comment comment) {
            tvUsername.setText(comment.getCommenter().getUsername());
            tvDescription.setText(comment.getText());
            if (comment.getCommenter().getParseFile(context
                    .getString(R.string.user_profile_picture_key))!=null){
                Glide.with(context).load(comment.getCommenter()
                        .getParseFile(context
                                .getString(R.string.user_profile_picture_key)).getUrl()).circleCrop().into(ivProfilePicture);
            }
            else {
                ivProfilePicture.setImageDrawable(context.getDrawable(R.drawable.instagram_user_filled_24));
            }
            didUserLike(comment);
        }

        @Override
        public void onClick(View view) {
            if ((Integer) ivLike.getTag() == R.drawable.ufi_heart) {
                ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ufi_heart_active));
                ivLike.setTag(R.drawable.ufi_heart_active);
                ivLike.setColorFilter(Color.RED);
                likes++;
                tvLikes.setText(context.getResources().getQuantityString(R.plurals.numberOfLikes,likes,likes));
                Comment comment = comments.get(getAdapterPosition());
                JSONArray likers = comment.getLikers();
                likers.put(ParseUser.getCurrentUser());
                comment.setLikers(likers);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                    }
                });
            } else {
                ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ufi_heart));
                ivLike.setTag(R.drawable.ufi_heart);
                ivLike.setColorFilter(Color.BLACK);
                likes--;
                tvLikes.setText(context.getResources().getQuantityString(R.plurals.numberOfLikes,likes,likes));
                Comment comment = comments.get(getAdapterPosition());
                int index = 0;
                JSONArray likers = comment.getLikers();
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
                comment.setLikers(likers);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        Log.i("try", "try");
                    }
                });
            }
        }

        private void didUserLike(Comment comment) {
            JSONArray likers = comment.getLikers();
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
            tvLikes.setText(context.getResources().getQuantityString(R.plurals.numberOfLikes,likes,likes));
        }
    }
}
