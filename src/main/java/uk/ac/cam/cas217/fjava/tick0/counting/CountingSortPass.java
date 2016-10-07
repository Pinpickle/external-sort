package uk.ac.cam.cas217.fjava.tick0.counting;

import uk.ac.cam.cas217.fjava.tick0.ExternalSortPass;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * Sorts a file entirely by performing counting sort.
 */
public class CountingSortPass extends ExternalSortPass {
    private final int offset;
    private final int count;
    public CountingSortPass(File file, int offset, int count) throws IOException {
        super(file, file);
        this.offset = offset;
        this.count = count;
    }

    @Override
    public void performSortPass() throws IOException {
        System.out.println("Performing counting sort");
        CountingSorter sorter = new CountingSorter(offset, count);

        FileChannel sourceChannel = FileChannel.open(sourceFile.toPath(), StandardOpenOption.READ);
        sourceChannel.transferTo(0, sourceChannel.size(), sorter);
        sourceChannel.close();

        FileChannel destinationChannel = FileChannel.open(destinationFile.toPath(), StandardOpenOption.WRITE);
        destinationChannel.transferFrom(sorter, 0, destinationChannel.size());
        destinationChannel.close();

        sorter.close();
    }

}
