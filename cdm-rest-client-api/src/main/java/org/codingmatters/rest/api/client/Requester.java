package org.codingmatters.rest.api.client;

import java.io.IOException;

public interface Requester {

    ResponseDelegate get() throws IOException;
    ResponseDelegate post(String contentType, byte[] body) throws IOException;
    ResponseDelegate put(String contentType, byte[] body) throws IOException;
    ResponseDelegate patch(String contentType, byte[] body) throws IOException;
    ResponseDelegate delete() throws IOException;
    ResponseDelegate delete(String contentType, byte[] body) throws IOException;

    Requester parameter(String name, String value);
    Requester header(String name, String value);
    Requester path(String path);
}
