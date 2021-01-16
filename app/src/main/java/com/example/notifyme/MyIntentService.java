package com.example.notifyme;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

import static com.example.notifyme.MainActivity.EXTRA;

public class MyIntentService extends IntentService {

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String message = intent.getStringExtra(MainActivity.EXTRA);
            Toast.makeText(this, "my message" + message, Toast.LENGTH_SHORT).show();
        }
    }

}