package org.codingmatters.rest.api.client;

import java.io.IOException;
import java.util.TreeMap;

public abstract class Requester {

    public abstract ResponseDelegate get() throws IOException;
    public abstract ResponseDelegate post(String contentType, byte[] body) throws IOException;
    public abstract ResponseDelegate put(String contentType, byte[] body) throws IOException;
    public abstract ResponseDelegate patch(String contentType, byte[] body) throws IOException;
    public abstract ResponseDelegate delete() throws IOException;
    public abstract ResponseDelegate delete(String contentType, byte[] body) throws IOException;

    private String path = "/";
    private final TreeMap<String, String> queryParameters = new TreeMap<>();
    private final TreeMap<String, String> headers = new TreeMap<>();

    public Requester parameter(String name, String value) {
        this.queryParameters.put(name, value);
        return this;
    }

    public Requester header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public Requester path(String path) {
        this.path = path;
        return this;
    }

    protected String path() {
        return path;
    }

    protected TreeMap<String, String> parameters() {
        return queryParameters;
    }
    protected TreeMap<String, String> headers() {
        return headers;
    }
}
