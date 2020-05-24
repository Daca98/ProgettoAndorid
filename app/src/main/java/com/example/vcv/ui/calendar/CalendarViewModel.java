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
import java.text.ParseException;
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
    public static CalendarFragment calendarFragment;

    public CalendarViewModel() {
        mCalendarOrders = new MutableLiveData<>();
    }

    public LiveData<ArrayList<CalendarOrder>> getCalendar() {
        return mCalendarOrders;
    }

    private User getUserFromLocalDB() {
        db = new QueryDB(context);
        return db.readUser();
    }

    public void downloadDataFromFirebase() {
        User user = getUserFromLocalDB();

        if (user != null && user.badgeNumber != null) {
            FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).limitToLast(14).addChildEventListener(
                    new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            //Toast.makeText(context, "onChildAdded", Toast.LENGTH_SHORT).show();
                            CalendarOrder order = dataSnapshot.getValue(CalendarOrder.class);
                            Date date = new Date(Long.parseLong(dataSnapshot.getKey())*1000L);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            sdf.setTimeZone(TimeZone.getDefault());
                            order.dateCalendarOrder = sdf.format(date);
                            try {
                                calendarFragment.setCalendar(order);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            //Toast.makeText(context, "onChildChanged", Toast.LENGTH_SHORT).show();
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                CalendarOrder order = snap.getValue(CalendarOrder.class);
                                order.dateCalendarOrder = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(snap.getKey());
                                calendarFragment.checkOnLocalData(order);
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
    }

    public void getSingleDay(Date date) {
        User user = getUserFromLocalDB();
        CalendarOrder calendarOrder = db.readCalendarOrderSingleDay(date);

        if (calendarOrder == null) {
            if (user != null && user.badgeNumber != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).child((date.getTime() / 1000) + "");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CalendarOrder order = dataSnapshot.getValue(CalendarOrder.class);
                        if(order != null) {
                            Date date = new Date(Long.parseLong(dataSnapshot.getKey()) * 1000L);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            sdf.setTimeZone(TimeZone.getDefault());
                            order.dateCalendarOrder = sdf.format(date);

                            db.insertSingleCalendarOrderData(order);

                            calendarFragment.setData(order);
                        }
                        calendarFragment.setData(order);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("", databaseError.getMessage());
                    }
                });
            }
        } else {
            calendarFragment.setData(calendarOrder);
        }
    }

    public void setConfirmed(CalendarOrder order){
        //Ciao Dalle
    }
}