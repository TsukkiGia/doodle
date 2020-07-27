package com.example.oneinamillion.adapters;

import android.content.Context;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
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
        }

        @Override
        public void onClick(View view) {

        }
    }
}
