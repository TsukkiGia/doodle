package com.example.oneinamillion.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.oneinamillion.EventDetailsActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class InviteFollowersAdapter extends RecyclerView.Adapter<InviteFollowersAdapter.ViewHolder> {
    Context context;
    List<ParseUser> followers;
    Event event;
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
        Button btnInvite;
        Boolean canSend = true;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvName = itemView.findViewById(R.id.tvName);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            btnInvite = itemView.findViewById(R.id.btnInvite);
        }

        public void bind(final ParseUser follower) {
            tvUsername.setText(follower.getUsername());
            tvName.setText(follower.getString("FirstName")+" "+follower.getString("LastName"));
            if (follower.getParseFile("ProfileImage")!= null){
                Glide.with(context).load(follower.getParseFile("ProfileImage").getUrl()).circleCrop().into(ivProfilePicture);
            }
            else {
                ivProfilePicture.setImageDrawable(context.getDrawable(R.drawable.instagram_user_filled_24));
            }
            btnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (canSend) {
                        Log.i(TAG, follower.getUsername());
                        Log.i(TAG, String.valueOf(follower.getString("email") == null));
                        Log.i(TAG, String.valueOf(follower.getEmail() == null));
                        Log.i(TAG, String.valueOf(follower.getString("AltEmail") == null));
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.get("https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                                String.valueOf(event.getLocation().getLatitude()) + "," + String.valueOf(event.getLocation().getLongitude()) +
                                "&key=" + context.getString(R.string.api_key), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess");
                                JSONObject jsonObject = json.jsonObject;
                                try {
                                    String address = jsonObject.getJSONArray("results")
                                            .getJSONObject(0).getString("formatted_address");
                                    BackgroundMail.newBuilder(context)
                                            .withUsername("aprilgtropse@gmail.com")
                                            .withPassword("FinnBalor")
                                            .withProcessVisibility(false)
                                            .withMailto(follower.getString("AltEmail"))
                                            .withType(BackgroundMail.TYPE_PLAIN)
                                            .withSubject("Invite to my event")
                                            .withBody("Hi! On " + event.getDate() + " at " + event.getTime() + ", I will be hosting " + event.getEventName()
                                                    + " and I will love for you to join! This event would be taking place at " + address + ". I hope to see you there!")
                                            .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                                                @Override
                                                public void onSuccess() {
                                                }
                                            })
                                            .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                                                @Override
                                                public void onFail() {
                                                }
                                            })
                                            .send();
                                    btnInvite.setText("Sent");
                                    btnInvite.setBackgroundColor(Color.GRAY);
                                    canSend = false;
                                } catch (JSONException e) {
                                    Log.e(TAG, "Hit JSON exception", e);
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "Failed", throwable);
                            }
                        });
                    }
                }
            });
        }
    }
}
