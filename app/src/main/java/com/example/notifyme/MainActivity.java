package com.example.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button button_notify;

    //     Every notification channel must be associated with an ID that is unique within your package.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    //    associate the notification with a notification ID so that your code can update or cancel the notification in the future
    private static final int NOTIFICATION_ID = 0;

    //    The Android system uses the NotificationManager class to deliver notifications to the user.
    NotificationManager mNotifyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_notify = findViewById(R.id.notify);
        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        createNotificationChannel();
    }

    public void sendNotification() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
//        mNotifyManager.notify(NOTIFICATION_ID, getMyNotificationBuilder().build());
    }

    //    On Android-powered devices running Android 8.0 (API level 26) or higher, notification channels
//    that you create in your app appear as Categories under App notifications in the device Settings app.
    public void createNotificationChannel() {

        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//        notification channels are only available in API 26 and higher. So create the notification channel if this is true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel //Each notification channel represents a type of notification
                    = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "Mascot Notification (Notification channel name)",
                    NotificationManager.IMPORTANCE_HIGH
            );

//            set the initial behavior for the channel, and the behavior is applied to all the notifications in
//            the channel. Whatever behavior your app sets for a notification channel, the user can
//            change that behavior later, and the user can turn off your app's notifications altogether
            notificationChannel.enableLights(true);//
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent allows the Android notification system to perform the assigned action on
//        behalf of your code. Retrieve a PendingIntent that will start a new activity
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,// Request code is used to retrieve the same pending intent instance later on (for cancelling, etc).
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified")
                .setContentText("This is your notification text")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_android)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent);
        return notifyBuilder;
    }
}