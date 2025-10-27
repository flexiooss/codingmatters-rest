package org.codingmatters.rest.api.client.test;

import org.codingmatters.rest.api.client.ResponseDelegate;
import org.codingmatters.rest.io.Encodings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if (headers != null) {
            for (Map.Entry<String, String[]> header : headers.entrySet()) {
                this.headers.put(header.getKey().toLowerCase(), header.getValue());
            }
        }

        this.contentType = contentType;
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
    public InputStream bodyStream() throws IOException {
        return new ByteArrayInputStream(this.body);
    }

    @Override
    public String[] header(String name) {
        String[] encodedHeaderValues = this.headers.getOrDefault(name.toLowerCase() + "*", new String[0]);
        String[] headerValues = this.headers.getOrDefault(name.toLowerCase(), new String[0]);
        return headerValues.length == 0 && encodedHeaderValues.length == 0 ? null
                : Stream.concat(Arrays.stream(headerValues), Arrays.stream(encodedHeaderValues).map(this::decodeValue)).toArray(String[]::new);
    }

    private String decodeValue(String value) {
        String[] parts = value.split("'");
        if (parts.length == 3) {
            try {
                return Encodings.Url.decode(parts[2], Encodings.CharSet.from(parts[0]));
            } catch (Encodings.CharSet.NoSuchCharsetException e) {
                return value;
            }
        } else {
            return value;
        }
    }

    @Override
    public String[] headerNames() {
        return this.headers.keySet()
                .stream()
                .map(name -> {
                    if (name.endsWith("*")) {
                        return name.substring(0, name.length() - 1);
                    }
                    return name;
                })
                .distinct()
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }

    @Override
    public String[] rawHeaderNames() {
        return this.headers.keySet().toArray(new String[this.headers.size()]);
    }

    @Override
    public String contentType() {
        return this.contentType;
    }
}
