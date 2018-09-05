package org.codingmatters.rest.api.types;

import org.codingmatters.rest.io.Content;

import java.io.IOException;
import java.io.InputStream;

public interface FileAsStream {
    Content content();

    default InputStream inputStream() throws IOException {
        return this.content().asStream();
    }

    default String string() throws IOException {
        return new String(content().asBytes());
    }
}
