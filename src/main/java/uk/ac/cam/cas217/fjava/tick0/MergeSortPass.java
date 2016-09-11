package uk.ac.cam.cas217.fjava.tick0;

import java.io.*;

class MergeSortPass extends ExternalSortPass {
    private final long blockSize;

    MergeSortPass(File sourceFile, File destinationFile, boolean reversed, long blockSize) throws IOException {
        super(sourceFile, destinationFile, reversed);
        this.blockSize = blockSize;
    }

    @Override
    public void performSortPass() throws IOException {
        System.out.println(String.format("Merging blocksize %s", blockSize));

        for (long offset = 0; offset * blockSize * 2 < intsInFile; offset += 1) {
            //System.out.println("BLOCK");
            merge(offset);
        }

        destinationStream.flush();
        destinationStream.close();
        sourceAccessFile.close();
    }

    private void merge(long offset) throws IOException {
        long start = blockSize * offset * 2;

        MergeSource source1 = new MergeSource(sourceFile, start, blockSize);
        MergeSource source2 = new MergeSource(sourceFile, Math.min(start + blockSize, intsInFile), blockSize);
        long end1 = source2.getIndex();
        long end2 = Math.min(source2.getIndex() + blockSize, intsInFile);

        while (source1.getIndex() < end1 || source2.getIndex() < end2) {
            int dataToWrite;

            if (source2.getIndex() >= end2) {
                dataToWrite = source1.getValue();
                source1.increaseIndex();
            } else if (source1.getIndex() >= end1) {
                dataToWrite = source2.getValue();
                source2.increaseIndex();
            } else {
                if (source1.getValue() < source2.getValue()) {
                    dataToWrite = source1.getValue();
                    source1.increaseIndex();
                } else {
                    dataToWrite = source2.getValue();
                    source2.increaseIndex();
                }
            }

            //System.out.println(dataToWrite);

            destinationStream.writeInt(dataToWrite);

        }

        source1.close();
        source2.close();
    }

    private static class MergeSource implements Closeable {
        private final DataInputStream inputStream;

        private long index;
        private int currentValue;
        private RandomAccessFile accessFile;
        private boolean isValueValid = false;

        private MergeSource(File sourceFile, long startIntIndex, long blockSize) throws IOException {
            inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
            inputStream.skip(startIntIndex * 4);
            accessFile = new RandomAccessFile(sourceFile, "r");
            index = startIntIndex;
        }


        public void increaseIndex() {
            isValueValid = false;
            index ++;
            // System.out.println(index);
        }

        public int getValue() throws IOException {
            if (!isValueValid) {
                currentValue = inputStream.readInt();//ExternalSortPass.intFromFileAtPos(accessFile, index);
                //System.out.println(index);
                // System.out.println(String.format("Stream: %d, Access: %d", inputStream.readInt(), currentValue));
                isValueValid = true;
            }
            // System.out.println(String.format("Stream: %d, Access: %d", ExternalSortPass.intFromFileAtPos(accessFile, index), currentValue));
            return currentValue;
        }

        public long getIndex() {
            return index;
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
            accessFile.close();
        }
    }
}
