package com.example.oneinamillion.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.oneinamillion.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    // reference to class object using interfaces
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
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Log.i("Date Picker", String.valueOf(month));
        if (day < 10 && month < 9) {
            String date = "0"+String.valueOf(month+1) + "/0" + String.valueOf(day) + "/" + String.valueOf(year);
            notifyDatePickerListener(date);
        }
        else if (day < 10 && month >= 9) {
            String date = String.valueOf(month+1) + "/0" + String.valueOf(day) + "/" + String.valueOf(year);
            notifyDatePickerListener(date);
        }
        else if (day >= 10 && month < 9) {
            String date = "0"+String.valueOf(month+1) + "/"+ String.valueOf(day) + "/" + String.valueOf(year);
            notifyDatePickerListener(date);
        }
        else{
            String date = String.valueOf(month+1) + "/"+ String.valueOf(day) + "/" + String.valueOf(year);
            notifyDatePickerListener(date);
        }
    }
}
