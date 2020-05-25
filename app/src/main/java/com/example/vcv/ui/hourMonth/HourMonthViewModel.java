package com.example.vcv.ui.hourMonth;

import android.content.Context;
import android.widget.Toast;

import com.example.vcv.utility.CalendarOrder;
import com.example.vcv.utility.QueryDB;
import com.example.vcv.utility.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HourMonthViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    public static Context context;
    public static HourMonthFragment hourMonthFragment;
    private static QueryDB db;

    public HourMonthViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is month fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    private User getUserFromLocalDB() {
        db = new QueryDB(context);
        return db.readUser();
    }

    public void getMonthHoursRecap() {
        User user = getUserFromLocalDB();
        final Calendar calStart = initCalStart();
        final Calendar calEnd = initCalEnd();
        final long[] totHoursShouldWork = {0L};
        final long[] totHoursWorked = {0L};
        final int[] totDays = {0};

        if (user != null && user.badgeNumber != null) {
            FirebaseDatabase.getInstance().getReference().child("orders").child(user.badgeNumber).limitToLast(31).addChildEventListener(
                    new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.getKey() != null) {
                                Date date = new Date(Long.parseLong(dataSnapshot.getKey()) * 1000L);
                                if (calStart.getTime().compareTo(date) < 0 && date.compareTo(calEnd.getTime()) < 0) {
                                    CalendarOrder order = dataSnapshot.getValue(CalendarOrder.class);
                                    totHoursShouldWork[0] += getSeconds(order.defaultHourToWork);
                                    totHoursWorked[0] += (getSeconds(order.hourTo) - getSeconds(order.hourFrom));
                                    totDays[0]++;

                                    hourMonthFragment.writeHoursInGraphics(getHourFromSeconds(totHoursShouldWork[0]), getHourFromSeconds(totHoursWorked[0]), getHourFromSeconds(totHoursWorked[0] - totHoursShouldWork[0]), totDays[0]);
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    private Calendar initCalStart() {
        Calendar calStart = Calendar.getInstance();
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        calStart.set(Calendar.DAY_OF_MONTH, calStart.getActualMinimum(Calendar.DAY_OF_MONTH));

        return calStart;
    }

    private Calendar initCalEnd() {
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        calEnd.set(Calendar.MILLISECOND, 0);
        calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));

        return calEnd;
    }

    private long getSeconds(String hour) {
        long hourSeconds = Integer.parseInt(hour.split(":")[0]) * 3600;
        long minutesSeconds = Integer.parseInt(hour.split(":")[1]) * 60;

        return hourSeconds + minutesSeconds;
    }

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
