package uk.ac.cam.cas217.fjava.tick0;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class MergeSortPass extends ExternalSortPass {
    private final long blockSize;

    MergeSortPass(File sourceFile, File destinationFile, boolean reversed, long blockSize) throws IOException {
        super(sourceFile, destinationFile, reversed);
        this.blockSize = blockSize;
    }

    @Override
    public void performSortPass() throws IOException {
        System.out.println(String.format("Merging blocksize %d", blockSize));

        merge();

        destinationStream.flush();
        destinationStream.close();
        sourceAccessFile.close();
    }

    private void merge() throws IOException {
        List<MergeSource> mergeSourceList = new LinkedList<>();
        for (long index = 0; index < intsInFile; index += blockSize) {
            mergeSourceList.add(new MergeSource(sourceFile, Math.min(index, intsInFile), Math.min(index + blockSize, intsInFile)));
        }

        boolean mergeFound;

        MergeSource[] mergeSources = mergeSourceList.toArray(new MergeSource[0]);

        System.out.println(String.format("Merging %d blocks", mergeSources.length));

        do {
            int smallestIndex = 0;
            mergeFound = false;
            for (int index = 0; index < mergeSources.length; index ++) {
                if (mergeSources[index].hasRemaining() &&
                    ((!mergeFound) || (mergeSources[index].getValue() < mergeSources[smallestIndex].getValue()))) {
                    smallestIndex = index;
                    mergeFound = true;
                }
            }

            if (mergeFound) {
                destinationStream.writeInt(mergeSources[smallestIndex].getValue());
                mergeSources[smallestIndex].increaseIndex();
            }
        } while (mergeFound);


        for (MergeSource mergeSource : mergeSources) {
            mergeSource.close();
        }
    }

    private static class MergeSource implements Closeable {
        private final DataInputStream inputStream;

        private long index;
        private int currentValue;
        private long endIntIndex;

        private MergeSource(File sourceFile, long startIntIndex, long endIntIndex) throws IOException {
            inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
            inputStream.skip(startIntIndex * 4);
            index = startIntIndex - 1;
            this.endIntIndex = endIntIndex;
            increaseIndex();
        }


        public void increaseIndex() throws IOException {
            index ++;
            if (hasRemaining()) {
                currentValue = inputStream.readInt();
            }
        }

        public int getValue() throws IOException {
            return currentValue;
        }

        public boolean hasRemaining() {
            return index < endIntIndex;
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }
    }
}
