package org.codingmatters.rest.io;

import org.codingmatters.rest.io.content.ByteContent;
import org.codingmatters.rest.io.content.ContentHelper;
import org.codingmatters.rest.io.content.CountedReferenceTemporaryFileContent;
import org.codingmatters.rest.io.content.FileContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public interface Content {
    Logger log = LoggerFactory.getLogger(Content.class);

    static Content from(String content) {
        return new ByteContent(content.getBytes());
    }

    static Content from(byte[] content) {
        return new ByteContent(content);
    }

    static Content from(File file) {
        return new FileContent(file);
    }

    ThreadLocal<List<CountedReferenceTemporaryFile>> temporaryFiles = ThreadLocal.withInitial(LinkedList::new);

    static void closeThreadTemppraryFiles() {
        for (CountedReferenceTemporaryFile temporaryFile : new ArrayList<>(temporaryFiles.get())) {
            try {
                temporaryFile.close();
            } catch (Exception e) {
                log.error("error closing temporary file", e);
            }
        }
        temporaryFiles.get().clear();
    }

    static Content from(InputStream in) {
        try {
            CountedReferenceTemporaryFile temp = CountedReferenceTemporaryFile.create();
            int length = ContentHelper.toFile(in, temp.get());
            return new CountedReferenceTemporaryFileContent(temp, length);
        } catch (IOException e) {
            throw new RuntimeException("failed creating temporary file for content, cannot recover from that", e);
        }
    }

    byte [] asBytes() throws IOException;
    default String asString() throws IOException {
        return new String(this.asBytes());
    }
    InputStream asStream() throws IOException;

    int length();

    default void to(OutputStream out) throws IOException {
        try(InputStream in = this.asStream()) {
            ContentHelper.copyStream(in, out);
        }
    }
}
