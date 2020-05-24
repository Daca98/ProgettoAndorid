package com.example.vcv.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vcv.R;
import com.example.vcv.utility.CalendarOrder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class ModifyCalendarFragment extends Fragment {
    private CalendarViewModel calendarViewModel;
    private CalendarOrder currentOrder;
    private CalendarFragment calendarFragment;
    private TextView hourStart, hourEnd, job;
    private EditText etJob, etStartHour, etEndHour, etTotalHour, etEquipment, etNote;
    TextView etExtraHour;
    private Button modify;

    public ModifyCalendarFragment(CalendarFragment calendarFragment) {
        this.calendarFragment = calendarFragment;
        currentOrder = calendarFragment.currentOrder;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel.context = this.getContext();
        CalendarViewModel.currentCalendarOrder = currentOrder;
        calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_modifycalendar, container, false);

        initGraphic(root);

        setData();

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    CalendarOrder newOrder = new CalendarOrder(
                            currentOrder.dateCalendarOrder,
                            etStartHour.getText().toString(),
                            etEndHour.getText().toString(),
                            currentOrder.defaultHourToWork,
                            etJob.getText().toString(),
                            currentOrder.confirmed,
                            etEquipment.getText().toString(),
                            etNote.getText().toString()
                    );
                    if (currentOrder.equals(newOrder)) {
                        calendarViewModel.saveChanges(newOrder);
                        hourStart.setText(newOrder.hourFrom);
                        hourEnd.setText(newOrder.hourTo);
                        Toast.makeText(getContext(), getString(R.string.save_data_success), Toast.LENGTH_SHORT).show();
                        calendarFragment.checkOnLocalData(newOrder);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.please_change_fields), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), getString(R.string.please_change_fields), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    private void initGraphic(View root) {
        hourStart = root.findViewById(R.id.HourStart);
        hourEnd = root.findViewById(R.id.HourEnd);
        job = root.findViewById(R.id.job);

        etJob = root.findViewById(R.id.et_Job);
        etStartHour = root.findViewById(R.id.et_startHour);
        etEndHour = root.findViewById(R.id.et_endHour);
        etTotalHour = root.findViewById(R.id.et_totalHour);
        etEquipment = root.findViewById(R.id.et_equipment);
        etNote = root.findViewById(R.id.et_note);

        etExtraHour = root.findViewById(R.id.et_extraordinaryHour);

        modify = root.findViewById(R.id.button_modify);
    }

    private void setData() {
        hourStart.setText(currentOrder.hourFrom);
        hourEnd.setText(currentOrder.hourTo);
        job.setText(currentOrder.job);

        etJob.setText(currentOrder.job);
        etStartHour.setText(currentOrder.hourFrom);
        etEndHour.setText(currentOrder.hourTo);
        etTotalHour.setText(currentOrder.defaultHourToWork);
        etEquipment.setText(currentOrder.equipment);
        etNote.setText(currentOrder.note);

        etExtraHour.setText("");
    }

    public void getExtraordinaryHours(String hourStart, String hourEnd, String hourDft) {
        String hourExtra;
    }


}
