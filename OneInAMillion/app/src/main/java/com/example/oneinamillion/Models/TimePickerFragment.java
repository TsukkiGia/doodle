package com.example.oneinamillion.Models;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.example.oneinamillion.R;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimePickerFragmentListener timePickerFragmentListener;

    public interface TimePickerFragmentListener {
        public void TimeSet(String time);
    }

    public TimePickerFragmentListener getTimePickerListener() {
        return this.timePickerFragmentListener;
    }

    public void setTimePickerListener(TimePickerFragmentListener listener) {
        this.timePickerFragmentListener = listener;
    }

    protected void notifyTimePickerListener(String time) {
        if(this.timePickerFragmentListener != null) {
            this.timePickerFragmentListener.TimeSet(time);
        }
    }
    public static TimePickerFragment newInstance(TimePickerFragment.TimePickerFragmentListener listener) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setTimePickerListener(listener);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = String.valueOf(hourOfDay)+":"+String.valueOf(minute);
        notifyTimePickerListener(time);
    }
}
