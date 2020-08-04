package com.example.oneinamillion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;

import java.util.List;

public class HostViewAdapter extends RecyclerView.Adapter<HostViewAdapter.ViewHolder> {
    Context context;
    List<Event> events;

    public HostViewAdapter(Context context, List<Event> events) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_host_view,parent,false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName;
        TextView tvDate;
        TextView tvAttendees;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvDate = itemView.findViewById(R.id.tvDateTime);
            tvAttendees = itemView.findViewById(R.id.tvAttendees);
        }

        public void bind(Event event) {
            tvEventName.setText(event.getEventName());
            tvAttendees.setText(event.getAttendees().length()+" attendees");
            tvDate.setText(event.getDate());
        }
    }
}
