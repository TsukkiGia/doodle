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
import com.example.oneinamillion.EventDetailsActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;

import org.parceler.Parcels;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    List<Event> events;
    Context context;
    public static final String TAG = "SearchResultsAdapter";

    public SearchResultAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result,parent,false);
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
        TextView tvDescription;
        ImageView ivEventImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            ivEventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Event event = events.get(position);
                    Log.i(TAG,String.valueOf(event.getDistance()));
                    Intent i = new Intent(context, EventDetailsActivity.class);
                    i.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                    i.putExtra("address", event.getParseAddress());
                    i.putExtra("activity", "AdapterItem");
                    i.putExtra("distance", event.getDistance());
                    context.startActivity(i);
                }
            });
        }

        public void bind(Event event) {
            tvEventName.setText(event.getEventName());
            tvDescription.setText(event.getDescription());
            Glide.with(context).load(event.getImage().getUrl()).into(ivEventImage);
        }
    }
}
