package com.example.vcv.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.vcv.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;
    private CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel.context = this.getContext();
        calendarViewModel =
                ViewModelProviders.of(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        Button modify = (Button) root.findViewById(R.id.button_modify);
        compactCalendar = (CompactCalendarView) root.findViewById(R.id.compactcalendar_view);

        //Per il testo del mese e anno sopra al calendario
        final TextView textView = root.findViewById(R.id.TV_Month);
        String month = dateFormatMonth.format(Calendar.getInstance().getTime());
        String monthCapitalize = month.substring(0, 1).toUpperCase() + month.substring(1);
        textView.setText(monthCapitalize);

        calendarViewModel.downloadDataFromFirebase(new Date());

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
                fragmentTransaction.replace(R.id.calendarFragment, modifyCalendarFragment,"modifyCalendarFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        /*calendarViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        return root;
    }
}
