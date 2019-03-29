package org.codingmatters.rest.io.content;

import org.codingmatters.rest.io.Content;
import org.codingmatters.rest.io.CountedReferenceTemporaryFile;

import java.io.IOException;
import java.io.InputStream;

public class CountedReferenceTemporaryFileContent implements Content {
    private final CountedReferenceTemporaryFile temporaryFile;
    private final int length;

    public CountedReferenceTemporaryFileContent(CountedReferenceTemporaryFile temporaryFile, int length) {
        this.temporaryFile = temporaryFile;

        this.length = length;
    }

    @Override
    public byte[] asBytes() throws IOException {
        try(InputStream in = this.asStream()) {
            return ContentHelper.bytes(in);
        }
    }

    @Override
    public InputStream asStream() throws IOException {
        return this.temporaryFile.inputStream();
    }

    @Override
    public int length() {
        return this.length;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.temporaryFile.close();
    }
}
