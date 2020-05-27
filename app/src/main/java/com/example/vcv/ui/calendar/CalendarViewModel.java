package com.example.vcv.ui.calendar;

import android.content.Context;
import android.util.Log;

import com.example.vcv.utility.CalendarOrder;
import com.example.vcv.utility.QueryDB;
import com.example.vcv.utility.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class CalendarViewModel extends ViewModel {

    private MutableLiveData<ArrayList<CalendarOrder>> mCalendarOrders;
    public static Context context;
    private static QueryDB db;
    public static CalendarFragment calendarFragment;
    public static CalendarOrder currentCalendarOrder;

    /**
     * Empty constructor
     */
    public CalendarViewModel() {

    }

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
     * Method used to download orders from firebase
     */
    public void downloadDataFromFirebase() {
        User user = getUserFromLocalDB();

        if (user != null && user.badgeNumber != null) {
            FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).limitToLast(14).addChildEventListener(
                    new ChildEventListener() {
                        /**
                         * Callback triggered to download the last 14 orders
                         *
                         * @param dataSnapshot
                         * @param s
                         */
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            CalendarOrder order = dataSnapshot.getValue(CalendarOrder.class);
                            Date date = new Date(Long.parseLong(dataSnapshot.getKey()) * 1000L);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            sdf.setTimeZone(TimeZone.getDefault());
                            order.dateCalendarOrder = sdf.format(date);
                            try {
                                calendarFragment.setCalendar(order);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        /**
                         * Callback triggered when one of the 14 downloaded children changed
                         *
                         * @param dataSnapshot
                         * @param s
                         */
                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            CalendarOrder order = dataSnapshot.getValue(CalendarOrder.class);
                            Date date = new Date(Long.parseLong(dataSnapshot.getKey()) * 1000L);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            sdf.setTimeZone(TimeZone.getDefault());
                            order.dateCalendarOrder = sdf.format(date);
                            calendarFragment.checkOnLocalData(order);
                        }

                        /**
                         *
                         * @param dataSnapshot
                         */
                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        }

                        /**
                         *
                         * @param dataSnapshot
                         * @param s
                         */
                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        }

                        /**
                         *
                         * @param databaseError
                         */
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }
    }

    /**
     * Method used to get a specific day from firebase
     *
     * @param date
     */
    public void getSingleDay(Date date) {
        User user = getUserFromLocalDB();
        CalendarOrder calendarOrder = db.readCalendarOrderSingleDay(date);

        if (calendarOrder == null) {
            if (user != null && user.badgeNumber != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).child((date.getTime() / 1000) + "");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    /**
                     * Handle the snapshot of referenced data. This method is triggered only once
                     *
                     * @param dataSnapshot
                     */
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CalendarOrder order = dataSnapshot.getValue(CalendarOrder.class);
                        if (order != null) {
                            Log.e("SINGLE_ORDER_DWNL", "Order getted from firebase with success");
                            Date date = new Date(Long.parseLong(dataSnapshot.getKey()) * 1000L);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            sdf.setTimeZone(TimeZone.getDefault());
                            order.dateCalendarOrder = sdf.format(date);

                            Log.e("SINGLE_ORDER_DWNL", "Order added to local db");
                            db.insertSingleCalendarOrderData(order);
                        }
                        calendarFragment.setData(order);
                    }

                    /**
                     * Handle the error occured while retriving data. This method is triggered only once
                     *
                     * @param databaseError
                     */
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("SINGLE_ORDER_DWNL", databaseError.getMessage());
                    }
                });
            }
        } else {
            calendarFragment.setData(calendarOrder);
        }
    }

    /**
     * Method used to set confirmation of day
     *
     * @param order
     */
    public void setConfirmed(CalendarOrder order) {
        User user = getUserFromLocalDB();

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(order.dateCalendarOrder);

            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 2);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Timestamp ts = new Timestamp(calendar.getTime().getTime());

                String timeStampDay = String.valueOf(ts.getTime() / 1000);

                FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).child(timeStampDay).child("confirmed").setValue(true);
            }
        } catch (Exception e) {
            Log.e("CONFIRM_CALENDAR", "Error while confirming the day " + e.getMessage());
        }
    }

    /**
     * Method used to save changes of a specific day on firebase
     *
     * @param order
     * @throws ParseException
     */
    public void saveChanges(CalendarOrder order) throws ParseException {
        User user = db.readUser();

        if (user != null && user.badgeNumber != null) {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(order.dateCalendarOrder);

            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 2);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Timestamp ts = new Timestamp(calendar.getTime().getTime());

                String timeStampDay = String.valueOf(ts.getTime() / 1000);

                FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).child(timeStampDay).setValue(order);
                Log.i("SAVE_ORDER_CHANGES", "Order's updeted successfully");
            }
        }
    }
}