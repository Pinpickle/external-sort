package uk.ac.cam.cas217.fjava.tick0.merge;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

/**
 * Merges together multiple {@link IntegerSource}s with the assumption that they are sorted. The resulting integer source
 * that this exposes will also be sorted.
 */
class IntegerSourceMerger implements ReadableByteChannel {
    private final IntegerSource[] sources;
    private int sourcesWithRemaining;
    private boolean isOpen = true;

    /**
     * @param sources - A list of {@link IntegerSource}s. Note these sources are mutated and consumed by this class. Do
     *                not use them after passing them to this class.
     */
    IntegerSourceMerger(List<? extends IntegerSource> sources) {
        this.sources = sources.toArray(new IntegerSource[0]);
        sourcesWithRemaining = this.sources.length;

        buildHeap();
    }

    @Override
    public void close() throws IOException {
        isOpen = false;

        for (IntegerSource source : sources) {
            source.close();
        }
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int bytes = 0;
        while (hasRemaining() && (dst.remaining() >= 4)) {
            dst.putInt(getValue());
            readyNextIndex();
            bytes += 4;
        }

        return bytes;
    }

    private boolean hasRemaining() {
        return sourcesWithRemaining > 0;
    }

    private int getValue() {
        return sources[0].getValue();
    }

    private void readyNextIndex() throws IOException {
        sources[0].readyNextIndex();

        if (!sources[0].hasRemaining()) {
            removeTopOfHeap();
        } else {
            heapifySources(0);
        }
    }

    /**
     * A priority queue backed by a heap is used to keep {@link IntegerSource}s in the correct order. This builds the
     * initial heap.
     */
    private void buildHeap() {
        for (int i = this.sources.length / 2; i >= 0; i --) {
            heapifySources(i);
        }
    }

    private void removeTopOfHeap() {
        IntegerSource topOfHeap = sources[0];

        sources[0] = sources[sourcesWithRemaining - 1];
        sources[sourcesWithRemaining - 1] = topOfHeap;
        sourcesWithRemaining -= 1;

        heapifySources(0);
    }

    private void heapifySources(int startIndex) {
        IntegerSource sourceAtIndex = sources[startIndex];
        int leftChildIndex = getLeftChildIndex(startIndex);
        int rightChildIndex = getRightChildIndex(startIndex);
        int smallestIndex = startIndex;

        if ((leftChildIndex < sourcesWithRemaining) && (sources[smallestIndex].getValue() > sources[leftChildIndex].getValue())) {
            smallestIndex = leftChildIndex;
        }

        if ((rightChildIndex < sourcesWithRemaining) && (sources[smallestIndex].getValue() > sources[rightChildIndex].getValue())) {
            smallestIndex = rightChildIndex;
        }

        if (smallestIndex == startIndex) {
            return;
        }

        sources[startIndex] = sources[smallestIndex];
        sources[smallestIndex] = sourceAtIndex;
        heapifySources(smallestIndex);
    }

    private int getLeftChildIndex(int parentIndex) {
        return parentIndex * 2 + 1;
    }

    private int getRightChildIndex(int parentIndex) {
        return parentIndex * 2 + 2;
    }
}
