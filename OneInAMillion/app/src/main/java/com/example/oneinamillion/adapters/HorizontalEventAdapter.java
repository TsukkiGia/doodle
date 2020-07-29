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
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;

import java.util.List;

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

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvEventName;
        TextView tvLocation;
        ImageView ivEvent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ivEvent = itemView.findViewById(R.id.ivEventImage);
        }

        public void bind(Event event) {
            tvEventName.setText(event.getEventName());
            tvLocation.setText(String.valueOf(event.getPrice()));
            if (event.getImage() != null) {
                Glide.with(context).load(event.getImage().getUrl()).into(ivEvent);
            }
        }
    }
}
