package com.example.vcv.ui.hourMonth;

import android.os.Bundle;
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

public class HourMonthFragment extends Fragment {

    private HourMonthViewModel hourMonthViewModel;
    public TextView hoursShouldWork, hoursWorked, hoursExtra, attention;
    Spinner monthChoices, yearsChoices;
    final int[] monthSelected = new int[]{-1};
    final int[] yearSelected = new int[]{-1};

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HourMonthViewModel.context = this.getContext();
        HourMonthViewModel.hourMonthFragment = this;

        hourMonthViewModel = ViewModelProviders.of(this).get(HourMonthViewModel.class);
        View root = inflater.inflate(R.layout.fragment_hourmonth, container, false);

        initGraphics(root);

        Date date = new Date();
        hourMonthViewModel.getMonthHoursRecap(Integer.parseInt(new SimpleDateFormat("MM").format(date)), Integer.parseInt(new SimpleDateFormat("yyyy").format(date)));

        return root;
    }

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
        monthSelected[0] = currentMonthSpinner;
        monthChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (monthSelected[0] > -1 && yearSelected[0] > -1) {
                        Date date = new SimpleDateFormat("MMMM", Locale.getDefault()).parse(adapterView.getItemAtPosition(i).toString());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        monthSelected[0] = cal.get(Calendar.MONTH);
                        hourMonthViewModel.getMonthHoursRecap(monthSelected[0], yearSelected[0]);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

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
        int currentYearSpinner = adapterYears.getPosition(new SimpleDateFormat("yyyy").format(today));
        yearsChoices.setSelection(currentYearSpinner);
        yearSelected[0] = currentYearSpinner;
        yearsChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (monthSelected[0] > -1 && yearSelected[0] > -1) {
                    yearSelected[0] = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
                    hourMonthViewModel.getMonthHoursRecap(monthSelected[0], yearSelected[0]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public final void writeHoursInGraphics(String totHoursShouldWork, String totHoursWorked, String totExtraHours, int daysCalculatedOn) {
        attention.setText(getString(R.string.from_label_recap) + " " + getFirstDayOfMonth() + " " + getString(R.string.to_label_recap) + " " + getFinalDayRecap(daysCalculatedOn));
        hoursShouldWork.setText(totHoursShouldWork);
        hoursWorked.setText(totHoursWorked);
        hoursExtra.setText(totExtraHours);
    }

    private String getFirstDayOfMonth() {
        return "01/" + String.format("%02d", (monthSelected[0] + 1)) + "/" + String.format("%02d", yearSelected[0]);
    }

    private String getFinalDayRecap(int daysCalculatedOn) {
        int day = daysCalculatedOn > 0 ? daysCalculatedOn : 1;
        return day + "/" + String.format("%02d", (monthSelected[0] + 1)) + "/" + String.format("%02d", yearSelected[0]);
    }
}
