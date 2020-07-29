package com.example.oneinamillion.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.oneinamillion.EventDetailsActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class HorizontalEventAdapter extends RecyclerView.Adapter<HorizontalEventAdapter.ViewHolder> {
    Context context;
    List<Event> events;
    public static final String TAG = "HorizontalEventAdapter";

    public HorizontalEventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Event> list) {
        events.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.horizontal_item_event,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvEventName;
        TextView tvLocation;
        ImageView ivEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ivEvent = itemView.findViewById(R.id.ivEventImage);
            itemView.setOnClickListener(this);
        }

        public void bind(Event event) {
            tvEventName.setText(event.getEventName());
            tvLocation.setText(String.valueOf(event.getPrice()));
            if (event.getImage() != null) {
                Glide.with(context).load(event.getImage().getUrl()).into(ivEvent);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            final Event event = events.get(position);
            Log.i(TAG,String.valueOf(event.getDistance()));
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("https://maps.googleapis.com/maps/api/geocode/json?latlng="+
                    String.valueOf(event.getLocation().getLatitude())+","+String.valueOf(event.getLocation().getLongitude())+
                    "&key="+context.getString(R.string.api_key), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "onSuccess");
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        String address = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                        Intent i = new Intent(context, EventDetailsActivity.class);
                        i.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                        i.putExtra("address",address);
                        i.putExtra("activity","AdapterItem");
                        i.putExtra("distance",event.getDistance());
                        context.startActivity(i);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Hit JSON exception",e);
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.i(TAG, "Failed");
                }
            });
        }
    }
}
