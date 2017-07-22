package org.codingmatters.rest.api.client;

import java.io.IOException;

public interface ResponseDelegate {
    int code();

    byte [] body() throws IOException;

    String header(String name);
}
