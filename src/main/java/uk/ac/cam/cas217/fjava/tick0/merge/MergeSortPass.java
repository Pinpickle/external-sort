package uk.ac.cam.cas217.fjava.tick0.merge;

import uk.ac.cam.cas217.fjava.tick0.ExternalSortPass;

import java.io.*;
import java.util.*;

public class MergeSortPass extends ExternalSortPass {
    private final long blockSize;

    public MergeSortPass(File sourceFile, File destinationFile, long blockSize) throws IOException {
        super(sourceFile, destinationFile);
        this.blockSize = blockSize;
    }

    @Override
    public void performSortPass() throws IOException {
        System.out.println(String.format("Merging blocksize %d for %d ints", blockSize, intsInFile));

        merge();
    }

    private void merge() throws IOException {
        int bufferSize = estimateMergeSourceBufferSize();
        System.out.println(String.format("Using buffer of %d bytes for merging", bufferSize));
        List<MergeSource> mergeSources = new LinkedList<>();
        for (long index = 0; index < intsInFile; index += blockSize) {
            mergeSources.add(new MergeSource(sourceFile, Math.min(index, intsInFile), Math.min(index + blockSize, intsInFile), bufferSize));
        }

        System.out.println(String.format("Merging %d blocks", mergeSources.size()));

        DataOutputStream destinationStream = createDataOutputStream(destinationFile);

        IntegerSourceMerger sourceQueue = new IntegerSourceMerger(mergeSources);

        while(sourceQueue.hasRemaining()) {
            destinationStream.writeInt(sourceQueue.getNextValue());
        }

        for (MergeSource mergeSource : mergeSources) {
            mergeSource.close();
        }

        destinationStream.flush();
        destinationStream.close();
    }

    private int estimateMergeSourceBufferSize() {
        System.gc();
        return Math.min(
            (int) blockSize,
            (int) Math.min((Runtime.getRuntime().freeMemory() / ((int) Math.ceil((float) intsInFile / (float) blockSize))) / 10, Integer.MAX_VALUE / 4)
        ) * 4;
    }

}
