package org.codingmatters.rest.api.client.okhttp;

import okhttp3.Response;
import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;

public class OkHttpResponseDelegate implements ResponseDelegate {
    private final Response response;

    public OkHttpResponseDelegate(Response response) {
        this.response = response;
    }

    @Override
    public int code() {
        return this.response.code();
    }

    @Override
    public byte[] body() throws IOException {
        return this.response.body().bytes();
    }

    @Override
    public String header(String name) {
        return this.response.header(name);
    }
}
