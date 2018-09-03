package org.codingmatters.rest.io;

import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CountedReferenceTemporaryFilePathTest {
    @Test
    public void givenNoTempDirPathSetted__whenFileCreated__thenCreatedInSystemTempDir() throws Exception {
        File f = CountedReferenceTemporaryFile.create().get();

        assertTrue(f.exists());
        assertThat(f.getParentFile().getAbsolutePath(), is(System.getProperty("java.io.tmpdir")));
    }

    @Test
    public void givenTempDirPathSetted__whenFileCreated__thenCreatedInTempPath() throws Exception {
        String previous = System.getProperty(CountedReferenceTemporaryFile.PATH_PROP);
        try {
            String tempPath = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID().toString();
            System.setProperty(CountedReferenceTemporaryFile.PATH_PROP, tempPath);
            File f = CountedReferenceTemporaryFile.create().get();

            assertTrue(f.exists());
            assertThat(f.getParentFile().getAbsolutePath(), is(tempPath));
        } finally {
            if(previous == null) {
                System.clearProperty(CountedReferenceTemporaryFile.PATH_PROP);
            } else {
                System.setProperty(CountedReferenceTemporaryFile.PATH_PROP, previous);
            }
        }
    }

}
