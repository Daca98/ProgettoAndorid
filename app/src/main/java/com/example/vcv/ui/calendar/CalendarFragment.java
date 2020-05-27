package com.example.vcv.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vcv.R;
import com.example.vcv.utility.CalendarOrder;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;


public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;
    private CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private ArrayList<CalendarOrder> calendarOrders = new ArrayList<>();
    Button confirm;
    Button modify;
    TextView hourStart;
    TextView hourEnd;
    TextView job;
    public CalendarOrder currentOrder = null;
    public Boolean useRemoteDate = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel.context = this.getContext();
        CalendarViewModel.calendarFragment = this;
        calendarViewModel =
                ViewModelProviders.of(this).get(CalendarViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        hourStart = root.findViewById(R.id.HourStart);
        hourEnd = root.findViewById(R.id.HourEnd);
        job = root.findViewById(R.id.job);
        modify = (Button) root.findViewById(R.id.button_modify);
        compactCalendar = (CompactCalendarView) root.findViewById(R.id.compactcalendar_view);
        confirm = (Button) root.findViewById(R.id.button_confirm);

        //Set the month on top of the calendar
        final TextView textView = root.findViewById(R.id.TV_Month);
        String month = dateFormatMonth.format(Calendar.getInstance().getTime());
        String monthCapitalize = month.substring(0, 1).toUpperCase() + month.substring(1);
        textView.setText(monthCapitalize);
        setData(null);
        setSaveAndChangeStatus(false, true);

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
                // save data on firebase
                calendarViewModel.setConfirmed(currentOrder);
                // Remove event from calendar
                try {
                    Date dateOrder = new SimpleDateFormat("yyyy-MM-dd").parse(currentOrder.dateCalendarOrder);
                    Event ev = new Event(R.color.colorWhite, dateOrder.getTime());
                    compactCalendar.removeEvent(ev);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), getString(R.string.day_confirmed), Toast.LENGTH_SHORT).show();
                setSaveAndChangeStatus(false, false);
            }
        });

        //Listener to open modify calendar
        modify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ModifyCalendarFragment modifyCalendarFragment = new ModifyCalendarFragment(CalendarFragment.this);
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
        if (!order.confirmed) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(order.dateCalendarOrder);
                ev = new Event(R.color.colorWhite, date.getTime());
                compactCalendar.addEvent(ev);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (order.dateCalendarOrder.equals(convertData(new Date()))) {
            setData(order);
        }
    }

    public void setData(CalendarOrder order) {
        if (order != null) {
            currentOrder = order;
            if (!calendarOrders.contains(order)) {
                calendarOrders.add(order);
            }
            hourStart.setText(order.hourFrom);
            hourEnd.setText(order.hourTo);
            job.setText(order.job);
            if (!order.confirmed) {
                setSaveAndChangeStatus(true, false);
            } else {
                setSaveAndChangeStatus(false, false);
            }
        } else {
            hourStart.setText(" - ");
            hourEnd.setText(" - ");
            job.setText(R.string.no_information);
            currentOrder = null;
            setSaveAndChangeStatus(false, true);
        }
    }

    private String convertData(Date date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private void getInfoCurrentDay(Date dateClicked) {
        confirm.setEnabled(true);
        useRemoteDate = true;

        for (CalendarOrder order :
                calendarOrders) {
            try {
                if (order.dateCalendarOrder.equals(convertData(dateClicked))) {
                    useRemoteDate = false;
                    setData(order);
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (useRemoteDate) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateClicked);
            calendar.set(Calendar.HOUR_OF_DAY, 2);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendarViewModel.getSingleDay(calendar.getTime());
        }
    }

    public void checkOnLocalData(CalendarOrder order) {
        int index = -1;
        int i = 0;

        for (CalendarOrder localeOrder : calendarOrders) {
            if (localeOrder.dateCalendarOrder.equals(order.dateCalendarOrder)) {
                index = i;
                break;
            }
            i++;
        }

        if (index > -1) {
            calendarOrders.remove(index);
            calendarOrders.add(index, order);
            if (!order.confirmed) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dateFormat.parse(order.dateCalendarOrder);
                    Event ev = new Event(R.color.colorWhite, date.getTime());
                    compactCalendar.removeEvent(ev);
                    compactCalendar.addEvent(ev);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (currentOrder.dateCalendarOrder.equals(order.dateCalendarOrder)) {
                hourStart.setText(order.hourFrom);
                hourEnd.setText(order.hourTo);
                job.setText(order.job);
            }
        }
    }

    // areEnabled = define if confirm button has to be enabled or disabled and if modify button has to show text "modify" or "detail"
    // forceDisableModify = define if also modify button has to be disabled
    private void setSaveAndChangeStatus(boolean areEnabled, boolean forceDisableModify) {
        confirm.setEnabled(areEnabled);
        if (areEnabled) {
            confirm.setBackgroundResource(R.drawable.button_confirm);
        } else {
            confirm.setBackgroundResource(R.drawable.button_disabled);
        }
        if (!forceDisableModify) {
            modify.setEnabled(true);
            modify.setBackgroundResource(R.drawable.button_confirm);
            modify.setText(areEnabled ? getString(R.string.modify) : getString(R.string.detail));
        } else {
            modify.setEnabled(false);
            modify.setBackgroundResource(R.drawable.button_disabled);
        }
    }
}
