package com.example.oneinamillion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.EventForSaving;
import com.example.oneinamillion.R;

import java.util.List;

public class SavedEventAdapter extends RecyclerView.Adapter<SavedEventAdapter.ViewHolder> {
    Context context;
    List<EventForSaving> events;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvEventName = itemView.findViewById(R.id.tvEventName);
        }

        public void bind(EventForSaving event) {
            tvEventName.setText(event.getEventName());
            tvDateTime.setText(event.getEventDate()+" at "+event.getEventTime());
        }
    }
}
