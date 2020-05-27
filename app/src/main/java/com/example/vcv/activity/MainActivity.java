package com.example.vcv.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.vcv.R;
import com.example.vcv.utility.CalendarOrder;
import com.example.vcv.utility.QueryDB;
import com.example.vcv.utility.RecapHours;
import com.example.vcv.utility.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private CompactCalendarView compactCalendar;

    /**
     * Create main activity
     *
     * @param savedInstanceState
     */
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
                    /**
                     * Callback triggered when you can get instance id
                     *
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("NOTIFICATION", "Get instance id failed ", task.getException());
                            return;
                        }

                        if (Locale.getDefault().getLanguage().equals("it")) {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("all-en");
                            FirebaseMessaging.getInstance().subscribeToTopic(getLocaleTopic()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                /**
                                 * Callback triggered when user is subscribed with success to a specific topic for firebase notification
                                 *
                                 * @param aVoid
                                 */
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("FIREBASE_NOTIFICATIONS", "User has been registered with success to '" + getLocaleTopic() + "' topic");
                                }
                            });
                        } else if (Locale.getDefault().getLanguage().equals("en")) {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("all-it");
                            FirebaseMessaging.getInstance().subscribeToTopic(getLocaleTopic()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                /**
                                 * Callback triggered when user is subscribed with success to a specific topic for firebase notification
                                 *
                                 * @param aVoid
                                 */
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("FIREBASE_NOTIFICATIONS", "User has been registered with success to '" + getLocaleTopic() + "' topic");
                                }
                            });
                        }
                    }
                });

        getMonthHoursRecap();
    }

    /**
     * Method to inflate menu (logout)
     *
     * @param menu
     * @return boolean depends on inflate operation
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Method to handle click of top menu (logout)
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logoff) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(getLocaleTopic()).addOnSuccessListener(new OnSuccessListener<Void>() {
                /**
                 * Callback triggered when user is removed with success from firebase notification topic
                 *
                 * @param aVoid
                 */
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("FIREBASE_NOTIFICATIONS", "User has been removed with success from '" + getLocaleTopic() + "' topic");
                }
            });
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            QueryDB db = new QueryDB(MainActivity.this);
            db.cleanLogout();
            Log.i("LOGOUT", "User removed from firebase and local db");
            try {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

                File f = new File(directory, "profile.jpg");
                f.delete();
                Log.i("LOGOUT", "User's profile picture removed");
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method use to get name of topic where register the user for firebase notification
     *
     * @return the name of firebase's topic to subscribe user. It depends on the device language to get the notification in the right language
     */
    private String getLocaleTopic() {
        return "all-" + Locale.getDefault().getLanguage();
    }

    /**
     * Method used to calculate recap of month's hours including extraordinary hours. It works on current month and, if necessary, on the previous one
     */
    private void getMonthHoursRecap() {
        final User user = getUserFromLocalDB();

        final Calendar prevCalStart = prevInitCalStart();
        final Calendar prevCalEnd = prevInitCalEnd();
        final Calendar calStart = initCalStart();
        final Calendar calEnd = initCalEnd();

        final long[] prevTotHoursShouldWork = {0L};
        final long[] prevTotHoursWorked = {0L};
        final int[] prevTotDays = {0};
        final long[] totHoursShouldWork = {0L};
        final long[] totHoursWorked = {0L};
        final int[] totDays = {0};

        if (user != null && user.badgeNumber != null) {
            final int[] numDaysToLoad = {62};

            FirebaseDatabase.getInstance().getReference().child("recaps").child(user.badgeNumber).child(new SimpleDateFormat("yyyy-MM").format(prevCalStart.getTime())).addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle the snapshot of referenced data. This method is triggered only once
                 *
                 * @param dataSnapshot
                 */
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    RecapHours recapHoursPrevMonth = dataSnapshot.getValue(RecapHours.class);

                    if (recapHoursPrevMonth != null && Integer.parseInt(recapHoursPrevMonth.toCalculation.split("-")[2]) == prevCalStart.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        Log.i("RECAP_MONTH", "Last month recap hours is already complete");
                        numDaysToLoad[0] = 31;
                    }

                    FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).limitToLast(numDaysToLoad[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                        /**
                         * Handle the snapshot of referenced data. This method is triggered only once
                         *
                         * @param dataSnapshot
                         */
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.YEAR, 1900);
                            Date prevLastDate = c.getTime();
                            Date lastDate = c.getTime();

                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                if (snap.getKey() != null) {
                                    Date date = new Date(Long.parseLong(snap.getKey()) * 1000L);

                                    // Load previous month hours
                                    if (prevCalStart.getTime().compareTo(date) < 0 && date.compareTo(prevCalEnd.getTime()) < 0) {
                                        CalendarOrder order = snap.getValue(CalendarOrder.class);
                                        prevTotHoursShouldWork[0] += getSeconds(order.defaultHourToWork);
                                        prevTotHoursWorked[0] += (getSeconds(order.realHourTo) - getSeconds(order.realHourFrom));
                                        prevTotDays[0]++;

                                        if (date.compareTo(prevLastDate) > 0) {
                                            prevLastDate = date;
                                        }
                                    }

                                    // Load current month hours
                                    if (calStart.getTime().compareTo(date) < 0 && date.compareTo(calEnd.getTime()) < 0) {
                                        CalendarOrder order = snap.getValue(CalendarOrder.class);
                                        totHoursShouldWork[0] += getSeconds(order.defaultHourToWork);
                                        totHoursWorked[0] += (getSeconds(order.realHourTo) - getSeconds(order.realHourFrom));
                                        totDays[0]++;

                                        if (date.compareTo(lastDate) > 0) {
                                            lastDate = date;
                                        }
                                    }
                                }
                            }

                            // Save on firebase current extra hours calculation prev month if necessary
                            if (numDaysToLoad[0] == 62) {
                                String prevKeyRecap = new SimpleDateFormat("yyyy-MM").format(prevLastDate);
                                if (prevTotDays[0] > 0) {
                                    RecapHours recapHours = new RecapHours(
                                            new SimpleDateFormat("yyyy-MM-dd").format(prevLastDate),
                                            getHourFromSeconds(prevTotHoursShouldWork[0]),
                                            getHourFromSeconds(prevTotHoursWorked[0]),
                                            getHourFromSeconds(prevTotHoursWorked[0] - prevTotHoursShouldWork[0])
                                    );
                                    FirebaseDatabase.getInstance().getReference().child("recaps").child(user.badgeNumber).child(prevKeyRecap).setValue(recapHours);
                                    Log.i("RECAP_MONTH", "Previous month recap hours updated on firebase");
                                }
                            }

                            // Save on firebase current extra hours calculation
                            String keyRecap = new SimpleDateFormat("yyyy-MM").format(lastDate);
                            if (totDays[0] > 0) {
                                RecapHours recapHours = new RecapHours(
                                        new SimpleDateFormat("yyyy-MM-dd").format(lastDate),
                                        getHourFromSeconds(totHoursShouldWork[0]),
                                        getHourFromSeconds(totHoursWorked[0]),
                                        getHourFromSeconds(totHoursWorked[0] - totHoursShouldWork[0])
                                );
                                FirebaseDatabase.getInstance().getReference().child("recaps").child(user.badgeNumber).child(keyRecap).setValue(recapHours);
                                Log.i("RECAP_MONTH", "Current month recap hours updated on firebase");
                            }
                        }

                        /**
                         * Handle the error occured while retriving data. This method is triggered only once
                         *
                         * @param databaseError
                         */
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("RECAP_MONTH", databaseError.getMessage());
                        }
                    });
                }

                /**
                 * Handle the error occured while retriving data. This method is triggered only once
                 *
                 * @param databaseError
                 */
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("RECAP_MONTH", databaseError.getMessage());
                }
            });
        }
    }

    /**
     * Method use to get logged user from local db
     *
     * @return logged user
     */
    private User getUserFromLocalDB() {
        QueryDB db = new QueryDB(this.getApplicationContext());
        return db.readUser();
    }

    /**
     * Method used to initialize calendar object to first day of previous month
     *
     * @return calendar object initialize to the first day of previous month
     */
    private Calendar prevInitCalStart() {
        Calendar calStart = Calendar.getInstance();
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        calStart.add(Calendar.MONTH, -1);
        calStart.set(Calendar.DAY_OF_MONTH, calStart.getActualMinimum(Calendar.DAY_OF_MONTH));

        return calStart;
    }

    /**
     * Method used to initialize calendar object to last day of previous month
     *
     * @return calendar object initialize to the last day of previous month
     */
    private Calendar prevInitCalEnd() {
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        calEnd.set(Calendar.MILLISECOND, 0);
        calEnd.add(Calendar.MONTH, -1);
        calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));

        return calEnd;
    }

    /**
     * Method used to initialize calendar object to first day of current month
     *
     * @return calendar object initialize to the first day of current month
     */
    private Calendar initCalStart() {
        Calendar calStart = Calendar.getInstance();
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        calStart.set(Calendar.DAY_OF_MONTH, calStart.getActualMinimum(Calendar.DAY_OF_MONTH));

        return calStart;
    }

    /**
     * Method used to initialize calendar object to last day of current month
     *
     * @return calendar object initialize to the last day of current month
     */
    private Calendar initCalEnd() {
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        calEnd.set(Calendar.MILLISECOND, 0);
        calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));

        return calEnd;
    }

    /**
     * Method used to convert string to long that rappresent hours and minutes in seconds
     *
     * @param hour
     * @return long that rappresent hours and minutes in seconds
     */
    private long getSeconds(String hour) {
        long hourSeconds = Integer.parseInt(hour.split(":")[0]) * 3600;
        long minutesSeconds = Integer.parseInt(hour.split(":")[1]) * 60;

        return hourSeconds + minutesSeconds;
    }

    /**
     * Method used to convert long into String that rappresent hour and minutes from seconds
     *
     * @param seconds
     * @return String that rappresent hour and minutes from seconds
     */
    private String getHourFromSeconds(long seconds) {
        String res = "";

        if (seconds > 0) {
            if ((seconds / 3600.0) < 1.0) {
                String minutes = Double.toString(seconds / 60.0);

                res += "00:" + String.format("%02d", Integer.parseInt(minutes.split("\\.")[0]));
            } else {
                String hourString = Double.toString(seconds / 3600.0);
                int hourInt = Integer.parseInt(hourString.split("\\.")[0]);
                res += String.format("%02d", hourInt);

                res += ":";

                String minutes = Double.toString((seconds - (3600 * hourInt)) / 60.0);
                res += String.format("%02d", Integer.parseInt(minutes.split("\\.")[0]));
            }
        } else {
            res = "00:00";
        }

        return res;
    }
}
