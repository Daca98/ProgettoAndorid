package com.example.vcv.ui.calendar;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Semaphore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CalendarViewModel extends ViewModel {

    private MutableLiveData<ArrayList<CalendarOrder>> mCalendarOrders;
    public static Context context;
    private static QueryDB db;

    public CalendarViewModel() {
        mCalendarOrders = new MutableLiveData<>();
    }

    public void changeData(Date date) throws InterruptedException {
        mCalendarOrders.setValue(downloadDataFromFirebase(date));
    }

    public LiveData<ArrayList<CalendarOrder>> getCalendar() {
        return mCalendarOrders;
    }

    private User getUserFromLocalDB() {
        db = new QueryDB(context);
        return db.readUser();
    }

    public ArrayList<CalendarOrder> downloadDataFromFirebase(Date date) {
        User user = getUserFromLocalDB();
        final ArrayList<CalendarOrder> calendarOrders = new ArrayList<>();

        if (user != null && user.badgeNumber != null) {
            FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).limitToLast(14).addChildEventListener(
                    new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Toast.makeText(context, "onChildAdded", Toast.LENGTH_SHORT).show();
                            CalendarOrder order = dataSnapshot.getValue(CalendarOrder.class);
                            Date date = new Date(Long.parseLong(dataSnapshot.getKey())*1000L);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            sdf.setTimeZone(TimeZone.getDefault());
                            order.dateCalendarOrder = sdf.format(date);
                            calendarOrders.add(order);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Toast.makeText(context, "onChildChanged", Toast.LENGTH_SHORT).show();
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                CalendarOrder order = snap.getValue(CalendarOrder.class);
                                order.dateCalendarOrder = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(snap.getKey());
                                calendarOrders.add(order);
                            }
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            Toast.makeText(context, "onChildRemoved", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Toast.makeText(context, "onChildMoved", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(context, "onCancelled", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        return calendarOrders;
    }

    public void getOldDay(Date date, final TextView hourStart, final TextView hourEnd, final TextView job) {
        User user = getUserFromLocalDB();
        CalendarOrder calendarOrder = db.readCalendarOrderSingleDay(date);

        if (calendarOrder == null) {
            if (user != null && user.badgeNumber != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).child((date.getTime() / 1000) + "");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CalendarOrder order = dataSnapshot.getValue(CalendarOrder.class);

                        Date date = new Date(Long.parseLong(dataSnapshot.getKey())*1000L);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        sdf.setTimeZone(TimeZone.getDefault());
                        order.dateCalendarOrder = sdf.format(date);

                        db.insertSingleCalendarOrderData(order);

                        hourStart.setText(order.hourFrom);
                        hourEnd.setText(order.hourTo);
                        job.setText(order.job);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("", databaseError.getMessage());
                    }
                });
            }
        } else {
            hourStart.setText(calendarOrder.hourFrom);
            hourEnd.setText(calendarOrder.hourTo);
            job.setText(calendarOrder.job);
        }
    }
}