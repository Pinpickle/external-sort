package uk.ac.cam.cas217.fjava.tick0;

/**
 * Utility class for efficiently sorting integers in place
 */
public class InPlaceSort {
    /**
     * We optimise by switching from radix sort to insertion sort if the partition of the array is smaller than a
     * predefined value. This is that value.
     */
    private static final int SORT_THRESHOLD = 200;

    public static int[] sortInPlace(int[] input){
        return sortInPlace(input, input.length);
    }

    public static int[] sortInPlace(int[] input, int end) {
        if (end < SORT_THRESHOLD) {
            return insertionSortInPlace(input, 0, end);
        } else {
            return radixSortInPlace(input, 0, end, 31);
        }
    }

    private static int[] radixSortInPlace(int[] toPartition, int start, int end, int place) {
        int lesserPartition = start;
        int greaterPartition = end;

        while (lesserPartition != greaterPartition) {
            boolean bit = ((toPartition[lesserPartition] >> place) & 1) == 1;

            if (place == 31) {
                // This is the sign bit, we need to sort from greatest to smallest
                bit = !bit;
            }

            if (!bit) {
                lesserPartition ++;
            } else {
                swapIndicesInArray(toPartition, lesserPartition, greaterPartition - 1);

                greaterPartition --;
            }
        }

        if (place > 0) {
            if (start < lesserPartition - SORT_THRESHOLD) {
                radixSortInPlace(toPartition, start, lesserPartition, place - 1);
            } else {
                insertionSortInPlace(toPartition, start, lesserPartition);
            }

            if (greaterPartition < end - SORT_THRESHOLD) {
                radixSortInPlace(toPartition, greaterPartition, end, place - 1);
            } else {
                insertionSortInPlace(toPartition, greaterPartition, end);
            }
        }

        return toPartition;
    }

    /**
     * Visible for testing
     */
    static int[] insertionSortInPlace(int[] toSort, int start, int end) {
        if (end - start < 1) {
            return toSort;
        }

        for (int index = start; index < end; index ++) {
            int value = toSort[index];
            int compareIndex = index - 1;

            while ((compareIndex >= 0) && (toSort[compareIndex] > value)) {
                toSort[compareIndex + 1] = toSort[compareIndex];
                compareIndex --;
            }

            toSort[compareIndex + 1] = value;
        }

        return toSort;
    }

    private static void swapIndicesInArray(int[] array, int firstIndex, int secondIndex) {
        int elementAtFirstIndex = array[firstIndex];
        array[firstIndex] = array[secondIndex];
        array[secondIndex] = elementAtFirstIndex;
    }
}
