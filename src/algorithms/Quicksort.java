package algorithms;

import java.lang.Comparable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Generic Quicksort;
//
// Input: A collection of comparable objects to sort.
// Output: Creates a sorted List of objects.

public class Quicksort<Obj extends Comparable<? super Obj>> {
    
    private List<Obj> sortedList;
    
    public Quicksort(Collection<Obj> unsortedList) {
        this.sortedList = new ArrayList<Obj>();
        this.sortedList.addAll(unsortedList);
        quickSort(this.sortedList, 0, sortedList.size()-1);
    }
    
    // Regular recursive quicksort function, calls a partition function to
    // determine the pivot point.
    private void quickSort(List<Obj> unsortedList, int startIdx, int endIdx) {
        if (startIdx < endIdx) {
            int partitionIdx = partition(unsortedList, startIdx, endIdx);
            quickSort(unsortedList, startIdx, partitionIdx - 1);
            quickSort(unsortedList, partitionIdx + 1, endIdx);
        }
        
    }
    
    // Partition function.
    private int partition(List<Obj> unsortedList, int startIdx, int endIdx) {
        // Pick the last element in our section to use as the pivot point.
        Obj x = unsortedList.get(endIdx);
        int i = startIdx - 1;
        // For each item, if it has a lower value
        // than the pivot then swap them.
        for (int j = startIdx; j < endIdx; j++) {
            //if a[j] <= x
            if (unsortedList.get(j).compareTo(x) <= 0) {
                i++;
                // swap a[i] and a[j]
                Obj temp = unsortedList.get(i);
                unsortedList.set(i, unsortedList.get(j));
                unsortedList.set(j, temp);
            }
        }
        // swap a[i+1] with a[endIdx]
        Obj temp = unsortedList.get(endIdx);
        unsortedList.set(endIdx, unsortedList.get(i+1));
        unsortedList.set(i+1, temp);
        
        return i + 1;
    }

    public List<Obj> getSortedList() {
        return sortedList;
    }
    
}
