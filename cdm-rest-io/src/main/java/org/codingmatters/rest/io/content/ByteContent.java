package org.codingmatters.rest.io.content;

import org.codingmatters.rest.io.Content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteContent implements Content {
    private final byte [] content;

    public ByteContent(byte[] content) {
        this.content = content;
    }

    @Override
    public byte[] asBytes() throws IOException {
        return this.content;
    }

    @Override
    public InputStream asStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public int length() {
        return this.content != null ? this.content.length : 0;
    }
}
