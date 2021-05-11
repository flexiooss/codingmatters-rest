package org.codingmatters.rest.io;

import org.codingmatters.rest.io.content.ByteContent;
import org.codingmatters.rest.io.content.ContentHelper;
import org.codingmatters.rest.io.content.CountedReferenceTemporaryFileContent;
import org.codingmatters.rest.io.content.FileContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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

    static Content from(InputStream in) throws IOException {
        CountedReferenceTemporaryFile temp = CountedReferenceTemporaryFile.create();
        int length = ContentHelper.toFile(in, temp.get());
        return new CountedReferenceTemporaryFileContent(temp, length);
    }

    byte [] asBytes() throws IOException;
    default String asString() throws IOException {
        return new String(this.asBytes());
    }
    InputStream asStream() throws IOException;

    default File asTemporaryFile() throws IOException {
        File tempDir = new File(System.getProperty(Content.class.getName() + ".temporary.path", System.getProperty("java.io.tmpdir")));
        tempDir.mkdirs();
        File temp = File.createTempFile("content", ".bin", tempDir);
        try(InputStream in = this.asStream() ; OutputStream out = new FileOutputStream(temp)) {
            byte[] buffer = new byte[1024];
            for (int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                out.write(buffer, 0, read);
            }
        }
        return temp;
    }

    int length();

    default void to(OutputStream out) throws IOException {
        try(InputStream in = this.asStream()) {
            ContentHelper.copyStream(in, out);
        }
    }
}
