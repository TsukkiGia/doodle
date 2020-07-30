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
import com.example.oneinamillion.R;
import com.parse.ParseUser;

import java.util.List;

public class LikersAdapter extends RecyclerView.Adapter<LikersAdapter.ViewHolder> {
    Context context;
    List<ParseUser> likers;

    public LikersAdapter(Context context, List<ParseUser> likers) {
        this.context = context;
        this.likers = likers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_liker,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user  = likers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return likers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvUsername;
        TextView tvName;
        ImageView ivProfilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvName = itemView.findViewById(R.id.tvName);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
        }

        public void bind(ParseUser parseUser) {
            tvUsername.setText(parseUser.getUsername());
            tvName.setText(parseUser.getString("FirstName")+" "+parseUser.getString("LastName"));
            if (parseUser.getParseFile("ProfileImage")!= null){
                Glide.with(context).load(parseUser.getParseFile("ProfileImage").getUrl()).circleCrop().into(ivProfilePicture);
            }
            else {
                ivProfilePicture.setImageDrawable(context.getDrawable(R.drawable.instagram_user_filled_24));
            }
        }
    }
}
