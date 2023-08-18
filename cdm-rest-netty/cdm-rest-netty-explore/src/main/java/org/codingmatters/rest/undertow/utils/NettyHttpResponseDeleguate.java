package org.codingmatters.rest.undertow.utils;

import io.netty.handler.codec.http.HttpResponse;
import org.codingmatters.rest.api.ResponseDelegate;

import java.io.InputStream;

public class NettyHttpResponseDeleguate implements ResponseDelegate {
    @Override
    public ResponseDelegate contenType(String contenType) {
        return null;
    }

    @Override
    public ResponseDelegate status(int code) {
        return null;
    }

    @Override
    public ResponseDelegate addHeader(String name, String... value) {
        return null;
    }

    @Override
    public ResponseDelegate addHeaderIfNot(String name, String... value) {
        return null;
    }

    @Override
    public ResponseDelegate payload(String payload, String charset) {
        return null;
    }

    @Override
    public ResponseDelegate payload(byte[] bytes) {
        return null;
    }

    @Override
    public ResponseDelegate payload(InputStream in) {
        return null;
    }

    @Override
    public void close() throws Exception {

    }

    public HttpResponse response() {
        return null;
    }
}
