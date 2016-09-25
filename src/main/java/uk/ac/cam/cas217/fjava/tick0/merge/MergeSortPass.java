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
        List<MergeSource> mergeSources = new LinkedList<>();
        for (long index = 0; index < intsInFile; index += blockSize) {
            mergeSources.add(new MergeSource(sourceFile, Math.min(index, intsInFile), Math.min(index + blockSize, intsInFile)));
        }

        System.out.println(String.format("Merging %d blocks", mergeSources.size()));

        DataOutputStream destinationStream = createDataOutputStream(destinationFile);

        MergeSourceList mergeSourceList = new MergeSourceList(mergeSources);

        while(mergeSourceList.hasRemaining()) {
            destinationStream.writeInt(mergeSourceList.getNextValue());
        }


        for (MergeSource mergeSource : mergeSources) {
            mergeSource.close();
        }

        destinationStream.flush();
        destinationStream.close();
    }

}
