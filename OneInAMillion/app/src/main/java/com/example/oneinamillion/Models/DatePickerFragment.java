package com.example.oneinamillion.Models;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.oneinamillion.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DatePickerFragmentListener datePickerListener;

    public interface DatePickerFragmentListener {
        public void DateSet(String date);
    }

    public DatePickerFragmentListener getDatePickerListener() {
        return this.datePickerListener;
    }

    public void setDatePickerListener(DatePickerFragmentListener listener) {
        this.datePickerListener = listener;
    }

    protected void notifyDatePickerListener(String date) {
        if(this.datePickerListener != null) {
            this.datePickerListener.DateSet(date);
        }
    }
    public static DatePickerFragment newInstance(DatePickerFragmentListener listener) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setDatePickerListener(listener);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String date = String.valueOf(month)+"/"+String.valueOf(day)+"/"+String.valueOf(year);
        notifyDatePickerListener(date);
    }
}
