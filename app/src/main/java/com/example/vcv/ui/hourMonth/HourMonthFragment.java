package com.example.vcv.ui.hourMonth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.vcv.R;
import com.example.vcv.ui.calendar.CalendarViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class HourMonthFragment extends Fragment {

    private HourMonthViewModel hourMonthViewModel;
    public TextView hoursShouldWork, hoursWorked, hoursExtra, attention;
    Spinner monthChoices, yearsChoices;
    int monthSelected = -1;
    int yearSelected = -1;
    boolean firstLoadMonth = true;
    boolean firstLoadYear = true;

    /**
     * Method used to create the fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View to inflate in the graphics
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HourMonthViewModel.context = this.getContext();
        HourMonthViewModel.hourMonthFragment = this;

        hourMonthViewModel = ViewModelProviders.of(this).get(HourMonthViewModel.class);
        View root = inflater.inflate(R.layout.fragment_hourmonth, container, false);

        initGraphics(root);

        Date date = new Date();
        hourMonthViewModel.getMonthHoursRecap(new SimpleDateFormat("yyyy").format(date) + "-" + new SimpleDateFormat("MM").format(date));

        return root;
    }

    /**
     * Method used to initialize graphic components
     *
     * @param root
     */
    private void initGraphics(View root) {
        attention = root.findViewById(R.id.desc_hour_recap);
        hoursShouldWork = root.findViewById(R.id.tw_tot_hours_should_work);
        hoursWorked = root.findViewById(R.id.tw_tot_hours_worked);
        hoursExtra = root.findViewById(R.id.tw_tot_extra_hours_worked);

        Date today = new Date();
        int currentMonthSpinner = Integer.parseInt(new SimpleDateFormat("MM").format(today)) - 1;
        ArrayAdapter<CharSequence> adapterMonths = ArrayAdapter.createFromResource(getContext(), R.array.months_array, android.R.layout.simple_spinner_dropdown_item);

        monthChoices = root.findViewById(R.id.choose_month);
        monthChoices.setAdapter(adapterMonths);
        monthChoices.setSelection(currentMonthSpinner);
        monthSelected = currentMonthSpinner;
        monthChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Define operation to perform when an item is clicked
             *
             * @param adapterView
             * @param view
             * @param i
             * @param l
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (!firstLoadMonth) {
                        Date date = new SimpleDateFormat("MMMM", Locale.getDefault()).parse(adapterView.getItemAtPosition(i).toString());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        monthSelected = cal.get(Calendar.MONTH);
                        hourMonthViewModel.getMonthHoursRecap(yearSelected + "-" + String.format("%02d", (monthSelected + 1)));
                    }
                    firstLoadMonth = false;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            /**
             *
             * @param adapterView
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        String[] years = new String[101];
        for (int i = 2000, j = 0; j < 101; i++, j++) {
            years[j] = Integer.toString(i);
        }
        ArrayAdapter<String> adapterYears = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, years);

        yearsChoices = root.findViewById(R.id.choose_year);
        yearsChoices.setAdapter(adapterYears);
        String currentYear = new SimpleDateFormat("yyyy").format(today);
        int currentYearSpinner = adapterYears.getPosition(currentYear);
        yearsChoices.setSelection(currentYearSpinner);
        yearSelected = Integer.parseInt(currentYear);
        yearsChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Define operation to perform when an item is clicked
             *
             * @param adapterView
             * @param view
             * @param i
             * @param l
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!firstLoadYear) {
                    yearSelected = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
                    hourMonthViewModel.getMonthHoursRecap(yearSelected + "-" + String.format("%02d", (monthSelected + 1)));
                }
                firstLoadYear = false;
            }

            /**
             *
             * @param adapterView
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * Method used to write the result in graphic
     *
     * @param totHoursShouldWork
     * @param totHoursWorked
     * @param totExtraHours
     * @param daysCalculatedOn
     */
    public final void writeHoursInGraphics(String totHoursShouldWork, String totHoursWorked, String totExtraHours, int daysCalculatedOn) {
        attention.setText(getString(R.string.from_label_recap) + " " + getFirstDayOfMonth() + " " + getString(R.string.to_label_recap) + " " + getFinalDayRecap(daysCalculatedOn));
        hoursShouldWork.setText(totHoursShouldWork);
        hoursWorked.setText(totHoursWorked);
        hoursExtra.setText(totExtraHours);
    }

    /**
     * Method use to get the date of the first day of month
     *
     * @return string that rappresent date of the first day of month
     */
    private String getFirstDayOfMonth() {
        return "01/" + String.format("%02d", (monthSelected + 1)) + "/" + String.format("%02d", yearSelected);
    }

    /**
     * Method use to get the date of the last day of month
     *
     * @return string that rappresent date of the last day of month
     */
    private String getFinalDayRecap(int daysCalculatedOn) {
        int day = daysCalculatedOn > 0 ? daysCalculatedOn : 1;
        return day + "/" + String.format("%02d", (monthSelected + 1)) + "/" + String.format("%02d", yearSelected);
    }
}
