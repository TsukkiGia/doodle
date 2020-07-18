package com.example.oneinamillion.adapters;

import android.content.Context;
import android.content.Intent;
import android.icu.text.RelativeDateTimeFormatter;
import android.text.format.DateUtils;
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
import com.example.oneinamillion.DetailsActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    List<Event> events;
    Context context;
    public static final String TAG = "EventAdapter";
    String address;

    public EventAdapter(Context context, List<Event> events) {
        this.events=events;
        this.context=context;
    }

    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Event> list) {
        events.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        try {
            holder.bind(event);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDateTime;
        TextView tvLocation;
        TextView tvEventName;
        ImageView ivEventImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            ivEventImage = itemView.findViewById(R.id.ivEvent);
            ivEventImage.setOnClickListener(this);
        }

        public void bind(Event event) throws ParseException {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("https://maps.googleapis.com/maps/api/geocode/json?latlng="+
                    String.valueOf(event.getLocation().getLatitude())+","+String.valueOf(event.getLocation().getLongitude())+
                    "&key=AIzaSyAHhqNOmXH6jPO42U89s12nJNAQucTvw40", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "onSuccess");
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        address = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                        Log.i(TAG, "Results: "+address);
                        tvLocation.setText(address);
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
            tvEventName.setText(event.getEventName());
            long now = System.currentTimeMillis();
            Date firstDate = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(event.getDate());
            long diffInMillies = firstDate.getTime();
            tvDateTime.setText(DateUtils.getRelativeTimeSpanString(diffInMillies, now, 0L, DateUtils.FORMAT_ABBREV_ALL));
            if (event.getImage() != null) {
                Glide.with(context).load(event.getImage().getUrl()).into(ivEventImage);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Event event = events.get(position);
            Intent i = new Intent(context, DetailsActivity.class);
            i.putExtra(Event.class.getSimpleName(),Parcels.wrap(event));
            i.putExtra("address",address);
            context.startActivity(i);
        }
    }
}
