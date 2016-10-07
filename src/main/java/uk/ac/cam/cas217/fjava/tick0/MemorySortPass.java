package uk.ac.cam.cas217.fjava.tick0;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * Sort pass that sorts blocks of the file in memory and writes them to the destination file.
 *
 * If and only if the block size is equal to the number of integers in the file, the destination file may be the same
 * as the source file. This allows the entire file to be sorted in memory.
 */
class MemorySortPass extends ExternalSortPass {
    private int blockSize;
    private final IntegerArrayByteChannel valuesToWrite;

    MemorySortPass(File sourceFile, File destinationFile, int blockSize) throws IOException {
        super(sourceFile, destinationFile);
        if ((sourceFile == destinationFile) && (blockSize != intsInFile)) {
            throw new IllegalArgumentException("If source and destination are the same, blockSize must = filesize / 4");
        }
        this.blockSize = blockSize;
        valuesToWrite = new IntegerArrayByteChannel(blockSize);
    }

    @Override
    public void performSortPass() throws IOException {
        System.out.println("Performing in-memory sort");

        for (long offset = 0; offset * blockSize < intsInFile; offset += 1) {
            sortBlock(offset);
        }

        valuesToWrite.close();
    }

    private void sortBlock(long offset) throws IOException {
        valuesToWrite.resetReadWrite();

        FileChannel sourceChannel = FileChannel.open(sourceFile.toPath(), StandardOpenOption.READ);

        long start = blockSize * offset;
        long finish = Math.min(start + blockSize, intsInFile);
        sourceChannel.transferTo(start * 4, (finish - start) * 4, valuesToWrite);
        sourceChannel.close();

        InPlaceSort.sortInPlace(valuesToWrite.getIntegers(), (int) (finish - start));

        FileChannel desinationChannel = FileChannel.open(destinationFile.toPath(), StandardOpenOption.WRITE);
        desinationChannel.transferFrom(valuesToWrite, start * 4, (finish - start) * 4);
        desinationChannel.close();
    }
}
