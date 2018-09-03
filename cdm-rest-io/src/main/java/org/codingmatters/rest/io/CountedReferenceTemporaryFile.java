package org.codingmatters.rest.io;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class CountedReferenceTemporaryFile implements AutoCloseable {

    static public final String PATH_PROP = CountedReferenceTemporaryFile.class.getName() + ".path";

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final File file;
    private final ReferenceCounter referenceCounter;

    public static CountedReferenceTemporaryFile create() throws IOException {
        File file = new File(tmpDir(), UUID.randomUUID().toString());
        file.getParentFile().mkdirs();
        file.createNewFile();
        file.deleteOnExit();
        ReferenceCounter referenceCounter = new ReferenceCounter(file);
        referenceCounter.increment();
        return new CountedReferenceTemporaryFile(file, referenceCounter);
    }

    private static File tmpDir() {
        return new File(System.getProperty(PATH_PROP, System.getProperty("java.io.tmpdir")));
    }


    private CountedReferenceTemporaryFile(File file, ReferenceCounter referenceCounter) {
        this.file = file;
        this.referenceCounter = referenceCounter;
    }

    public File get() {
        return this.file;
    }

    @Override
    public void close() throws Exception {
        if(! this.closed.getAndSet(true)) {
            this.referenceCounter.decrement();
        }
    }

    public InputStream inputStream() throws FileNotFoundException {
        this.referenceCounter.increment();
        return new CountedReferenceInputStream(this.file, this.referenceCounter);
    }

    public OutputStream outputStream() throws FileNotFoundException {
        this.referenceCounter.increment();
        return new CountedReferenceOutputStream(this.file, this.referenceCounter);
    }

    public static class CountedReferenceInputStream extends FileInputStream {
        private final AtomicBoolean closed = new AtomicBoolean(false);
        private final ReferenceCounter referenceCounter;

        private CountedReferenceInputStream(File file, ReferenceCounter referenceCounter) throws FileNotFoundException {
            super(file);
            this.referenceCounter = referenceCounter;
        }

        @Override
        public void close() throws IOException {
            if(! this.closed.getAndSet(true)) {
                this.referenceCounter.decrement();
            }
            super.close();
        }
    }

    static public class CountedReferenceOutputStream extends FileOutputStream {
        private final AtomicBoolean closed = new AtomicBoolean(false);
        private final ReferenceCounter referenceCounter;

        private CountedReferenceOutputStream(File file, ReferenceCounter referenceCounter) throws FileNotFoundException {
            super(file);
            this.referenceCounter = referenceCounter;
        }

        @Override
        public void close() throws IOException {
            if(! this.closed.getAndSet(true)) {
                this.referenceCounter.decrement();
            }
            super.close();
        }
    }
}
