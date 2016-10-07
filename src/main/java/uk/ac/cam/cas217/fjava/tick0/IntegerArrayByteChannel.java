package uk.ac.cam.cas217.fjava.tick0;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * An array of integers that exposes readable and writable byte channel interfaces
 */
public class IntegerArrayByteChannel implements ReadableByteChannel, WritableByteChannel {
    private boolean open = true;
    private final int[] integers;
    private int readPosition;
    private int writePosition;

    /**
     *
     * @param integerCount - The total amount of integers to be stored in this array. The behaviour for trying to read/write
     *                     more integers than this is undefined.
     */
    public IntegerArrayByteChannel(int integerCount) {
        integers = new int[integerCount];
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void close() throws IOException {
        open = false;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int bytesRead = 0;

        while (dst.remaining() >= 4) {
            dst.putInt(integers[readPosition]);
            readPosition ++;
            bytesRead += 4;
        }

        return bytesRead;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        int bytesWritten = 0;

        while (src.remaining() >= 4) {
            integers[writePosition] = src.getInt();
            writePosition ++;
            bytesWritten += 4;
        }

        return bytesWritten;
    }

    /**
     * The integers read/written to. Feel free to mutate this array.
     */
    public int[] getIntegers() {
        return integers;
    }

    /**
     * Resets the internal read and write indices to allow the read and write methods to start fresh
     */
    public void resetReadWrite() {
        readPosition = 0;
        writePosition = 0;
    }
}
