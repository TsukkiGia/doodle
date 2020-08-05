package com.example.oneinamillion.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oneinamillion.InviteFollowersActivity;
import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.List;

public class HostViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Event> events;
    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;
    public static final String TAG = "HostViewAdapter";

    public HostViewAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SHOW_MENU) {
            View view = LayoutInflater.from(context).inflate(R.layout.menu2, parent, false);
            return new HostViewAdapter.MenuViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_host_view, parent, false);
            return new HostViewAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HostViewAdapter.ViewHolder) {
            Event event = events.get(position);
            ((ViewHolder) holder).tvEventName.setText(event.getEventName());
            ((ViewHolder) holder).tvAttendees.setText(event.getAttendees().length()+" attendees");
            ((ViewHolder) holder).tvDate.setText(event.getDate());
            Glide.with(context).load(event.getImage().getUrl()).into(((ViewHolder) holder).ivEventImage);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void showMenu(int position) {
        for(int i = 0; i < events.size(); i++){
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
        for(int i = 0; i < events.size(); i++){
            events.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName;
        TextView tvDate;
        TextView tvAttendees;
        RelativeLayout container;
        ImageView ivEventImage;
        ImageView ivInvite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvDate = itemView.findViewById(R.id.tvDateTime);
            tvAttendees = itemView.findViewById(R.id.tvAttendees);
            container = itemView.findViewById(R.id.container);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            ivInvite = itemView.findViewById(R.id.ivInvite);
            ivInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Event event = events.get(getAdapterPosition());
                    Intent i = new Intent(context, InviteFollowersActivity.class);
                    i.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                    context.startActivity(i);
                }
            });
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDelete;

        public MenuViewHolder(View view) {
            super(view);
            ivDelete = view.findViewById(R.id.ivDelete);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
                    Event event = events.get(getAdapterPosition());
                    query.getInBackground(event.getObjectId(), new GetCallback<Event>() {
                        @Override
                        public void done(Event object, ParseException e) {
                            if (e==null) {
                                object.deleteInBackground();
                                events.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                            }
                            else {
                                Log.e(TAG,"Error",e);
                            }
                        }
                    });
                }
            });
        }
    }
}
