package uk.ac.cam.cas217.fjava.tick0;

import java.io.*;

class MemorySortPass extends ExternalSortPass {
    private long blockSize;
    private DataOutputStream destinationStreamLazyInit = null;
    private final int[] valuesToWrite;

    MemorySortPass(File sourceFile, File destinationFile, long blockSize) throws IOException {
        super(sourceFile, destinationFile);
        if ((sourceFile == destinationFile) && (blockSize != intsInFile)) {
            throw new IllegalArgumentException("If source and destination are the same, blockSize must = filesize / 4");
        }
        this.blockSize = blockSize;
        valuesToWrite = new int[(int) blockSize];
    }

    @Override
    public void performSortPass() throws IOException {
        System.out.println("Performing in-memory sort");

        for (long offset = 0; offset * blockSize < intsInFile; offset += 1) {
            sortBlock(offset);
        }

        getDestinationStreamLazily().close();
    }

    private void sortBlock(long offset) throws IOException {
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
        long start = blockSize * offset;
        long finish = Math.min(start + blockSize, intsInFile);
        inputStream.skip(start * 4);

        for (long index = start; index < finish; index ++) {
            valuesToWrite[(int) (index - start)] = inputStream.readInt();
        }

        RadixSort.radixSortInPlace(valuesToWrite);

        inputStream.close();

        DataOutputStream destinationStream = getDestinationStreamLazily();

        for (long index = start; index < finish; index ++) {
            destinationStream.writeInt(valuesToWrite[(int) (index - start)]);
        }

        destinationStream.flush();
    }

    /**
     * We create the destination stream on the first time that we need it, as it erases the destination file when it is
     * created. This way, if there is only one pass done, read and write can be on the same file.
     */
    private DataOutputStream getDestinationStreamLazily() throws IOException {
        if (destinationStreamLazyInit == null) {
            destinationStreamLazyInit = createDataOutputStream(destinationFile);
        }

        return destinationStreamLazyInit;
    }
}
