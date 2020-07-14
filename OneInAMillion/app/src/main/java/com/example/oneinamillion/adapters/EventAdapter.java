package com.example.oneinamillion.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.DetailsActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;

import org.parceler.Parcels;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    List<Event> events;
    Context context;
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
        holder.bind(event);
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

        public void bind(Event event) {
            tvLocation.setText("Hawaii");
            tvEventName.setText(event.getEventName());
            tvDateTime.setText(event.getDate().toString());
            Glide.with(context).load(event.getImage().getUrl()).into(ivEventImage);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Event event = events.get(position);
            Intent i = new Intent(context, DetailsActivity.class);
            i.putExtra(Event.class.getSimpleName(),Parcels.wrap(event));
            context.startActivity(i);
        }
    }
}
