package uk.ac.cam.cas217.fjava.tick0;

import org.junit.Test;
import uk.ac.cam.cas217.fjava.tick0.utils.InPlaceSort;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.assertArrayEquals;

public class InPlaceSortTest {
    @Test
    public void sortsWithPositiveIntegers() {
        assertArrayEquals(
            new int[] {1, 3, 4, 5, 6, 7, 8, 9},
            InPlaceSort.sortInPlace(new int[] {3, 5, 1, 6, 4, 7, 9, 8})
        );
    }

    @Test
    public void sortsWithPositiveAndNegativeIntegers() {
        assertArrayEquals(
            new int[] {-9, -8, -7, -6, 1, 3, 4, 5},
            InPlaceSort.sortInPlace(new int[] {3, 5, 1, -6, 4, -7, -9, -8})
        );
    }

    @Test
    public void sortWorksWithLargeArrays() {
        int[] ints = new Random().ints(100000, Integer.MIN_VALUE, Integer.MAX_VALUE).toArray();
        int[] expectedInts = IntStream.of(ints).sorted().toArray();

        long timeNow = System.nanoTime();
        int[] sortedInts = InPlaceSort.sortInPlace(ints);
        System.out.println(String.format("Radix sort of 100000 integers took %d micro seconds", (System.nanoTime() - timeNow) / 1000));

        assertArrayEquals(
            expectedInts,
            sortedInts
        );
    }

    @Test
    public void insertionSortWorks() {
        assertArrayEquals(
            new int[] {1, 3, 4, 5, 6, 7, 8, 9},
            InPlaceSort.insertionSortInPlace(new int[] {3, 5, 1, 6, 4, 7, 9, 8}, 0, 8)
        );
    }
}
