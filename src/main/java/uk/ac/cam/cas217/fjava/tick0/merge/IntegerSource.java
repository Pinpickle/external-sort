package uk.ac.cam.cas217.fjava.tick0.merge;

import java.io.IOException;

public interface IntegerSource {
    void increaseIndex() throws IOException;
    int getValue();
    boolean hasRemaining();
}
