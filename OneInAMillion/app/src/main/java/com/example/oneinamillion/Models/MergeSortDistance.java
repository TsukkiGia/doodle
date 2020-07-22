package com.example.oneinamillion.Models;

import java.util.ArrayList;
import java.util.List;

public class MergeSortDistance {
    public void Merge(List<Event> left, List<Event> right, List<Event> unsorted) {
        int left_index = 0;
        int right_index = 0;
        int index = 0;
        while (left_index < left.size() && right_index < right.size()) {
            if (left.get(left_index).getDistance()<right.get(right_index).getDistance()) {
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