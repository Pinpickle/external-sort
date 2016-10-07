package uk.ac.cam.cas217.fjava.tick0.counting;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.StandardOpenOption;

/**
 * Calculates the minimum and maximum integer in a file
 */
public class IntegerFileRangeFinder {
    private final File file;

    public IntegerFileRangeFinder(File file) {
        this.file = file;

        if (this.file.length() < 8) {
            throw new IllegalArgumentException("File must contain at least two integers to determine range");
        }
    }

    public IntegerFileRange getIntegerRange() throws IOException {
        IntegerFileRangeWriter rangeWriter = new IntegerFileRangeWriter();
        FileChannel fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ);

        fileChannel.transferTo(0, fileChannel.size(), rangeWriter);
        IntegerFileRange fileRange = rangeWriter.getRange();

        rangeWriter.close();
        fileChannel.close();

        return fileRange;
    }

    private static class IntegerFileRangeWriter implements WritableByteChannel {
        private boolean open = true;
        int minimum = Integer.MAX_VALUE;
        int maximum = Integer.MIN_VALUE;

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
            int bytesRead = 0;
            while (src.remaining() >= 4) {
                int nextValue = src.getInt();
                bytesRead += 4;

                maximum = Math.max(maximum, nextValue);
                minimum = Math.min(minimum, nextValue);
            }

            return bytesRead;
        }

        public IntegerFileRange getRange() {
            return new IntegerFileRange(minimum, maximum);
        }
    }

    public static class IntegerFileRange {
        private final int minimum;
        private final int maximum;

        private IntegerFileRange(int minimum, int maximum) {
            this.minimum = minimum;
            this.maximum = maximum;
        }

        public int getMaximum() {
            return maximum;
        }

        public int getMinimum() {
            return minimum;
        }

        public long getRange() {
            return (long) maximum - (long) minimum;
        }
    }
}
