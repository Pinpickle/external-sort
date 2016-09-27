package uk.ac.cam.cas217.fjava.tick0;

public class RadixSort {
    public static int[] radixSortInPlace(int[] input){
        return radixSortInPlace(input, input.length);
    }

    public static int[] radixSortInPlace(int[] input, int end) {
        return inplacePartition(input, 0, end, 31);
    }

    private static int[] inplacePartition(int[] toPartition, int start, int end, int place) {
        int lesserPartition = start;
        int greaterPartition = end;

        while (lesserPartition != greaterPartition) {
            boolean bit = ((toPartition[lesserPartition] >> place) & 1) == 1;

            if (place == 31) {
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
            if (start < lesserPartition - 1) {
                inplacePartition(toPartition, start, lesserPartition, place - 1);
            }

            if (greaterPartition < end - 1) {
                inplacePartition(toPartition, greaterPartition, end, place - 1);
            }
        }

        return toPartition;
    }

    private static void swapIndicesInArray(int[] array, int firstIndex, int secondIndex) {
        int elementAtFirstIndex = array[firstIndex];
        array[firstIndex] = array[secondIndex];
        array[secondIndex] = elementAtFirstIndex;
    }
}
