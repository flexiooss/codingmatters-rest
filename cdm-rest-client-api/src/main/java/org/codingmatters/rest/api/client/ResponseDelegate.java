package org.codingmatters.rest.api.client;

import java.io.IOException;
import java.io.InputStream;

public interface ResponseDelegate extends AutoCloseable {
    int code();

    byte [] body() throws IOException;
    InputStream bodyStream() throws IOException;

    String [] header(String name);
    String [] headerNames();
    String [] rawHeaderNames();
    String contentType();

    @Override
    default void close() throws Exception {}
}
