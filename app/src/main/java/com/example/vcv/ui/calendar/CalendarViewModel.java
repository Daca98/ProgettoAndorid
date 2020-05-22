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

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CalendarViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    public static Context context;
    private static QueryDB db;

    public CalendarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is calendar fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    private User getUserFromLocalDB() {
        db = new QueryDB(context);
        return db.readUser();
    }

    public void downloadDataFromFirebase(Date date) {
        User user = getUserFromLocalDB();

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

        if (user != null && user.badgeNumber != null) {
            // TODO: make specific call for a specific day
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber); //.orderByKey().startAt(startTimeStamp.toString()).endAt(endTimeStamp.toString());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<CalendarOrder> calendarOrders = new ArrayList<>();

                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        calendarOrders.add(snap.getValue(CalendarOrder.class));
                        System.out.println(snap.getValue(CalendarOrder.class)); // TODO: fix crash
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("", databaseError.getMessage());
                }
            });
        }
    }
}