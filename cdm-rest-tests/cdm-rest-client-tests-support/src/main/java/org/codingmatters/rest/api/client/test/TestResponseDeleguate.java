package org.codingmatters.rest.api.client.test;

import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

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

        this.headers = new TreeMap<>();
        if(headers != null) {
            for (Map.Entry<String, String[]> header : headers.entrySet()) {
                this.headers.put(header.getKey().toLowerCase(), header.getValue());
            }
        }

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
        return this.headers.get(name.toLowerCase());
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
