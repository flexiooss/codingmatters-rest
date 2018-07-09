package org.codingmatters.rest.api.client;

import java.io.IOException;

public interface ResponseDelegate extends AutoCloseable {
    int code();
    byte [] body() throws IOException;
    String [] header(String name);
    String [] headerNames();
    String contentType();

    @Override
    default void close() throws Exception {}
}
