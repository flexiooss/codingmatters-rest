package org.codingmatters.rest.tests.api;

import org.codingmatters.rest.api.ResponseDelegate;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

public class TestResponseDeleguate implements ResponseDelegate {
    private String contentType;
    private int code;
    private Map<String, String[]> headers = new TreeMap<>();
    private String charset;
    private byte[] payload;

    @Override
    public ResponseDelegate contenType(String contenType) {
        this.contentType = contenType;
        return this;
    }

    @Override
    public ResponseDelegate status(int code) {
        this.code = code;
        return this;
    }

    @Override
    public ResponseDelegate addHeader(String name, String ... value) {
        this.headers.put(name, value);
        return this;
    }

    @Override
    public ResponseDelegate payload(String payload, String charset) {
        this.charset = charset;
        try {
            this.payload = payload.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("unsupported charset : " + charset, e);
        }
        return this;
    }

    @Override
    public ResponseDelegate payload(byte[] bytes) {
        this.payload = bytes;
        return this;
    }

    public String contentType() {
        return contentType;
    }

    public int status() {
        return code;
    }

    public Map<String, String[]> headers() {
        return headers;
    }

    public String charset() {
        return charset;
    }

    public byte[] payload() {
        return payload;
    }
}
