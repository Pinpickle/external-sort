package uk.ac.cam.cas217.fjava.tick0;

import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class RadixSortTest {
    @Test
    public void radixSortsWithPositiveIntegers() {
        assertArrayEquals(
            new int[] {1, 3, 4, 5, 6, 7, 8, 9},
            RadixSort.radixSortInPlace(new int[] {3, 5, 1, 6, 4, 7, 9, 8})
        );
    }

    @Test
    public void radixSortsWithPositiveAndNegativeIntegers() {
        assertArrayEquals(
            new int[] {-9, -8, -7, -6, 1, 3, 4, 5},
            RadixSort.radixSortInPlace(new int[] {3, 5, 1, -6, 4, -7, -9, -8})
        );
    }

    @Test
    public void radixSortWorksWithLargeArrays() {
        int[] ints = new Random().ints(10000, Integer.MIN_VALUE, Integer.MAX_VALUE).toArray();
        int[] expectedInts = IntStream.of(ints).sorted().toArray();

        long timeNow = System.nanoTime();
        int[] sortedInts = RadixSort.radixSortInPlace(ints);
        System.out.println(String.format("Radix sort of 10000 integers took %d micro seconds", (System.nanoTime() - timeNow) / 1000));

        assertArrayEquals(
            expectedInts,
            sortedInts
        );
    }
}
