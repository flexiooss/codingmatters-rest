package org.codingmatters.rest.api.types;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface FileAsStream {
    byte [] content();

    default InputStream inputStream() {
        return new ByteArrayInputStream(content());
    }

    default String string() {
        return new String(content());
    }
}
