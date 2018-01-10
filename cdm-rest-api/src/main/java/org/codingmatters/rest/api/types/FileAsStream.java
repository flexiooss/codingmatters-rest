package org.codingmatters.rest.api.types;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface FileAsStream {
    byte [] content();

    default InputStream asStream() {
        return new ByteArrayInputStream(content());
    }

    default String asString() {
        return new String(content());
    }
}
