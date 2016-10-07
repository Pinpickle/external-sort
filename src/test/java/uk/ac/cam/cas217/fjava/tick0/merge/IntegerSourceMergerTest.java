package uk.ac.cam.cas217.fjava.tick0.merge;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
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

        ByteBuffer buffer = ByteBuffer.allocate(400);

        sources.read(buffer);
        buffer.flip();

        IntBuffer intBuffer = buffer.asIntBuffer();
        int[] ints = new int[intBuffer.limit()];
        intBuffer.get(ints);

        Assert.assertArrayEquals(
            new int[] {9, 10, 15, 16, 77, 83, 100, 112, 115, 142, 1000},
            ints
        );
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
        public void readyNextIndex() throws IOException {
            toSend.remove(0);
        }

        @Override
        public int getValue() {
            return toSend.get(0);
        }

        @Override
        public void close() throws IOException { }
    }
}
