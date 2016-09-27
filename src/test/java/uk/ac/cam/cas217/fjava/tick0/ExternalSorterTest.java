package uk.ac.cam.cas217.fjava.tick0;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertEquals;

/**
 * Automated test-suite for ExternalSorter
 *
 * Note that this is using the forbidden nio apis, but is only used when testing
 */
public class ExternalSorterTest {
    private final String[] checksums = {
        "d41d8cd98f0b24e980998ecf8427e",
        "a54f041a9e15b5f25c463f1db7449",
        "c2cb56f4c5bf656faca0986e7eba38",
        "c1fa1f22fa36d331be4027e683baad6",
        "8d79cbc9a4ecdde112fc91ba625b13c2",
        "1e52ef3b2acef1f831f728dc2d16174d",
        "6b15b255d36ae9c85ccd3475ec11c3",
        "1484c15a27e48931297fb6682ff625",
        "ad4f60f065174cf4f8b15cbb1b17a1bd",
        "32446e5dd58ed5a5d7df2522f0240",
        "435fe88036417d686ad8772c86622ab",
        "c4dacdbc3c2e8ddbb94aac3115e25aa2",
        "3d5293e89244d513abdf94be643c630",
        "468c1c2b4c1b74ddd44ce2ce775fb35c",
        "79d830e4c0efa93801b5d89437f9f3e",
        "c7477d400c36fca5414e0674863ba91",
        "cc80f01b7d2d26042f3286bdeff0d9",
        "cc80f01b7d2d26042f3286bdeff0d9"
    };
    private static final String testSourcePath = "./test-suite/testSource.dat";
    private static final String testTempPath = "./test-suite/testTemp.dat";

    private Path getSourcePath(int index) {
        return Paths.get(String.format("./test-suite/test%sa.dat", index));
    }

    private Path getTempPath(int index) {
        return Paths.get(String.format("./test-suite/test%sb.dat", index));
    }

    private String sortFileAndGetChecksum(int index) throws IOException {
        Files.copy(getSourcePath(index + 1), Paths.get(testSourcePath), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(getTempPath(index + 1), Paths.get(testTempPath), StandardCopyOption.REPLACE_EXISTING);


        System.out.println(String.format("==== Sorting %s", index));
        long startTime = System.nanoTime();
        new ExternalSorter(testSourcePath, testTempPath).sort();
        long endTime = System.nanoTime();

        System.out.println(String.format("= Sorted in %d ms", (endTime - startTime) / 1000000));



        System.gc();

        System.out.println(String.format("= Data File Checksum: %s", ExternalSort.checkSum(testSourcePath)));
        System.out.println(String.format("= Temp File Checksum: %s", ExternalSort.checkSum(testTempPath)));

        return ExternalSort.checkSum(testSourcePath);
    }

    @Test
    public void testSort() throws IOException {
        //System.out.println(ExternalSort.checkSum("./test-suite/test18a.dat"));

        for (int index = 0; index < checksums.length; index ++) {
            assertEquals(
                checksums[index],
                sortFileAndGetChecksum(index)
            );
        }

    }
}
