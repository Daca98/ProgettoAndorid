package com.example.vcv.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.vcv.R;
import com.example.vcv.utility.QueryDB;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private CompactCalendarView compactCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_calendar, R.id.navigation_myProfile, R.id.navigation_path, R.id.navigation_hourMonth)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        if (Locale.getDefault().getLanguage().equals("it")) {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("all-en");
                            FirebaseMessaging.getInstance().subscribeToTopic(getLocaleTopic()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("FIREBASE_NOTIFICATIONS", "User has been registered with success to '" + getLocaleTopic() + "' topic");
                                }
                            });
                        } else if (Locale.getDefault().getLanguage().equals("en")) {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("all-it");
                            FirebaseMessaging.getInstance().subscribeToTopic(getLocaleTopic()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("FIREBASE_NOTIFICATIONS", "User has been registered with success to '" + getLocaleTopic() + "' topic");
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logoff) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(getLocaleTopic()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("FIREBASE_NOTIFICATIONS", "User has been removed with success from '" + getLocaleTopic() + "' topic");
                }
            });
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            QueryDB db = new QueryDB(MainActivity.this);
            db.cleanLogout();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getLocaleTopic() {
        return "all-" + Locale.getDefault().getLanguage();
    }
}
