package org.codingmatters.rest.api.client.okhttp;

import okhttp3.Response;
import okhttp3.ResponseBody;
import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OkHttpResponseDelegate implements ResponseDelegate {
    private final int code;
    private final byte[] body;
    private final Map<String, List<String>> headers;
    private final String contentType;

    public OkHttpResponseDelegate(Response response) throws IOException {
        this.code = response.code();
        try(ResponseBody body = response.body()) {
            this.contentType = response.body().contentType() != null ? response.body().contentType().toString() : null;
            try(InputStream in = body.byteStream()) {
                ByteBuffer content = ByteBuffer.allocate((int) response.body().contentLength());
                byte [] buffer = new byte[1024];
                for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                    content = content.put(buffer, 0, read);
                }
                this.body = content.array();
            }
        }
        this.headers = new HashMap<>(response.headers().toMultimap());
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
        List<String> headerValues = this.headers.get(name.toLowerCase());
        return headerValues != null ? headerValues.toArray(new String [headerValues.size()]) : null;
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
