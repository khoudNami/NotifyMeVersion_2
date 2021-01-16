package com.example.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.paging.ContiguousPagedList;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button_notify, button_cancel, button_update;

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private static final String ACTION_UPDATE_NOTIFICATION = "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION";

    public static final String EXTRA = "com.example.android.notifyme.EXTRA";

    private static final int NOTIFICATION_ID = 0;

    NotificationManager mNotifyManager;
    NotificationManagerCompat mNotificationManagerCompat;

    private NotificationReceiver mReceiver = new NotificationReceiver();
    private SecondReceiver mSecondReceiver = new SecondReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_notify = findViewById(R.id.notify);
        button_cancel = findViewById(R.id.cancel);
        button_update = findViewById(R.id.update);

        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        registerReceiver(mSecondReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));

        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotification();
            }
        });

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();
            }
        });

        createNotificationChannel();
        setNotificationButtonState(true, false, false);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();

    }

    void setNotificationButtonState(Boolean isNotifyEnabled, Boolean isUpdateEnabled, Boolean isCancelEnabled) {
        button_notify.setEnabled(isNotifyEnabled);
        button_update.setEnabled(isUpdateEnabled);
        button_cancel.setEnabled(isCancelEnabled);
    }

    private void updateNotification() {
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification updated")
        );
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, false, true);
    }

    private void cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true, false, false);
    }

    public void sendNotification() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, true, true);
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManagerCompat = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "Mascot Notification (Notification channel name)",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.enableLights(true);//
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,// Request code is used to retrieve the same pending intent instance later on (for cancelling, etc).
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID,
                updateIntent,
                PendingIntent.FLAG_ONE_SHOT
        );

        Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra(EXTRA, "Value");
        PendingIntent servicePendingIntent = PendingIntent.getService(
                this,
                0,
                intent,
                0);


        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified")
                .setContentText("This is your notification text")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_android)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent)
                .addAction(R.drawable.ic_update, "Start Service", servicePendingIntent)
                .addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent);
        return notifyBuilder;
    }


    public class NotificationReceiver extends BroadcastReceiver {
        public NotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }

    public class SecondReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Second receiver", Toast.LENGTH_SHORT).show();
        }
    }
}