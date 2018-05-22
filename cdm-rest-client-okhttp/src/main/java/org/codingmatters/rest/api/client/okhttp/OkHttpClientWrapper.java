package org.codingmatters.rest.api.client.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.client.okhttp.exception.ConnectionTimeoutException;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class OkHttpClientWrapper {

    static public OkHttpClientWrapper build() {
        return build(new OkHttpClient.Builder());
    }

    static public OkHttpClientWrapper build(OkHttpClient.Builder builder) {
        return from(builder.build());
    }

    static public OkHttpClientWrapper from(OkHttpClient client) {
        return new OkHttpClientWrapper(client);
    }

    private final OkHttpClient delegate;

    private OkHttpClientWrapper(OkHttpClient delegate) {
        this.delegate = delegate;
    }

    public Response execute(Request request) throws IOException {
        try {
            return this.delegate.newCall(request).execute();
        } catch (SocketTimeoutException e) {
            if(e.getMessage().equals("connect timed out")) {
                throw new ConnectionTimeoutException("connection timed out", e);
            } else {
                throw e;
            }
        }
    }
}
