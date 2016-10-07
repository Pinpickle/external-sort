package uk.ac.cam.cas217.fjava.tick0.counting;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Implements counting sort while exposing writeable and readable byte channel interfaces
 */
class CountingSorter implements WritableByteChannel, ReadableByteChannel {
    private final int offset;
    private final int count;

    private boolean open = true;
    private final long[] intCounts;
    private int intPosition;

    CountingSorter(int offset, int count) {
        this.offset = offset;
        this.count = count;
        intCounts = new long[count];
        intPosition = offset;
    }

    @Override
    public void close() throws IOException {
        open = false;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        int bytesWritten = 0;
        while (src.remaining() >= 4) {
            intCounts[src.getInt() - offset]++;
            bytesWritten += 4;
        }

        return bytesWritten;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int bytesRead = 0;

        while ((dst.remaining() >= 4) && (intPosition - offset < count)) {
            if (intCounts[intPosition - offset] == 0) {
                intPosition += 1;
                continue;
            }

            dst.putInt(intPosition);
            intCounts[intPosition - offset]--;
            bytesRead += 4;
        }

        return bytesRead;
    }
}
