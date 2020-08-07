package com.example.oneinamillion.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.oneinamillion.EventDetailsActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.EventDao;
import com.example.oneinamillion.Models.EventForSaving;
import com.example.oneinamillion.ParseApplication;
import com.example.oneinamillion.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Event> events;
    Context context;
    public static final String TAG = "EventAdapter";
    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    public EventAdapter(Context context, List<Event> events) {
        this.events=events;
        this.context=context;
    }

    @Override
    public int getItemViewType(int position) {
        if(events.get(position).isShowMenu()){
            return SHOW_MENU;
        }else{
            return HIDE_MENU;
        }
    }

    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Event> list) {
        events.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SHOW_MENU) {
            View view = LayoutInflater.from(context).inflate(R.layout.menu, parent, false);
            return new MenuViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Event event = events.get(position);
        if (holder instanceof ViewHolder){
            Log.i(TAG,String.valueOf(event.getDistance()));
            ((ViewHolder) holder).tvPrice.setText(String.format("$%s", event.getPrice()));
            ((ViewHolder) holder).tvEventName.setText(event.getEventName());
            long now = System.currentTimeMillis();
            Date DateTime = null;
            try {
                DateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm",
                        Locale.ENGLISH).parse(event.getDate()+" "+event.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long DateTimeInMillies = DateTime.getTime();
            ((ViewHolder) holder).tvDateTime.setText(
                    DateUtils.getRelativeTimeSpanString(DateTimeInMillies, now, 0L, DateUtils.FORMAT_ABBREV_ALL));
            if (event.getImage() != null) {
                Glide.with(context).load(event.getImage().getUrl()).into(((ViewHolder) holder).ivEventImage);
            }
            if (event.getAddress()==null) {
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
                            event.setAddress(address);
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
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void showMenu(int position) {
        for (int i = 0; i <events.size(); i++){
            events.get(i).setShowMenu(false);
        }
        events.get(position).setShowMenu(true);
        notifyDataSetChanged();
    }


    public boolean isMenuShown() {
        for(int i = 0; i < events.size(); i++){
            if(events.get(i).isShowMenu()){
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for(int i = 0; i<events.size(); i++){
            events.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
        TextView tvDateTime;
        TextView tvPrice;
        TextView tvEventName;
        ImageView ivEventImage;
        RelativeLayout container;
        private GestureDetector mGestureDetector;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            ivEventImage.setOnTouchListener(this);
            mGestureDetector = new GestureDetector(context,this);
            container = itemView.findViewById(R.id.rlContainer);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            int position = getAdapterPosition();
            final Event event = events.get(position);
            Log.i(TAG,String.valueOf(event.getDistance()));
            Intent i = new Intent(context, EventDetailsActivity.class);
            i.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
            i.putExtra("address", event.getAddress());
            i.putExtra("activity", "AdapterItem");
            i.putExtra("distance", event.getDistance());
            context.startActivity(i);
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            Boolean amIattending = false;
            int position = getAdapterPosition();
            final Event event = events.get(position);
            JSONArray attendees = event.getAttendees();
            for (int i = 0; i < attendees.length(); i++){
                try {
                    String userID = attendees.getString(i);
                    if (userID.equals(ParseUser.getCurrentUser().getObjectId())) {
                        amIattending = true;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (amIattending) {
                for (int i = 0; i < attendees.length(); i++){
                    try {
                        String userID = attendees.getString(i);
                        if (userID.equals(ParseUser.getCurrentUser().getObjectId())) {
                            attendees.remove(i);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                };
                Snackbar.make(itemView, R.string.successful_unRSVP, Snackbar.LENGTH_SHORT)
                        .setAction(context.getString(R.string.snackbar_dismiss),onClickListener).show();
                event.setAttendees(attendees);
                event.saveInBackground();
            }
            else {
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                };
                Snackbar.make(itemView,"You have successfully RSVPed!", Snackbar.LENGTH_SHORT)
                        .setAction(context.getString(R.string.snackbar_dismiss),onClickListener).show();
                attendees.put(ParseUser.getCurrentUser().getObjectId());
                event.setAttendees(attendees);
                event.saveInBackground();
            }
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            Log.i(TAG,"flung");
            return false;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(view.getId()==R.id.ivEventImage) {
                mGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
            return false;
        }
    }

    private class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDelete;

        public MenuViewHolder(View view) {
            super(view);
            ivDelete = view.findViewById(R.id.ivDelete);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Event event = events.get(getAdapterPosition());
                    JSONArray attendees = event.getAttendees();
                    for (int i = 0; i < attendees.length(); i++){
                        try {
                            String userID = attendees.getString(i);
                            if (userID.equals(ParseUser.getCurrentUser().getObjectId())) {
                                attendees.remove(i);
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    };
                    Snackbar.make(itemView, R.string.successful_unRSVP, Snackbar.LENGTH_SHORT)
                            .setAction(context.getString(R.string.snackbar_dismiss),onClickListener).show();
                    event.setAttendees(attendees);
                    event.saveInBackground();
                    for (int i = 0; i < events.size(); i++){
                        if (events.get(i).getObjectId().equals(event.getObjectId())){
                            events.remove(i);
                            notifyItemRemoved(i);
                        }
                    }
                }
            });
        }
    }
}
