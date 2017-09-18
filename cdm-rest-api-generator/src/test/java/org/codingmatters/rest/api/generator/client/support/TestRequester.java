package org.codingmatters.rest.api.generator.client.support;

import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;

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
        return this.nextResponse(GET);
    }

    @Override
    public ResponseDelegate post(String contentType, byte[] body) throws IOException {
        return this.nextResponse(POST);
    }

    @Override
    public ResponseDelegate put(String contentType, byte[] body) throws IOException {
        return this.nextResponse(PUT);
    }

    @Override
    public ResponseDelegate patch(String contentType, byte[] body) throws IOException {
        return this.nextResponse(PATCH);
    }

    @Override
    public ResponseDelegate delete() throws IOException {
        return this.nextResponse(DELETE);
    }

    @Override
    public ResponseDelegate delete(String contentType, byte[] body) throws IOException {
        return this.nextResponse(DELETE);
    }

    private ResponseDelegate nextResponse(TestRequesterFactory.Method method) throws IOException {
        try {
            return this.factory.nextResponse(method);
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
    public Requester header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    @Override
    public Requester path(String path) {
        this.path = path;
        return null;
    }



    public TestRequesterFactory factory() {
        return factory;
    }

    public String url() {
        return url;
    }

    public String path() {
        return path;
    }

    public HashMap<String, String> parameters() {
        return parameters;
    }

    public HashMap<String, String> headers() {
        return headers;
    }
}
