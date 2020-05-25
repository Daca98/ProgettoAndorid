package com.example.vcv.ui.hourMonth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.vcv.R;
import com.example.vcv.ui.calendar.CalendarViewModel;

import java.util.Date;

public class HourMonthFragment extends Fragment {

    private HourMonthViewModel hourMonthViewModel;
    public TextView hoursShouldWork, hoursWorked, hoursExtra;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HourMonthViewModel.context = this.getContext();
        HourMonthViewModel.hourMonthFragment = this;

        hourMonthViewModel = ViewModelProviders.of(this).get(HourMonthViewModel.class);
        View root = inflater.inflate(R.layout.fragment_hourmonth, container, false);

        initGraphics(root);

        hourMonthViewModel.getMonthHoursRecap();

        return root;
    }

    private void initGraphics(View root) {
        hoursShouldWork = root.findViewById(R.id.tw_tot_hours_should_work);
        hoursWorked = root.findViewById(R.id.tw_tot_hours_worked);
        hoursExtra = root.findViewById(R.id.tw_tot_extra_hours_worked);
    }

    public void writeHoursInGraphics(String totHoursShouldWork, String totHoursWorked, String totExtraHours, int daysCalculatedOn) {
        hoursShouldWork.setText(totHoursShouldWork);
        hoursWorked.setText(totHoursWorked);
        hoursExtra.setText(totExtraHours);
    }
}
