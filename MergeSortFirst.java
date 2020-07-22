import java.util.ArrayList;
import java.util.List;

public class MergeSortFirst <T extends Comparable<T>> {
    public void Merge(List<T> left, List<T> right, List<T> unsorted) {
        int left_index = 0;
        int right_index = 0;
        int index = 0;
        while (left_index < left.size() && right_index < right.size()) {
            if (left.get(left_index).compareTo(right.get(right_index)) < 0) {
                unsorted.remove(index); // O(n)
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

    public void mergeSort(List<T> unsorted) {
        // [6, 3, 4]
        if (unsorted.size() > 1) {
            int mid = unsorted.size() / 2;
            List<T> left = new ArrayList<>(unsorted.subList(0, mid));   // O(n)
            List<T> right = new ArrayList<>(unsorted.subList(mid, unsorted.size()));
            mergeSort(left);
            mergeSort(right);
            Merge(left, right, unsorted);
        }
    }

    public static void main(String[] args) {
        List<Double> gia = new ArrayList<>();
        gia.add(67.3);
        gia.add(72.2);
        gia.add(633.0);
        gia.add(2d);
        MergeSort<Double> m = new MergeSort<>();
        m.mergeSort(gia);
        System.out.println(gia);
    }
}


