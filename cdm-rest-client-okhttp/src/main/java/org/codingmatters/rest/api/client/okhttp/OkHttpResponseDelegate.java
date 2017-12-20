package org.codingmatters.rest.api.client.okhttp;

import okhttp3.Response;
import okhttp3.ResponseBody;
import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OkHttpResponseDelegate implements ResponseDelegate {
    //private final Response response;

    private final int code;
    private final byte[] body;
    private final Map<String, List<String>> headers;

    public OkHttpResponseDelegate(Response response) throws IOException {
        //this.response = response;
        this.code = response.code();
        try(ResponseBody body = response.body()) {
            this.body = body.bytes();
        }
        this.headers = new HashMap<>(response.headers().toMultimap());
    }

    @Override
    public int code() {
        //return this.response.code();
        return this.code;
    }

    @Override
    public byte[] body() throws IOException {
        //try(ResponseBody body = this.response.body()) {
        //    return body.bytes();
        //}
        return this.body;
    }

    @Override
    public String[] header(String name) {
        //List<String> headers = this.response.headers(name);
        List<String> header = this.headers.get(name.toLowerCase());
        return header != null ? header.toArray(new String [headers.size()]) : null;
    }
}
