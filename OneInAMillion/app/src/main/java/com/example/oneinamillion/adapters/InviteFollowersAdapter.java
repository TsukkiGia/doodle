package com.example.oneinamillion.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class InviteFollowersAdapter extends RecyclerView.Adapter<InviteFollowersAdapter.ViewHolder> {
    Context context;
    List<ParseUser> followers;
    Event event;
    List<String> followersToInvite = new ArrayList<>();

    public List<String> getFollowersToInvite() {
        return followersToInvite;
    }

    public static final String TAG = "InviteAdapter";

    public InviteFollowersAdapter(Context context, List<ParseUser> followers, Event event) {
        this.context = context;
        this.followers = followers;
        this.event = event;
    }

    public void clear() {
        followers.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ParseUser> list) {
        followers.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follower,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser follower = followers.get(position);
        holder.bind(follower);
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvUsername;
        TextView tvName;
        ImageView ivProfilePicture;
        CheckBox cbSend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvName = itemView.findViewById(R.id.tvName);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            cbSend = itemView.findViewById(R.id.cbSend);
        }

        public void bind(final ParseUser follower) {
            tvUsername.setText(follower.getUsername());
            tvName.setText(follower.getString("FirstName")+" "+follower.getString("LastName"));
            if (follower.getParseFile("ProfileImage")!= null){
                Glide.with(context).load(follower.getParseFile("ProfileImage").getUrl()).circleCrop().into(ivProfilePicture);
            }
            else {
                Glide.with(context).load(getURLForResource(R.drawable.defaultprofile))
                        .circleCrop().into(ivProfilePicture);
            }
            cbSend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        followersToInvite.add(follower.getString("AltEmail"));
                    }
                    else{
                        for (int i = 0; i < followersToInvite.size(); i++){
                            if (followersToInvite.get(i).equals(follower.getString("AltEmail"))){
                                followersToInvite.remove(i);
                                break;
                            }
                        }
                    }
                    Log.i(TAG,followersToInvite.toString());
                }
            });
        }
        public String getURLForResource (int resourceId) {
            //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
            return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
        }
    }
}
