package uk.ac.cam.cas217.fjava.tick0;

import uk.ac.cam.cas217.fjava.tick0.merge.MergeSortPass;

import java.io.*;

/**
 * Sorts a data file using a temporary file as a data store
 *
 * Temporary file has to be of the same size as the data file
 */
class ExternalSorter {
    private long availableMemory = 1000000;
    private final String originalFilePath;
    private final String dataFilePath;

    ExternalSorter(String originalFile, String temporaryFile) {
        originalFilePath = originalFile;
        dataFilePath = temporaryFile;
    }

    void sort() throws IOException {
        File originalFileHandle = new File(originalFilePath);
        File dataFileHandle = new File(dataFilePath);

        long intsInFile = originalFileHandle.length() / 4;
        long blockSize = calculateInitialBlockSize(intsInFile);

        System.out.println(String.format("Sorting file with bytes: %s", originalFileHandle.length()));

        if (blockSize == intsInFile) {
            // We can perform the entire sort in memory, no need for the data file
            new MemorySortPass(originalFileHandle, originalFileHandle, blockSize).performSortPass();
        } else {
            new MemorySortPass(originalFileHandle, dataFileHandle, blockSize).performSortPass();
            new MergeSortPass(dataFileHandle, originalFileHandle, blockSize).performSortPass();
        }
    }

    private long calculateInitialBlockSize(long intsInFile) {
        System.gc();
        long blockSize = Math.min(Runtime.getRuntime().freeMemory() / 6, Math.min(Integer.MAX_VALUE, intsInFile));

        if (blockSize == 0) {
            return 0;
        }

        while (intsInFile % blockSize != 0) {
            blockSize -= 1;
        }

        return blockSize;
    }
}
