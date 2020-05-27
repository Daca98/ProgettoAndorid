package com.example.vcv.ui.hourMonth;

import android.content.Context;
import android.util.Log;

import com.example.vcv.utility.CalendarOrder;
import com.example.vcv.utility.QueryDB;
import com.example.vcv.utility.RecapHours;
import com.example.vcv.utility.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class HourMonthViewModel extends ViewModel {
    public static Context context;
    public static HourMonthFragment hourMonthFragment;
    private static QueryDB db;

    /**
     * Method use to get logged user from local db
     *
     * @return logged user
     */
    private User getUserFromLocalDB() {
        db = new QueryDB(context);
        return db.readUser();
    }

    /**
     * Method used to get hours recap from firebase or local db and also calculate it if is the current month and year
     *
     * @param keyRecap
     */
    public void getMonthHoursRecap(String keyRecap) {
        final User user = getUserFromLocalDB();
        final Calendar calStart = initCalStart();
        final Calendar calEnd = initCalEnd();
        final long[] totHoursShouldWork = {0L};
        final long[] totHoursWorked = {0L};
        final int[] totDays = {0};

        Date today = new Date();

        if (user != null && user.badgeNumber != null) {
            if (Integer.parseInt(keyRecap.split("-")[0]) == Integer.parseInt(new SimpleDateFormat("yyyy").format(today)) &&
                    Integer.parseInt(keyRecap.split("-")[1]) == Integer.parseInt(new SimpleDateFormat("MM").format(today))) {
                FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).limitToLast(31).addListenerForSingleValueEvent(new ValueEventListener() {
                    /**
                     * Handle the snapshot of referenced data. This method is triggered only once
                     *
                     * @param dataSnapshot
                     */
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, 1900);
                        Date lastDate = c.getTime();

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            if (snap.getKey() != null) {
                                Date date = new Date(Long.parseLong(snap.getKey()) * 1000L);

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

                        // Save on firebase current extra hours calculation
                        String keyRecap = new SimpleDateFormat("yyyy-MM").format(lastDate);
                        if (totDays[0] > 0) {
                            RecapHours recapHours = new RecapHours(
                                    new SimpleDateFormat("yyyy-MM-dd").format(lastDate),
                                    getHourFromSeconds(totHoursShouldWork[0]),
                                    getHourFromSeconds(totHoursWorked[0]),
                                    getHourFromSeconds(totHoursWorked[0] - totHoursShouldWork[0])
                            );
                            hourMonthFragment.writeHoursInGraphics(recapHours.totHoursShouldWork, recapHours.totHoursWorked, recapHours.totExtra, Integer.parseInt(recapHours.toCalculation.split("-")[2]));
                            FirebaseDatabase.getInstance().getReference().child("recaps").child(user.badgeNumber).child(keyRecap).setValue(recapHours);
                            Log.i("RECAP_MONTH", "Month's recap hours updated on firebase");
                        } else {
                            hourMonthFragment.writeHoursInGraphics("00:00", "00:00", "00:00", 0);
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
            } else {
                if (db == null) {
                    db = new QueryDB(context);
                }

                RecapHours recap = db.readRecap(keyRecap);

                if (recap != null) {
                    hourMonthFragment.writeHoursInGraphics(recap.totHoursShouldWork, recap.totHoursWorked, recap.totExtra, Integer.parseInt(recap.toCalculation.split("-")[2]));
                } else {
                    FirebaseDatabase.getInstance().getReference().child("recaps").child(user.badgeNumber).child(keyRecap).addListenerForSingleValueEvent(new ValueEventListener() {
                        /**
                         * Handle the snapshot of referenced data. This method is triggered only once
                         *
                         * @param dataSnapshot
                         */
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                RecapHours recap = dataSnapshot.getValue(RecapHours.class);

                                if (recap != null) {
                                    hourMonthFragment.writeHoursInGraphics(recap.totHoursShouldWork, recap.totHoursWorked, recap.totExtra, Integer.parseInt(recap.toCalculation.split("-")[2]));
                                    db.insertRecap(recap);
                                    Log.i("RECAP_MONTH", "Recap inserted in local db");
                                } else {
                                    hourMonthFragment.writeHoursInGraphics("00:00", "00:00", "00:00", 0);
                                }
                            } else {
                                hourMonthFragment.writeHoursInGraphics("00:00", "00:00", "00:00", 0);
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
            }
        }
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
