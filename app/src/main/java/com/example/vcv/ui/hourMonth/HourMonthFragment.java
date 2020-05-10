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

public class HourMonthFragment extends Fragment {

    private HourMonthViewModel hourMonthViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        hourMonthViewModel =
                ViewModelProviders.of(this).get(HourMonthViewModel.class);
        View root = inflater.inflate(R.layout.fragment_hourmonth, container, false);

        return root;
    }
}
