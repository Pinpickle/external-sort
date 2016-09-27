package uk.ac.cam.cas217.fjava.tick0.merge;

import uk.ac.cam.cas217.fjava.tick0.ExternalSortPass;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Sorts the contents from the source file into the destination file by merging blocks together that are already
 * sorted. The merge is an n-way merge where n is the number of separate sorted blocks.
 *
 * If n * blockSize > integers in file, the last block may be partially filled.
 */
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

        List<FileIntegerSource> fileIntegerSources = new LinkedList<>();
        for (long index = 0; index < intsInFile; index += blockSize) {
            fileIntegerSources.add(new FileIntegerSource(sourceFile, Math.min(index, intsInFile), Math.min(index + blockSize, intsInFile), bufferSize));
        }

        System.out.println(String.format("Merging %d blocks", fileIntegerSources.size()));

        DataOutputStream destinationStream = createDataOutputStream(destinationFile);
        IntegerSource sortedIntegerSource = new IntegerSourceMerger(fileIntegerSources);

        while(sortedIntegerSource.hasRemaining()) {
            destinationStream.writeInt(sortedIntegerSource.getValue());
            sortedIntegerSource.readyyNextIndex();
        }

        for (FileIntegerSource fileIntegerSource : fileIntegerSources) {
            fileIntegerSource.close();
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
