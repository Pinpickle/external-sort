package uk.ac.cam.cas217.fjava.tick0.merge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class IntegerSourceMerger {
    private final IntegerSource[] sources;
    private int sourcesWithRemaining;

    IntegerSourceMerger(List<? extends IntegerSource> sources) {
        this.sources = sources.toArray(new IntegerSource[0]);
        sourcesWithRemaining = this.sources.length;

        buildHeap();
    }

    boolean hasRemaining() {
        return sourcesWithRemaining > 0;
    }

    int getNextValue() throws IOException {
        IntegerSource smallestSource = sources[0];

        int smallestValue = sources[0].getValue();
        sources[0].increaseIndex();

        if (!smallestSource.hasRemaining()) {
            sources[0] = sources[sourcesWithRemaining - 1];
            sources[sourcesWithRemaining - 1] = smallestSource;
            sourcesWithRemaining -= 1;
        }

        if (hasRemaining()) {
            heapifySources(0);
        }

        return smallestValue;
    }

    private void buildHeap() {
        for (int i = this.sources.length - 1; i >= 0; i --) {
            heapifySources(i);
        }
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
