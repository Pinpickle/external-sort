package uk.ac.cam.cas217.fjava.tick0;

import java.io.*;

/**
 * Represents a single pass from one file (A), to another (B), where the results in B is some partially sorted form
 * of the contents of A
 */
abstract public class ExternalSortPass {
    protected final RandomAccessFile sourceAccessFile;
    protected final File sourceFile;
    protected final File destinationFile;
    protected final DataOutputStream destinationStream;
    protected final long intsInFile;

    public ExternalSortPass(File sourceFile, File destinationFile) throws IOException {
        this.sourceFile = sourceFile;
        this.destinationFile = destinationFile;
        sourceAccessFile = new RandomAccessFile(sourceFile, "r");
        destinationStream = createDataOutputStream(destinationFile);
        intsInFile = sourceFile.length() / 4;
    }

    public ExternalSortPass(File sourceFile, File destinationFile, boolean reversed) throws IOException {
        this(reversed ? destinationFile : sourceFile, reversed ? sourceFile : destinationFile);
    }

    public abstract void performSortPass() throws IOException;

    private DataOutputStream createDataOutputStream(File file) throws IOException {
        return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
    }

    public static int intFromFileAtPos(RandomAccessFile file, long position) throws IOException {
        file.seek(position * 4);
        return file.readInt();
    }
}
