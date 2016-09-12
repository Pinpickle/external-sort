package uk.ac.cam.cas217.fjava.tick0;

import java.io.*;

class MemorySortPass extends ExternalSortPass {
    private long blockSize;

    MemorySortPass(File sourceFile, File destinationFile) throws IOException {
        super(sourceFile, destinationFile);
    }

    @Override
    public void performSortPass() throws IOException {
        sourceAccessFile.seek(0);
        blockSize = calculateBlockSize();

        for (long offset = 0; offset * blockSize < intsInFile; offset += 1) {
            sortBlock(offset);
        }

        destinationStream.close();
        sourceAccessFile.close();
    }

    public long getBlockSize() {
        return blockSize;
    }

    private void sortBlock(long offset) throws IOException {
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
        long start = blockSize * offset;
        long finish = Math.min(start + blockSize, intsInFile);
        int[] values = new int[(int) blockSize];
        inputStream.skip(start * 4);

        for (long index = start; index < finish; index ++) {
            values[(int) (index - start)] = inputStream.readInt();
        }

        RadixSort.radixSortInPlace(values);

        inputStream.close();

        for (long index = start; index < finish; index ++) {
            destinationStream.writeInt(values[(int) (index - start)]);
        }

        destinationStream.flush();
    }

    private long calculateBlockSize() {
        System.gc();
        long blockSize = Math.min(Runtime.getRuntime().freeMemory() / 40, Math.min(Integer.MAX_VALUE, intsInFile));

        if (blockSize == 0) {
            return 0;
        }

        while (intsInFile % blockSize != 0) {
            blockSize -= 1;
        }

        return blockSize;
    }
}
