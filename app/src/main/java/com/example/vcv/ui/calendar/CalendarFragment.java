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
    TextView hourStart;
    TextView hourEnd;
    TextView job;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel.context = this.getContext();
        calendarViewModel =
                ViewModelProviders.of(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        hourStart = root.findViewById(R.id.HourStart);
        hourEnd = root.findViewById(R.id.HourEnd);
        job = root.findViewById(R.id.job);

        try {
            calendarViewModel.changeData(new Date());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Button modify = (Button) root.findViewById(R.id.button_modify);
        compactCalendar = (CompactCalendarView) root.findViewById(R.id.compactcalendar_view);

        //Per il testo del mese e anno sopra al calendario
        final TextView textView = root.findViewById(R.id.TV_Month);
        String month = dateFormatMonth.format(Calendar.getInstance().getTime());
        String monthCapitalize = month.substring(0, 1).toUpperCase() + month.substring(1);
        textView.setText(monthCapitalize);

        //evento test
        try {

            String dateString = "24/04/2020";
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Date date = dateFormat.parse(dateString);

            Event ev1 = new Event(R.color.colorWhite, date.getTime());
            compactCalendar.addEvent(ev1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //listener per il calendario
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            //quando premo su un giorno
            @Override
            public void onDayClick(Date dateClicked) {
                /*if(dateClicked.toString().compareTo("data")) {
                    //modifica campi
                }*/
                Calendar calendarSubDays = Calendar.getInstance();
                calendarSubDays.setTime(new Date());
                calendarSubDays.add(Calendar.DATE, -6);

                if (dateClicked.compareTo(calendarSubDays.getTime()) >= 0) {
                    // TODO: PRENDERE DA FIREBASE I DATI DEGLI ULTIMI 14 GIORNI
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateClicked);
                    calendar.set(Calendar.HOUR_OF_DAY, 2);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendarViewModel.getOldDay(calendar.getTime(), hourStart, hourEnd, job);
                }
            }

            //quando scorro tra i mesi
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                String month = dateFormatMonth.format(firstDayOfNewMonth);
                String monthCapitalize = month.substring(0, 1).toUpperCase() + month.substring(1);
                textView.setText(monthCapitalize);
            }
        });

        //Listener per passare alla modifica delle ore
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

        calendarViewModel.getCalendar().observe(getViewLifecycleOwner(), new Observer<ArrayList<CalendarOrder>>() {
            @Override
            public void onChanged(@Nullable ArrayList<CalendarOrder> s) {
                calendarOrders.addAll(s);
            }
        });

        return root;
    }
}
