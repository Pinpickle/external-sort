package uk.ac.cam.cas217.fjava.tick0.merge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class MergeSourceList {
    List<MergeSource> sources;

    MergeSourceList(List<MergeSource> sources) {
        this.sources = new ArrayList<>(sources);
    }

    boolean hasRemaining() {
        return !sources.isEmpty();
    }

    int getNextValue() throws IOException {
        MergeSource smallestSource = null;
        boolean mergeFound = false;
        for (MergeSource source : sources) {
            if (source.hasRemaining() &&
                ((!mergeFound) || (source.getValue() < smallestSource.getValue()))) {
                smallestSource = source;
                mergeFound = true;
            }
        }

        sources.remove(smallestSource);
        sources.add(0, smallestSource);

        MergeSource smallestSourcez = sources.get(0);

        int val = smallestSourcez.getValue();
        smallestSourcez.increaseIndex();

        if (!smallestSourcez.hasRemaining()) {
            sources.remove(smallestSourcez);
        }
        return val;
    }
}
