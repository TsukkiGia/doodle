package com.example.oneinamillion.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.oneinamillion.DetailsActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;
import com.parse.ParseUser;

import org.json.JSONArray;
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

    public EventAdapter(Context context, List<Event> events) {
        this.events=events;
        this.context=context;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
        TextView tvDateTime;
        TextView tvPrice;
        TextView tvEventName;
        ImageView ivEventImage;
        private GestureDetector mGestureDetector;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            ivEventImage.setOnTouchListener(this);
            mGestureDetector = new GestureDetector(context,this);
        }

        public void bind(Event event) throws ParseException {
            tvPrice.setText("$"+String.valueOf(event.getPrice()));
            tvEventName.setText(event.getEventName());
            long now = System.currentTimeMillis();
            Date firstDate = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.ENGLISH).parse(event.getDate()+" "+event.getTime());
            Log.i(TAG,event.getEventName()+" "+String.valueOf(firstDate.getTime())+" "+firstDate.toString());
            long diffInMillies = firstDate.getTime();
            tvDateTime.setText(DateUtils.getRelativeTimeSpanString(diffInMillies, now, 0L, DateUtils.FORMAT_ABBREV_ALL));
            if (event.getImage() != null) {
                Glide.with(context).load(event.getImage().getUrl()).into(ivEventImage);
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            int position = getAdapterPosition();
            final Event event = events.get(position);
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
                        Intent i = new Intent(context, DetailsActivity.class);
                        i.putExtra(Event.class.getSimpleName(),Parcels.wrap(event));
                        i.putExtra("address",address);
                        i.putExtra("activity","AdapterItem");
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
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            Boolean amIattending = false;
            int position = getAdapterPosition();
            final Event event = events.get(position);
            JSONArray attendees = event.getAttendees();
            for(int i = 0; i<attendees.length();i++ ){
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
                for(int i = 0; i<attendees.length();i++ ){
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
                Toast.makeText(context,"You have successfully unRSVPed!",Toast.LENGTH_SHORT).show();
                event.setAttendees(attendees);
                event.saveInBackground();
            }
            else {
                Toast.makeText(context,"You have successfully RSVPed!",Toast.LENGTH_SHORT).show();
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
}
