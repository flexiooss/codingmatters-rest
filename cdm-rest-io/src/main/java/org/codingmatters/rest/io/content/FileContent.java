package org.codingmatters.rest.io.content;

import org.codingmatters.rest.io.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class FileContent implements Content {

    static private final Logger log = LoggerFactory.getLogger(FileContent.class);

    private final File file;
    private AtomicInteger length = new AtomicInteger(-1);

    public FileContent(File file) {
        this.file = file;
    }

    @Override
    public byte[] asBytes() throws IOException {
        try(InputStream in = this.asStream()) {
            return ContentHelper.bytes(in);
        }
    }

    @Override
    public InputStream asStream() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override
    public int length() {
        if(this.length.get() == -1) {
            try(InputStream in = this.asStream()) {
                int l = 0;
                byte[] buffer = new byte[1024];
                for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                    l += read;
                }
                this.length.set(l);
            } catch (IOException e) {
                log.error("failed to calculate file content length", e);
                return -1;
            }
        }
        return this.length.get();
    }
}
