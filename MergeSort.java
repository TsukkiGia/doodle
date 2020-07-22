import java.util.ArrayList;
import java.util.List;

public class MergeSort <T extends Comparable<T>> {
    public static final int COARSE_BASE_CASE = 10;
    public List<T> Merge(List<T> left, List<T> right, List<T> unsorted) {
        int left_index = 0;
        int right_index = 0;
        List<T> merged = new ArrayList<>(left.size() + right.size());
        while (left_index < left.size() && right_index < right.size()) {
            if (left.get(left_index).compareTo(right.get(right_index)) < 0) {
                merged.add(left.get(left_index));
                left_index++;
            } else {
                merged.add(right.get(right_index));
                right_index++;
            }
        }
        while (left_index < left.size()) {
            merged.add(left.get(left_index));
            left_index++;
        }
        while (right_index < right.size()) {
            merged.add(right.get(right_index));
            right_index++;
        }
        return merged;
    }

    public List<T> mergeSort(List<T> unsorted) {
        // [6, 3, 4]
        if (unsorted.size() > COARSE_BASE_CASE) {
            int mid = unsorted.size() / 2;
            List<T> left = new ArrayList<>(unsorted.subList(0, mid));   // O(n)
            List<T> right = new ArrayList<>(unsorted.subList(mid, unsorted.size()));
            return Merge(mergeSort(left), mergeSort(right), unsorted);
        } else {
            // call insertion sort
            unsorted.sort(T::compareTo); // method reference
            return unsorted;
        }
    }

    public static void main(String[] args) {
        List<Double> gia = new ArrayList<>();
        gia.add(63.0);
        gia.add(21d);
        gia.add(6.3);
        gia.add(7.214);
        gia.add(6313.0);
        gia.add(211d);
        gia.add(67.3);
        gia.add(72.2);
        gia.add(633.0);
        gia.add(2d);
        gia.add(7.3);
        gia.add(7.2);
        MergeSort<Double> m = new MergeSort<>();
        m.mergeSort(gia);
        System.out.println(m.mergeSort(gia));
    }
}


