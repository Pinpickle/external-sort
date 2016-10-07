package uk.ac.cam.cas217.fjava.tick0.merge;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * An {@link IntegerSource} that uses a file full of integers as the backing source
 */
class FileIntegerSource implements IntegerSource, Closeable {
    private final DataInputStream inputStream;
    private final long endIntIndex;

    private long intIndex;
    private int currentValue;

    FileIntegerSource(File sourceFile, long startIntIndex, long endIntIndex, int bufferSize) throws IOException {
        this.endIntIndex = endIntIndex;
        inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(sourceFile), bufferSize));
        inputStream.skip(startIntIndex * 4);
        intIndex = startIntIndex - 1;
        readyNextIndex();
    }

    @Override
    public void readyNextIndex() throws IOException {
        intIndex++;
        if (hasRemaining()) {
            currentValue = inputStream.readInt();
        }
    }

    @Override
    public int getValue() {
        return currentValue;
    }

    @Override
    public boolean hasRemaining() {
        return intIndex < endIntIndex;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
