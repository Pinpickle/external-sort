package uk.ac.cam.cas217.fjava.tick0;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static DataOutputStream createDataOutputStream(File file) throws IOException {
        return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
    }
}
