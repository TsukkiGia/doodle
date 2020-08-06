package com.example.oneinamillion.Models;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.oneinamillion.R;
import com.parse.ParseUser;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "20")
                .setSmallIcon(R.drawable.red_marker)
                .setContentTitle("Event Notification")
                .setContentText("You have an event coming up")
                .setNumber((Integer)ParseUser.getCurrentUser().getNumber("ActiveEvents"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}
