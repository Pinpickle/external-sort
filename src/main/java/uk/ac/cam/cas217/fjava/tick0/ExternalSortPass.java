package uk.ac.cam.cas217.fjava.tick0;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Represents a single pass from one file (A), to another (B), where the results in B is some partially sorted form
 * of the contents of A
 */
abstract public class ExternalSortPass {
    protected final File sourceFile;
    protected final File destinationFile;
    protected final long intsInFile;

    public ExternalSortPass(File sourceFile, File destinationFile) throws IOException {
        this.sourceFile = sourceFile;
        this.destinationFile = destinationFile;
        intsInFile = sourceFile.length() / 4;
    }

    public abstract void performSortPass() throws IOException;

    protected DataOutputStream createDataOutputStream(File file) throws IOException {
        return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
    }
}
