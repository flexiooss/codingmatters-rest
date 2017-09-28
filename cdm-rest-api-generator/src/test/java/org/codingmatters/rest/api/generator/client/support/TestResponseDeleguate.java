package org.codingmatters.rest.api.generator.client.support;

import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.util.Map;

public class TestResponseDeleguate implements ResponseDelegate {
    private final int code;
    private final byte[] body;
    private final Map<String, String[]> headers;

    public TestResponseDeleguate(int code, byte[] body, Map<String, String[]> headers) {
        this.code = code;
        this.body = body;
        this.headers = headers;
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
}
