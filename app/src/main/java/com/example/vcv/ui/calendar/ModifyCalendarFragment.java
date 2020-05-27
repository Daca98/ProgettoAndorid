package com.example.vcv.ui.calendar;

import android.os.Bundle;
import android.util.Log;
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

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class ModifyCalendarFragment extends Fragment {
    private CalendarViewModel calendarViewModel;
    private CalendarOrder currentOrder;
    private Boolean useRemoteDate;
    private CalendarFragment calendarFragment;
    private TextView hourStart, hourEnd, job;
    private EditText etJob, etStartHour, etEndHour, etTotalHour, etEquipment, etNote, twExtraHour;
    private Button modify;

    /**
     * Constructor
     *
     * @param calendarFragment
     */
    public ModifyCalendarFragment(CalendarFragment calendarFragment) {
        this.calendarFragment = calendarFragment;
        currentOrder = calendarFragment.currentOrder;
        useRemoteDate = calendarFragment.useRemoteDate;
    }

    /**
     * Method used to create the fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel.context = this.getContext();
        CalendarViewModel.currentCalendarOrder = currentOrder;
        calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_modifycalendar, container, false);

        initGraphic(root);

        setData();

        modify.setOnClickListener(new View.OnClickListener() {
            /**
             * Method to handle modify button's click
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                try {
                    CalendarOrder newOrder = new CalendarOrder(
                            currentOrder.dateCalendarOrder,
                            hourStart.getText().toString(),
                            hourEnd.getText().toString(),
                            etTotalHour.getText().toString(),
                            etJob.getText().toString(),
                            currentOrder.confirmed,
                            etEquipment.getText().toString(),
                            etNote.getText().toString(),
                            etStartHour.getText().toString(),
                            etEndHour.getText().toString()
                    );
                    if (currentOrder.equals(newOrder)) {
                        calendarViewModel.saveChanges(newOrder);
                        etStartHour.setText(newOrder.realHourFrom);
                        etEndHour.setText(newOrder.realHourTo);
                        calendarFragment.checkOnLocalData(newOrder);
                        twExtraHour.setText(getExtraordinaryHours(newOrder.realHourFrom, newOrder.realHourTo, newOrder.defaultHourToWork));
                        currentOrder = newOrder;
                        Toast.makeText(getContext(), getString(R.string.save_data_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("SAVE_ORDER_CHANGES", "Error to save changes of specific order");
                        Toast.makeText(getContext(), getString(R.string.please_change_fields), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("SAVE_ORDER_CHANGES", "Error to save changes of specific order " + e.getMessage());
                    Toast.makeText(getContext(), getString(R.string.please_change_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    /**
     * Method used to initialize graphics components
     *
     * @param root
     */
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

        twExtraHour = root.findViewById(R.id.et_extraordinaryHour);

        modify = root.findViewById(R.id.button_modify);
    }

    /**
     * Method used to set value in graphic components
     */
    private void setData() {
        hourStart.setText(currentOrder.hourFrom);
        hourEnd.setText(currentOrder.hourTo);
        job.setText(currentOrder.job);

        etJob.setText(currentOrder.job);
        etTotalHour.setText(currentOrder.defaultHourToWork);
        if (!currentOrder.realHourFrom.equals("00:00")) {
            etStartHour.setText(currentOrder.realHourFrom);
        }
        if (!currentOrder.realHourTo.equals("00:00")) {
            etEndHour.setText(currentOrder.realHourTo);
        }
        if (!currentOrder.equipment.equals("")) {
            etEquipment.setText(currentOrder.equipment);
        }
        if (!currentOrder.note.equals("")) {
            etNote.setText(currentOrder.note);
        }
        if (!currentOrder.realHourTo.equals("00:00") && !currentOrder.realHourFrom.equals("00:00")) {
            twExtraHour.setText(getExtraordinaryHours(currentOrder.realHourFrom, currentOrder.realHourTo, currentOrder.defaultHourToWork));
        }

        if (!useRemoteDate) {
            modify.setEnabled(true);
            modify.setBackgroundResource(R.drawable.button_confirm);
        } else {
            modify.setEnabled(false);
            modify.setBackgroundResource(R.drawable.button_disabled);
        }
    }

    /**
     * Method used to get extra hours
     *
     * @param startHour
     * @param endHour
     * @param dftHour
     * @return extra hours
     */
    public String getExtraordinaryHours(String startHour, String endHour, String dftHour) {
        long extraSeconds = ((getSeconds(endHour) - getSeconds(startHour)) - getSeconds(dftHour));

        return getHourFromSeconds(extraSeconds);
    }

    /**
     * Method used to convert string to long that rappresent hours and minutes in seconds
     *
     * @param hour
     * @return long that rappresent hours and minutes in seconds
     */
    private long getSeconds(String hour) {
        long hourSeconds = Integer.parseInt(hour.split(":")[0]) * 3600;
        long minutesSeconds = Integer.parseInt(hour.split(":")[1]) * 60;

        return hourSeconds + minutesSeconds;
    }

    /**
     * Method used to convert long into String that rappresent hour and minutes from seconds
     *
     * @param seconds
     * @return String that rappresent hour and minutes from seconds
     */
    private String getHourFromSeconds(long seconds) {
        String res = "";

        if (seconds > 0) {
            if ((seconds / 3600.0) < 1.0) {
                String minutes = Double.toString(seconds / 60.0);

                res += "00:" + String.format("%02d", Integer.parseInt(minutes.split("\\.")[0]));
            } else {
                String hourString = Double.toString(seconds / 3600.0);
                int hourInt = Integer.parseInt(hourString.split("\\.")[0]);
                res += String.format("%02d", hourInt);

                res += ":";

                String minutes = Double.toString((seconds - (3600 * hourInt)) / 60.0);
                res += String.format("%02d", Integer.parseInt(minutes.split("\\.")[0]));
            }
        } else {
            res = "00:00";
        }

        return res;
    }
}
