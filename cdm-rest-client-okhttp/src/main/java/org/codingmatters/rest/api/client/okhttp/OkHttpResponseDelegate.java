package org.codingmatters.rest.api.client.okhttp;

import okhttp3.Response;
import okhttp3.ResponseBody;
import org.codingmatters.rest.api.client.ResponseDelegate;
import org.codingmatters.rest.io.CountedReferenceTemporaryFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OkHttpResponseDelegate implements ResponseDelegate {
    private final int code;
    private final Map<String, List<String>> headers;
    private final String contentType;
    private final CountedReferenceTemporaryFile bodyFile;

    public OkHttpResponseDelegate(Response response) throws IOException {
        this.code = response.code();
        try(ResponseBody body = response.body()) {
            this.contentType = response.body().contentType() != null ? response.body().contentType().toString() : null;
            this.bodyFile = CountedReferenceTemporaryFile.create();

            try(OutputStream out = this.bodyFile.outputStream(); InputStream in = body.byteStream()) {
                byte [] buffer = new byte[1024];
                for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                    out.write(buffer, 0, read);
                }
                out.flush();
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
        try(InputStream in = this.bodyFile.inputStream() ; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte [] buffer = new byte[1024];
            for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                out.write(buffer, 0, read);
            }
            out.flush();
            return out.toByteArray();
        }
    }

    @Override
    public InputStream bodyStream() throws IOException {
        return this.bodyFile.inputStream();
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

    @Override
    public void close() throws Exception {
        this.bodyFile.close();
    }
}
