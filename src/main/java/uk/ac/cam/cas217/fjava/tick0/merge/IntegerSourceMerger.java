package uk.ac.cam.cas217.fjava.tick0.merge;

import java.io.IOException;
import java.util.List;

/**
 * Merges together multiple {@link IntegerSource}s with the assumption that they are sorted. The resulting integer source
 * that this exposes will also be sorted.
 */
class IntegerSourceMerger implements IntegerSource {
    private final IntegerSource[] sources;
    private int sourcesWithRemaining;

    IntegerSourceMerger(List<? extends IntegerSource> sources) {
        this.sources = sources.toArray(new IntegerSource[0]);
        sourcesWithRemaining = this.sources.length;

        buildHeap();
    }

    public boolean hasRemaining() {
        return sourcesWithRemaining > 0;
    }

    public int getValue() {
        return sources[0].getValue();
    }

    @Override
    public void readyyNextIndex() throws IOException {
        sources[0].readyyNextIndex();

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
