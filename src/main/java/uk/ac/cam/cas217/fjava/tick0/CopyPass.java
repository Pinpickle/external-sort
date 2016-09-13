package uk.ac.cam.cas217.fjava.tick0;

import java.io.*;
import java.nio.file.Files;

/**
 * Created by christian on 12/09/16.
 */
public class CopyPass extends ExternalSortPass {
    CopyPass(File sourceFile, File destinationFile, boolean reversed) throws IOException {
        super(sourceFile, destinationFile, reversed);
    }

    @Override
    public void performSortPass() throws IOException {
        System.out.println("Copying File");
        InputStream from = new FileInputStream(sourceFile);
        OutputStream to = new FileOutputStream(destinationFile);

        byte[] buf = new byte[8192];
        int len;
        while ((len = from.read(buf)) > 0) {
            to.write(buf, 0, len);
        }
    }
}
