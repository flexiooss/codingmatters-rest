package org.codingmatters.rest.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.undertow.util.HttpString;
import org.codingmatters.rest.api.ResponseDelegate;
import org.codingmatters.rest.io.headers.HeaderEncodingHandler;
import org.codingmatters.rest.netty.utils.DynamicByteBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyHttpResponseDeleguate implements ResponseDelegate {
    private final boolean keepAlive;
    private final FullHttpResponse response;
    private DynamicByteBuffer body;
    private final int maxInMemoryCapacity = 2 * 1024;

    public NettyHttpResponseDeleguate(boolean keepAlive) {
        this.keepAlive = keepAlive;
        this.response = new DefaultFullHttpResponse(HTTP_1_1, OK);
    }

    @Override
    public ResponseDelegate contenType(String contenType) {
        if(contenType != null) {
            this.response.headers().set(HttpHeaderNames.CONTENT_TYPE, contenType);
        }
        return this;
    }

    @Override
    public ResponseDelegate status(int code) {
        this.response.setStatus(HttpResponseStatus.valueOf(code));
        return this;
    }

    @Override
    public ResponseDelegate addHeader(String name, String... values) {
        if(values != null) {
            for (String value : values) {
                if( HeaderEncodingHandler.needEncoding( value )){
                    this.response.headers().add(name + "*", HeaderEncodingHandler.encodeHeader( value ));
                } else {
                    this.response.headers().add(name, value);
                }
            }
        }
        return this;
    }

    @Override
    public ResponseDelegate addHeaderIfNot(String name, String... values) {
        List<String> current = this.response.headers().getAll(name);
        if(current == null || current.size() == 0) {
            return this.addHeader(name, values);
        }
        return this;
    }

    @Override
    public ResponseDelegate payload(String payload, String charset) {
        try {
            return this.payload(payload.getBytes(charset != null ? charset : "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseDelegate payload(byte[] bytes) {
        this.ensureFreshBody();
        try {
            this.body.accumulate(Unpooled.wrappedBuffer(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public ResponseDelegate payload(InputStream in) {
        this.ensureFreshBody();
        byte [] buffer = new byte[1024];
        try {
            for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                this.body.accumulate(Unpooled.wrappedBuffer(buffer, 0, read));
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void ensureFreshBody() {
        if(this.body != null) {
            this.body.release();
        }
        this.body = new DynamicByteBuffer(this.maxInMemoryCapacity);
    }

    @Override
    public void close() throws Exception {
        if (this.body != null) {
            this.body.release();
            this.body = null;
        }
    }

    public HttpResponse response() {
        if(this.body != null) {
            try (InputStream in = this.body.stream()) {
                byte[] buffer = new byte[1024];
                for (int read = in.read(buffer); read != -1; read = in.read(buffer)) {
                    this.response.content().writeBytes(buffer, 0, read);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(this.keepAlive) {
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        }
        return response;
    }
}
