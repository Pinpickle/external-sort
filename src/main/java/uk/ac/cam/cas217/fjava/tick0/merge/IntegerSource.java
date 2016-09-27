package uk.ac.cam.cas217.fjava.tick0.merge;

import java.io.IOException;

/**
 * Represents a potentially finite stream of integers where the next value has to be requested manually
 */
interface IntegerSource {
    void readyyNextIndex() throws IOException;
    int getValue();
    boolean hasRemaining();
}
