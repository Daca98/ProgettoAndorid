package com.example.vcv.ui.calendar;

import android.content.Context;
import android.util.Log;

import com.example.vcv.utility.CalendarOrder;
import com.example.vcv.utility.QueryDB;
import com.example.vcv.utility.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

import androidx.annotation.NonNull;
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

    public ArrayList<CalendarOrder>  downloadDataFromFirebase(Date date) throws InterruptedException {
        User user = getUserFromLocalDB();
        final ArrayList<CalendarOrder> calendarOrders = new ArrayList<>();

        // Get timestamp of Monday of current date
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(date);
        calendarStart.set(Calendar.HOUR_OF_DAY, 0);
        calendarStart.set(Calendar.MINUTE, 0);
        calendarStart.set(Calendar.SECOND, 0);
        calendarStart.set(Calendar.MILLISECOND, 0);
        calendarStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Timestamp startTimeStamp = new Timestamp(calendarStart.getTime().getTime());

        // Get timestamp of Sunday of current date
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(calendarStart.getTime());
        calendarEnd.add(Calendar.DATE, 6);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
        calendarEnd.set(Calendar.MINUTE, 0);
        calendarEnd.set(Calendar.SECOND, 0);
        calendarEnd.set(Calendar.MILLISECOND, 0);
        Timestamp endTimeStamp = new Timestamp(calendarEnd.getTime().getTime());

        final Semaphore semaphore = new Semaphore(0);

        if (user != null && user.badgeNumber != null) {
            Query ref = FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).orderByChild("dateCalendarOrder").startAt(startTimeStamp.toString().split("\\.")[0]).endAt(endTimeStamp.toString().split("\\.")[0]);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        CalendarOrder order = snap.getValue(CalendarOrder.class);
                        order.ID = snap.getKey();
                        calendarOrders.add(order);
                        semaphore.release();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("", databaseError.getMessage());
                }
            });
        }
        semaphore.acquire();
        return calendarOrders;
    }
}