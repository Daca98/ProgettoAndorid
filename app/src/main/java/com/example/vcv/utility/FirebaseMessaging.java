package com.example.vcv.utility;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.vcv.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;

public class FirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.new_notification), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}
