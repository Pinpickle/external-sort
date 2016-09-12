package uk.ac.cam.cas217.fjava.tick0;

import java.util.Arrays;

public class RadixSort {
    public static int[] radixSortInPlace(int[] input){
        return inplacePartition(input, 0, input.length, 31);
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
                int valueInGreaterPartition = toPartition[greaterPartition - 1];
                toPartition[greaterPartition - 1] = toPartition[lesserPartition];
                toPartition[lesserPartition] = valueInGreaterPartition;

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
}
