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

import com.example.oneinamillion.EventDetailsActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.EventForSaving;
import com.example.oneinamillion.R;

import org.parceler.Parcels;

import java.util.List;

public class SavedEventAdapter extends RecyclerView.Adapter<SavedEventAdapter.ViewHolder> {
    Context context;
    List<EventForSaving> events;
    public static final String TAG = "SavedEventAdapter";

    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<EventForSaving> list) {
        events.addAll(list);
        notifyDataSetChanged();
    }

    public SavedEventAdapter(Context context, List<EventForSaving> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventForSaving event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvDateTime;
        TextView tvPrice;
        TextView tvEventName;
        ImageView ivEventImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
        }

        public void bind(EventForSaving event) {
            tvEventName.setText(event.getEventName());
            tvDateTime.setText(event.getEventDate()+" at "+event.getEventTime());
            //tvPrice.setText(event.getEventAddress());
            ivEventImage.setImageDrawable(context.getDrawable(R.drawable.placeholder));
            ivEventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final EventForSaving event = events.get(position);
                    Log.i(TAG,String.valueOf(event.getDistance()));
                    Intent i = new Intent(context, EventDetailsActivity.class);
                    i.putExtra(EventForSaving.class.getSimpleName(), Parcels.wrap(event));
                    i.putExtra("address", event.getEventAddress());
                    i.putExtra("activity", "AdapterItem");
                    i.putExtra("distance", event.getDistance());
                    context.startActivity(i);
                }
            });
        }
    }
}