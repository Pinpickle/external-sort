package uk.ac.cam.cas217.fjava.tick0.merge;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntegerSourceMergerTest {

    @Test
    public void mergesMultipleSourcesCorrectly() throws IOException {
        IntegerSourceMerger sources = new IntegerSourceMerger(
            Arrays.asList(
                new IntegerSourceMock(10, 15, 100, 142),
                new IntegerSourceMock(9, 16, 112, 115),
                new IntegerSourceMock(77, 83, 1000)
            )
        );

        assertSourcesValue(sources, 9);
        assertSourcesValue(sources, 10);
        assertSourcesValue(sources, 15);
        assertSourcesValue(sources, 16);
        assertSourcesValue(sources, 77);
        assertSourcesValue(sources, 83);
        assertSourcesValue(sources, 100);
        assertSourcesValue(sources, 112);
        assertSourcesValue(sources, 115);
        assertSourcesValue(sources, 142);
        assertSourcesValue(sources, 1000);

        Assert.assertFalse(sources.hasRemaining());
    }

    private void assertSourcesValue(IntegerSourceMerger sources, int value) throws IOException {
        Assert.assertEquals(value, sources.getValue());
        sources.readyyNextIndex();
    }

    private static class IntegerSourceMock implements IntegerSource {
        private List<Integer> toSend;

        IntegerSourceMock(int... ints) {
            toSend = IntStream.of(ints).boxed().collect(Collectors.toList());
        }

        @Override
        public boolean hasRemaining() {
            return !toSend.isEmpty();
        }

        @Override
        public void readyyNextIndex() throws IOException {
            toSend.remove(0);
        }

        @Override
        public int getValue() {
            return toSend.get(0);
        }
    }
}
