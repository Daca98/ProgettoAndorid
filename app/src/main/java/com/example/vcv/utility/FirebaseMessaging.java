package com.example.vcv.utility;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.vcv.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class FirebaseMessaging extends FirebaseMessagingService {
    /**
     * Method used to create instance of FirebaseMessaging
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Method used to handle the receiving of new notifications
     *
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.new_notification), Toast.LENGTH_LONG).show();
                Log.i("NOTIFICATION", "Received new notification from firebase for new orders available");
            }
        });
    }

    /**
     * Method to handle the token
     *
     * @param token
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}
