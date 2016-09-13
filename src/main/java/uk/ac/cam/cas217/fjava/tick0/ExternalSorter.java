package uk.ac.cam.cas217.fjava.tick0;

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

        MemorySortPass initialPass = new MemorySortPass(originalFileHandle, dataFileHandle);
        initialPass.performSortPass();

        long blockSize = initialPass.getBlockSize();
        long fileLength = originalFileHandle.length() / 4;
        boolean reversed = true;

        for (; blockSize < fileLength; blockSize *= 2) {
            new MergeSortPass(originalFileHandle, dataFileHandle, reversed, blockSize).performSortPass();

            reversed = !reversed;
        }

        if (reversed) {
            new CopyPass(originalFileHandle, dataFileHandle, true).performSortPass();
        }
    }
}
