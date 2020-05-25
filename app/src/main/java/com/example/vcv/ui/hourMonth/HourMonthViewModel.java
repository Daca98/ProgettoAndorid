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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
        final Calendar totHoursShouldWork = initLocalCalendars(null, null);
        final Calendar totHoursWorked = initLocalCalendars(null, null);
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

                                    totHoursShouldWork.add(Calendar.HOUR_OF_DAY, Integer.parseInt(order.defaultHourToWork.split(":")[0]));
                                    totHoursShouldWork.add(Calendar.MINUTE, Integer.parseInt(order.defaultHourToWork.split(":")[1]));

                                    Calendar calSupp = initLocalCalendars(Integer.valueOf(order.hourTo.split(":")[0]), Integer.valueOf(order.hourTo.split(":")[1]));
                                    calSupp.add(Calendar.HOUR_OF_DAY, Integer.parseInt(order.hourFrom.split(":")[0]) * -1);
                                    calSupp.add(Calendar.MINUTE, Integer.parseInt(order.hourFrom.split(":")[1]) * -1);
                                    String hours = new SimpleDateFormat("HH").format(calSupp.getTime());
                                    String minutes = new SimpleDateFormat("mm").format(calSupp.getTime());
                                    totHoursWorked.add(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
                                    totHoursWorked.add(Calendar.MINUTE, Integer.parseInt(minutes));

                                    totDays[0]++;

                                    try {
                                        hourMonthFragment.writeHoursInGraphics(getTotHourFromToday(totHoursShouldWork), getTotHourFromToday(totHoursWorked), "", totDays[0]);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            System.out.println(totHoursShouldWork.getTime());
                            System.out.println(totHoursWorked.getTime());
                            System.out.println(totDays[0]);
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

    private Calendar initLocalCalendars(Integer hourOfDay, Integer minute) {
        Calendar cal = Calendar.getInstance();
        // Create instance with final hour work
        cal.set(Calendar.HOUR_OF_DAY, (hourOfDay != null ? hourOfDay : 0));
        cal.set(Calendar.MINUTE, (minute != null ? minute : 0));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    private String getTotHourFromToday(Calendar calendar) throws ParseException {
        String res = "";

        Date firstDate = calendar.getTime();
        Date secondDate = initLocalCalendars(null, null).getTime();

        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

        long hours = diff / 60;
        double minutesDouble = 60 * ((diff / 60.0) - (diff / 60));
        int minutes = Integer.parseInt(new DecimalFormat("#").format(minutesDouble));

        if (hours < 10) {
            res += "0" + hours;
        } else {
            res += hours;
        }

        res += ":";

        if (minutes < 10) {
            res += "0" + minutes;
        } else {
            res += minutes;
        }

        return res;
    }
}
