package org.codingmatters.rest.api.client.caching.utils;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

public class ResponseReviver {

    private final byte[] bodyBytes;
    private final MediaType contentType;
    private final Response.Builder builder;

    public ResponseReviver(Response response) throws IOException {
        this.bodyBytes = response.body().bytes();
        this.contentType = response.body().contentType();
        this.builder = response.newBuilder();
    }

    public synchronized Response revived() {
        return this.builder.body(ResponseBody.create(bodyBytes, contentType)).build();
    }

}
