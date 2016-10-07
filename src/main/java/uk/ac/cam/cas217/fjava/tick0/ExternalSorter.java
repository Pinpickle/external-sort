package uk.ac.cam.cas217.fjava.tick0;

import uk.ac.cam.cas217.fjava.tick0.counting.CountingSortPass;
import uk.ac.cam.cas217.fjava.tick0.counting.IntegerFileRangeFinder;
import uk.ac.cam.cas217.fjava.tick0.merge.MergeSortPass;

import java.io.File;
import java.io.IOException;

/**
 * Sorts a data file using a temporary file as a data store
 */
class ExternalSorter {
    private final String originalFilePath;
    private final String dataFilePath;

    ExternalSorter(String originalFile, String temporaryFile) {
        originalFilePath = originalFile;
        dataFilePath = temporaryFile;
    }

    void sort() throws IOException {
        File originalFile = new File(originalFilePath);
        File dataFile = new File(dataFilePath);

        if (originalFile.length() % 4 != 0) {
            throw new IllegalArgumentException("The file to sort must contain only integers. Its length must be divisible by 4");
        }

        long intsInFile = originalFile.length() / 4;
        int blockSize = calculateInitialBlockSize(intsInFile);
        System.out.println(String.format("Sorting file with bytes: %s", originalFile.length()));

        if (intsInFile <= 1) {
            System.out.println("File is already sorted");
            return;
        }

        IntegerFileRangeFinder.IntegerFileRange integerRange = new IntegerFileRangeFinder(originalFile).getIntegerRange();

        if (integerRange.getRange() <= blockSize) {
            // The range of integers is sufficiently small so we can perform counting sort on the whole file
            new CountingSortPass(originalFile, integerRange.getMinimum(), (int) integerRange.getRange() + 1).performSortPass();
        } else if (blockSize == intsInFile) {
            // We can perform the entire sort in memory, no need for the data file
            new MemorySortPass(originalFile, originalFile, blockSize).performSortPass();
        } else {
            new MemorySortPass(originalFile, dataFile, blockSize).performSortPass();
            new MergeSortPass(dataFile, originalFile, blockSize).performSortPass();
        }
    }

    private int calculateInitialBlockSize(long intsInFile) {
        System.gc();
        return (int) Math.min(Runtime.getRuntime().freeMemory() / 7, Math.min(Integer.MAX_VALUE, intsInFile));
    }
}
