package org.codingmatters.rest.api.generator.client.support;

import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.codingmatters.rest.api.generator.client.support.TestRequesterFactory.Method.*;


public class TestRequester implements Requester {
    private final TestRequesterFactory factory;

    private final String url;
    private String path;
    private HashMap<String, String> parameters = new HashMap();
    private HashMap<String, String> headers = new HashMap<>();


    public TestRequester(String url, TestRequesterFactory factory) {
        this.url = url;
        this.factory = factory;
    }

    @Override
    public ResponseDelegate get() throws IOException {
        return this.nextResponse(GET, null, null);
    }

    @Override
    public ResponseDelegate post(String contentType, byte[] body) throws IOException {
        return this.nextResponse(POST, contentType, body);
    }

    @Override
    public ResponseDelegate put(String contentType, byte[] body) throws IOException {
        return this.nextResponse(PUT, contentType, body);
    }

    @Override
    public ResponseDelegate patch(String contentType, byte[] body) throws IOException {
        return this.nextResponse(PATCH, contentType, body);
    }

    @Override
    public ResponseDelegate delete() throws IOException {
        return this.nextResponse(DELETE, null, null);
    }

    @Override
    public ResponseDelegate delete(String contentType, byte[] body) throws IOException {
        return this.nextResponse(DELETE, contentType, body);
    }

    private ResponseDelegate nextResponse(TestRequesterFactory.Method method, String requestContentType, byte [] requestBody) throws IOException {
        try {
            this.factory.called(new TestRequesterFactory.Call(method, this.url, this.path, new HashMap<>(this.parameters), new HashMap<>(this.headers), requestContentType, requestBody));
            return this.factory.nextResponse(method, this);
        } catch (NoSuchElementException e) {
            throw new IOException("no response was supposed to be returned for method " + method, e);
        }
    }

    @Override
    public Requester parameter(String name, String value) {
        this.parameters.put(name, value);
        return this;
    }

    @Override
    public Requester parameter(String name, String[] value) {
        this.parameters.put(name, value != null ? Arrays.stream(value).collect(Collectors.joining(",")) : null);
        return this;
    }

    @Override
    public Requester parameter(String name, Iterable<String> value) {
        if(value != null) {
            boolean started = false;
            StringBuilder v = new StringBuilder();
            for (String val : value) {
                if(started) {
                    v.append(",");
                }
                v.append(val);
                started = true;
            }
            return this.parameter(name, v.toString());
        }
        return this.parameter(name, (String)null);
    }

    @Override
    public Requester header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    @Override
    public Requester path(String path) {
        this.path = path;
        return this;
    }

}
