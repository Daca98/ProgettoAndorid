package com.example.vcv.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.vcv.R;
import com.example.vcv.utility.CalendarOrder;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;
    private CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private ArrayList<CalendarOrder> calendarOrders = new ArrayList<>();
    Button confirm;
    TextView hourStart;
    TextView hourEnd;
    TextView job;
    private Date today = new Date();
    private CalendarOrder currentOrder = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel.context = this.getContext();
        CalendarViewModel.calendarFragment = this;
        calendarViewModel =
                ViewModelProviders.of(this).get(CalendarViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        hourStart = root.findViewById(R.id.HourStart);
        hourEnd = root.findViewById(R.id.HourEnd);
        job = root.findViewById(R.id.job);
        final Button modify = (Button) root.findViewById(R.id.button_modify);
        compactCalendar = (CompactCalendarView) root.findViewById(R.id.compactcalendar_view);
        confirm = (Button) root.findViewById(R.id.button_confirm);

        //Set the month on top of the calendar
        final TextView textView = root.findViewById(R.id.TV_Month);
        String month = dateFormatMonth.format(Calendar.getInstance().getTime());
        String monthCapitalize = month.substring(0, 1).toUpperCase() + month.substring(1);
        textView.setText(monthCapitalize);
        setData(null);

        calendarViewModel.downloadDataFromFirebase();

        //listener for calendar
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            //when click one day
            @Override
            public void onDayClick(Date dateClicked) {
                getInfoCurrentDay(dateClicked);
            }

            //when scroll on months
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                //Set the month on top of the calendar
                String month = dateFormatMonth.format(firstDayOfNewMonth);
                String monthCapitalize = month.substring(0, 1).toUpperCase() + month.substring(1);
                textView.setText(monthCapitalize);

                getInfoCurrentDay(firstDayOfNewMonth);
            }
        });

        //Listener to confirm have seen order
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // save data on firebase and local db
                calendarViewModel.setConfirmed(currentOrder);
                // Change confirm value in local arraylist of orders
                for (CalendarOrder calendarOrder : calendarOrders) {
                    if (calendarOrder.dateCalendarOrder.equals(currentOrder.dateCalendarOrder)) {
                        calendarOrder.confirmed = true;
                    }
                }
                // Remove event from calendar
                try {
                    Date dateOrder = new SimpleDateFormat("yyyy-MM-dd").parse(currentOrder.dateCalendarOrder);
                    Event ev = new Event(R.color.colorWhite, dateOrder.getTime());
                    compactCalendar.removeEvent(ev);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        //Listener to open modify calendar
        modify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ModifyCalendarFragment modifyCalendarFragment = new ModifyCalendarFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.calendarFragment, modifyCalendarFragment, "modifyCalendarFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return root;
    }

    public void setCalendar(CalendarOrder order) throws ParseException {
        calendarOrders.add(order);
        Event ev;
        //add event for new days
        if(!order.confirmed){
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(order.dateCalendarOrder);
                ev = new Event(R.color.colorWhite, date.getTime());
                compactCalendar.addEvent(ev);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(order.dateCalendarOrder.equals(convertData(new Date()))){
            setData(order);
        }
    }

    public void setData(CalendarOrder order){
        if(order != null){
            currentOrder = order;
            if(!calendarOrders.contains(order)){
                calendarOrders.add(order);
            }
            hourStart.setText(order.hourFrom);
            hourEnd.setText(order.hourTo);
            job.setText(order.job);
            if(!order.confirmed){
                confirm.setFocusable(true);
            }
            else{
                confirm.setFocusable(false);
            }
        } else{
            hourStart.setText(" - ");
            hourEnd.setText(" - ");
            job.setText(R.string.no_information);
        }
    }

    private String convertData(Date date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private void getInfoCurrentDay(Date dateClicked){
        confirm.setFocusable(true);
        Boolean useRemoteDate = true;

        for (CalendarOrder order :
                calendarOrders) {
            try {
                if(order.dateCalendarOrder.equals(convertData(dateClicked))){
                    useRemoteDate = false;
                    setData(order);
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(useRemoteDate) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateClicked);
            calendar.set(Calendar.HOUR_OF_DAY, 2);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendarViewModel.getSingleDay(calendar.getTime());
        }
    }

    public void checkOnLocalData(CalendarOrder order){
        for (CalendarOrder localeOrder:
             calendarOrders) {
            if (localeOrder.dateCalendarOrder.equals(order.dateCalendarOrder)) {
                int index = calendarOrders.indexOf(localeOrder);
                calendarOrders.remove(index);
                calendarOrders.add(index, order);
                if(!order.confirmed){
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = dateFormat.parse(order.dateCalendarOrder);
                        Event ev = new Event(R.color.colorWhite, date.getTime());
                        compactCalendar.addEvent(ev);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
