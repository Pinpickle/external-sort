package uk.ac.cam.cas217.fjava.tick0.merge;

import uk.ac.cam.cas217.fjava.tick0.AsyncFileStream;

import java.io.*;

class MergeSource implements IntegerSource, Closeable {
    private final AsyncFileStream inputStream;

    private long index;
    private int currentValue;
    private long endIntIndex;

    MergeSource(File sourceFile, long startIntIndex, long endIntIndex) throws IOException {
        System.out.println(String.format("Merge source with size %d", endIntIndex - startIntIndex));
        inputStream = new AsyncFileStream(sourceFile, startIntIndex, endIntIndex);
        index = startIntIndex - 1;
        this.endIntIndex = endIntIndex;
        increaseIndex();
    }


    public void increaseIndex() throws IOException {
        index++;
        if (hasRemaining()) {
            currentValue = inputStream.readInt();
        }
    }

    public int getValue() {
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
