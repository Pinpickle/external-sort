package uk.ac.cam.cas217.fjava.tick0.merge;

import java.io.*;

class MergeSource implements IntegerSource, Closeable {
    private final DataInputStream inputStream;

    private long index;
    private int currentValue;
    private long endIntIndex;

    MergeSource(File sourceFile, long startIntIndex, long endIntIndex, int bufferSize) throws IOException {
        inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(sourceFile), bufferSize));
        inputStream.skip(startIntIndex * 4);
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
