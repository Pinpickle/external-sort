package uk.ac.cam.cas217.fjava.tick0.merge;

import java.io.*;

class MergeSource implements Closeable {
    private final DataInputStream inputStream;

    private long index;
    private int currentValue;
    private long endIntIndex;

    MergeSource(File sourceFile, long startIntIndex, long endIntIndex) throws IOException {
        inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
        inputStream.skip(startIntIndex * 4);
        index = startIntIndex - 1;
        this.endIntIndex = endIntIndex;
        increaseIndex();
    }


    void increaseIndex() throws IOException {
        index++;
        if (hasRemaining()) {
            currentValue = inputStream.readInt();
        }
    }

    int getValue() {
        return currentValue;
    }

    boolean hasRemaining() {
        return index < endIntIndex;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
