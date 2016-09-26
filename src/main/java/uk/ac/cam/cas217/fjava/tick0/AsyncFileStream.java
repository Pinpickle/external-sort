package uk.ac.cam.cas217.fjava.tick0;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Represents a stream of integers from a portion of a file. Buffer are read into asynchronously with a, but this class
 * has a completely synchronous interface.
 */
public class AsyncFileStream implements Closeable {
    private static final int BUFFER_SIZE = 8192 * 2;

    private final ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private final ByteBuffer loadingByteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private Future<Integer> bytesReadFuture = null;
    private final AsynchronousFileChannel fileChannel;
    private long nextFileOffset;
    private final long finalByteBosition;
    private long index = 0;

    public AsyncFileStream(File source, long startPosition, long endPosition) throws IOException {
        fileChannel = AsynchronousFileChannel.open(source.toPath(), StandardOpenOption.READ);
        nextFileOffset = startPosition * 4;
        byteBuffer.position(byteBuffer.limit());
        finalByteBosition = endPosition * 4;
        loadBytes();
    }

    /**
     * Get the next integer from the file
     */
    public int readInt() throws IOException {
        if (!byteBuffer.hasRemaining()) {
            readBytes();
        }

        if (index >= finalByteBosition) {
            throw new IOException("Reached end of stream");
        }

        index += 4;
        return byteBuffer.getInt();
    }

    private void readBytes() throws IOException {
        waitForBytesToLoad();
        byteBuffer.clear();
        byteBuffer.put(loadingByteBuffer);
        byteBuffer.flip();
        loadBytes();
    }

    private void loadBytes() {
        if (nextFileOffset < finalByteBosition) {
            loadingByteBuffer.clear();
            bytesReadFuture = fileChannel.read(loadingByteBuffer, nextFileOffset);
        }
    }

    private void waitForBytesToLoad() {
        try {
            nextFileOffset += bytesReadFuture.get();
        } catch (InterruptedException | ExecutionException exception) {
            throw new RuntimeException(exception);
        } finally {
            bytesReadFuture = null;
        }
        loadingByteBuffer.flip();
    }

    @Override
    public void close() throws IOException {
        if (bytesReadFuture != null) {
            bytesReadFuture.cancel(true);
        }
        fileChannel.close();
    }
}
