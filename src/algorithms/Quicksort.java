package algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2011-09-10
 */
public class Quicksort<Obj extends Comparable<? super Obj>> {

    private List<Obj> sortedList;

    /**
     * Constructor; when the object is made the sort is automatically called on
     * the passed in collection.
     * 
     * @param unsortedList a collection of objects to be sorted which extend the
     *            {@link Comparable} interface.
     */
    public Quicksort(Collection<Obj> unsortedList) {
        this.sortedList = new ArrayList<Obj>();
        this.sortedList.addAll(unsortedList);
        quickSort(this.sortedList, 0, sortedList.size() - 1);
    }

    /**
     * The recursive quicksort function calls a partition function to determine
     * the pivot point. After the pivot point is found this function is called
     * again on each side of the array as split by the pivot point.
     * 
     * @param unsortedList the unsorted list of objects to be sorted.
     * @param startIdx the initial index to begin sorting at.
     * @param endIdx the final index to end sorting at.
     */
    private void quickSort(List<Obj> unsortedList, int startIdx, int endIdx) {
        if (startIdx < endIdx) {
            int partitionIdx = partition(unsortedList, startIdx, endIdx);
            quickSort(unsortedList, startIdx, partitionIdx - 1);
            quickSort(unsortedList, partitionIdx + 1, endIdx);
        }

    }

    /**
     * The partition function selects an element from the list and moves it to
     * the correct location in the list. The location of this element in the
     * list is then returned.
     * 
     * @param unsortedList the unsorted list of objects to be sorted.
     * @param startIdx the initial index to begin sorting at.
     * @param endIdx the final index to end sorting at.
     * @return An integer representing the location of the pivot (sorted
     *         element).
     */
    private int partition(List<Obj> unsortedList, int startIdx, int endIdx) {
        // Pick the last element in our section to use as the pivot point.
        Obj x = unsortedList.get(endIdx);
        int i = startIdx - 1;
        // For each item, if it has a lower value than the pivot then swap them.
        for (int j = startIdx; j < endIdx; j++) {
            //if a[j] <= x
            if (unsortedList.get(j).compareTo(x) <= 0) {
                i++;
                // Swap a[i] and a[j].
                Obj temp = unsortedList.get(i);
                unsortedList.set(i, unsortedList.get(j));
                unsortedList.set(j, temp);
            }
        }
        // Swap a[i+1] with a[endIdx].
        Obj temp = unsortedList.get(endIdx);
        unsortedList.set(endIdx, unsortedList.get(i + 1));
        unsortedList.set(i + 1, temp);

        return i + 1;
    }

    /**
     * Get the sorted list of objects.
     * 
     * @return The sorted list of objects.
     */
    public List<Obj> getSortedList() {
        return sortedList;
    }

}
