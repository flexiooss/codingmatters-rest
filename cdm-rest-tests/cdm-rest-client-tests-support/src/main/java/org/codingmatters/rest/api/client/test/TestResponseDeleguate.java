package org.codingmatters.rest.api.client.test;

import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.util.Map;

public class TestResponseDeleguate implements ResponseDelegate {
    private final int code;
    private final byte[] body;
    private final Map<String, String[]> headers;
    private final String contentType;

    public TestResponseDeleguate(int code, byte[] body, Map<String, String[]> headers) {
        this(code, body, headers, null);
    }
    public TestResponseDeleguate(int code, byte[] body, Map<String, String[]> headers, String contentType) {
        this.code = code;
        this.body = body;
        this.headers = headers;
        this.contentType=  contentType;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public byte[] body() throws IOException {
        return this.body;
    }

    @Override
    public String[] header(String name) {
        return this.headers.get(name);
    }

    @Override
    public String[] headerNames() {
        return this.headers.keySet().toArray(new String[this.headers.size()]);
    }

    @Override
    public String contentType() {
        return this.contentType;
    }
}
