package org.codingmatters.rest.api.client.okhttp;

import okhttp3.Response;
import okhttp3.ResponseBody;
import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.util.List;

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
        try(ResponseBody body = this.response.body()) {
            return body.bytes();
        }
    }

    @Override
    public String[] header(String name) {
        List<String> headers = this.response.headers(name);
        return headers.toArray(new String [headers.size()]);
    }
}
