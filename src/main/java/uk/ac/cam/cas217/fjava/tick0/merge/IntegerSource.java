package uk.ac.cam.cas217.fjava.tick0.merge;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents a potentially finite stream of integers where the next value has to be requested manually
 */
interface IntegerSource extends Closeable {
    void readyNextIndex() throws IOException;
    int getValue();
    boolean hasRemaining();
}
