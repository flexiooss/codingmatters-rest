package org.codingmatters.rest.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CountedReferenceTemporaryFileTest {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();
    private String previousPathProp;
    private CountedReferenceTemporaryFile tempFile;

    @Before
    public void setUp() throws Exception {
        this.previousPathProp = System.getProperty(CountedReferenceTemporaryFile.PATH_PROP);
        System.setProperty(CountedReferenceTemporaryFile.PATH_PROP, this.tmpDir.getRoot().getAbsolutePath());

        this.tempFile = CountedReferenceTemporaryFile.create();
    }

    @After
    public void tearDown() throws Exception {
        if(this.previousPathProp == null) {
            System.clearProperty(CountedReferenceTemporaryFile.PATH_PROP);
        } else {
            System.setProperty(CountedReferenceTemporaryFile.PATH_PROP, this.previousPathProp);
        }

        this.tempFile.close();
    }

    @Test
    public void givenNoReferenceCreated__whenClosed__thenFileIsDeleted() throws Exception {
        CountedReferenceTemporaryFile tmp = CountedReferenceTemporaryFile.create();
        File f = tmp.get();

        tmp.close();
        assertFalse(f.exists());
    }

    @Test
    public void givenAnInputStreamCreatedIsNotClosed__whenClosed__thenFileIsNotDeleted() throws Exception {
        CountedReferenceTemporaryFile tmp = CountedReferenceTemporaryFile.create();
        File f = tmp.get();
        try(InputStream in = tmp.inputStream()) {
            tmp.close();
            assertTrue(f.exists());
        }
    }


    @Test
    public void givenAnInputStreamCreatedAndReferenceClosed__whenClosed__thenFileIsDeleted() throws Exception {
        CountedReferenceTemporaryFile tmp = CountedReferenceTemporaryFile.create();
        File f = tmp.get();

        InputStream in = tmp.inputStream();
        in.close();

        tmp.close();

        assertFalse(f.exists());
    }

    @Test
    public void givenAnInputStreamCreatedAndTempFileIsClosed__whenReferenceIsClosed__thenFileIsDeleted() throws Exception {
        CountedReferenceTemporaryFile tmp = CountedReferenceTemporaryFile.create();
        File f = tmp.get();
        InputStream in = tmp.inputStream();
        tmp.close();

        in.close();

        assertFalse(f.exists());
    }





    @Test
    public void givenAnOutputStreamCreatedAndTempFileIsClosed__whenOutputStreamIsClosed__thenFileIsDeleted() throws Exception {
        CountedReferenceTemporaryFile tmp = CountedReferenceTemporaryFile.create();
        File f = tmp.get();
        OutputStream out = tmp.outputStream();
        tmp.close();

        out.close();

        assertFalse(f.exists());
    }



    @Test
    public void givenAnOutputStreamCreatedIsNotClosed__whenClosed__thenFileIsNotDeleted() throws Exception {
        CountedReferenceTemporaryFile tmp = CountedReferenceTemporaryFile.create();
        File f = tmp.get();
        try(OutputStream out = tmp.outputStream()) {
            tmp.close();
            assertTrue(f.exists());
        }
    }

    @Test
    public void givenAnOutputStreamCreatedAndOutputStreamClosed__whenClosed__thenFileIsDeleted() throws Exception {
        CountedReferenceTemporaryFile tmp = CountedReferenceTemporaryFile.create();
        File f = tmp.get();

        OutputStream out = tmp.outputStream();
        out.close();

        tmp.close();

        assertFalse(f.exists());
    }
}