package com.example.oneinamillion.Models;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.oneinamillion.R;

public class ReminderBroadcast extends BroadcastReceiver {
    Event event;

    public ReminderBroadcast(Event event) {
        this.event = event;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "20")
                .setSmallIcon(R.drawable.red_marker)
                .setContentTitle("Event Notification")
                .setContentText(event.getEventName()+" is coming up within an hour!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}
