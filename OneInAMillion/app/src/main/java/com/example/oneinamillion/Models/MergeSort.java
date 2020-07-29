package com.example.oneinamillion.Models;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MergeSort {
    String sort_metric;
    double leftcomparable;
    double rightcomparable;
    final static String date_metric = "date";
    final static  String price_metric = "price";
    final static String distance_metric = "distance";

    public MergeSort(String sort_metric) {
        this.sort_metric = sort_metric;
    }

    public void Merge(List<Event> left, List<Event> right, List<Event> unsorted) {
        int left_index = 0;
        int right_index = 0;
        int index = 0;
        while (left_index < left.size() && right_index < right.size()) {
            //static variable or string resources
            switch(sort_metric) {
                case date_metric:
                    try {
                        Date leftdate = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.ENGLISH)
                                .parse(left.get(left_index).getDate() + " " + left.get(left_index).getTime());
                        Date rightdate = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.ENGLISH)
                                .parse(right.get(right_index).getDate() + " " + right.get(right_index).getTime());
                        assert leftdate != null;
                        leftcomparable = (double) leftdate.getTime();
                        assert rightdate != null;
                        rightcomparable = (double) rightdate.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;

                case distance_metric:
                    leftcomparable = left.get(left_index).getDistance();
                    rightcomparable = right.get(right_index).getDistance();
                    break;

                case price_metric:
                    leftcomparable = left.get(left_index).getPrice();
                    rightcomparable = right.get(right_index).getPrice();
                    break;

                default:
                    Log.i("help","help");
                }

        if (leftcomparable<rightcomparable) {
            unsorted.remove(index);
            unsorted.add(index, left.get(left_index));
            left_index++;
            index++;
        } else {
            unsorted.remove(index);
            unsorted.add(index, right.get(right_index));
            right_index++;
            index++;
        }

        }
        while (left_index < left.size()) {
            unsorted.remove(index);
            unsorted.add(index, left.get(left_index));
            left_index++;
            index++;
        }
        while (right_index < right.size()) {
            unsorted.remove(index);
            unsorted.add(index, right.get(right_index));
            right_index++;
            index++;
        }
    }

    public void mergeSort(List<Event> unsorted) {
        if (unsorted.size() > 1) {
            int mid = unsorted.size() / 2;
            List<Event> left = new ArrayList<>(unsorted.subList(0, mid));
            List<Event> right = new ArrayList<>(unsorted.subList(mid, unsorted.size()));
            mergeSort(left);
            mergeSort(right);
            Merge(left, right, unsorted);
        }
    }
}
