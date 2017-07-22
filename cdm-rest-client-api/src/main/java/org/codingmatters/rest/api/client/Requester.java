package org.codingmatters.rest.api.client;

import java.io.IOException;
import java.util.TreeMap;

public abstract class Requester {

    public abstract ResponseDelegate get() throws IOException;
    public abstract ResponseDelegate post() throws IOException;
    public abstract ResponseDelegate put() throws IOException;
    public abstract ResponseDelegate patch() throws IOException;
    public abstract ResponseDelegate delete() throws IOException;

    private String path = "/";
    private final TreeMap<String, String> queryParameters = new TreeMap<>();

    public Requester queryParameter(String name, String value) {
        this.queryParameters.put(name, value);
        return this;
    }

    public Requester path(String path) {
        this.path = path;
        return this;
    }

    protected String path() {
        return path;
    }

    protected TreeMap<String, String> queryParameters() {
        return queryParameters;
    }
}
