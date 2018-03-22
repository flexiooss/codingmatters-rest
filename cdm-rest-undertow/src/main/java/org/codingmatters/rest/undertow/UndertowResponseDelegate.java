package org.codingmatters.rest.undertow;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.codingmatters.rest.api.ResponseDelegate;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by nelt on 4/27/17.
 */
public class UndertowResponseDelegate implements ResponseDelegate {
    private final HttpServerExchange exchange;

    public UndertowResponseDelegate(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public ResponseDelegate contenType(String contentType) {
        this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
        return this;
    }

    @Override
    public ResponseDelegate status(int code) {
        this.exchange.setStatusCode(code);
        return this;
    }

    @Override
    public ResponseDelegate addHeader(String name, String ... values) {
        if(values != null) {
            for (String value : values) {
                this.exchange.getResponseHeaders().add(HttpString.tryFromString(name), value);
            }
        }

        return this;
    }

    @Override
    public ResponseDelegate payload(String payload, String charset) {
        this.exchange.getResponseSender().send(payload, Charset.forName(charset));
        return this;
    }

    @Override
    public ResponseDelegate payload(byte [] bytes) {
        this.exchange.getResponseSender().send(ByteBuffer.wrap(bytes));
        return this;
    }
}
