package org.codingmatters.rest.server.netty;

import io.netty.handler.codec.http.*;
import org.codingmatters.rest.api.ResponseDelegate;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyHttpResponseDeleguate implements ResponseDelegate {
    private final boolean keepAlive;

    public NettyHttpResponseDeleguate(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

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
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

//        long size = body != null ? body.size() : 0L;
//        String content = "Server says, hello ! Body size is " + size + ".";
        String content = "Yo !";

        response.content().writeBytes(content.getBytes(StandardCharsets.UTF_8));
        if(this.keepAlive) {
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        }
        return response;
    }
}
