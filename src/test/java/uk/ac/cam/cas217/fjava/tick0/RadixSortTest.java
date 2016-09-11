package uk.ac.cam.cas217.fjava.tick0;

import org.junit.Test;

import static org.junit.Assert.*;

public class RadixSortTest {
    @Test
    public void radixSortsWithPositiveIntegers() {
        assertArrayEquals(
            new int[] {1, 3, 4, 5, 6, 7, 8, 9},
            RadixSort.radixSort(new int[] {3, 5, 1, 6, 4, 7, 9, 8})
        );
    }

    @Test
    public void radixSortsWithPositiveAndNegativeIntegers() {
        assertArrayEquals(
            new int[] {-9, -8, -7, -6, 1, 3, 4, 5},
            RadixSort.radixSort(new int[] {3, 5, 1, -6, 4, -7, -9, -8})
        );
    }
}
