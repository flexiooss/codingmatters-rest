package org.codingmatters.rest.undertow;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.codingmatters.rest.api.ResponseDeleguate;

import java.nio.charset.Charset;

/**
 * Created by nelt on 4/27/17.
 */
public class UndertowResponseDeleguate implements ResponseDeleguate {
    private final HttpServerExchange exchange;

    public UndertowResponseDeleguate(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public ResponseDeleguate contenType(String contentType) {
        this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
        return this;
    }

    @Override
    public ResponseDeleguate status(int code) {
        this.exchange.setStatusCode(code);
        return this;
    }

    @Override
    public ResponseDeleguate addHeader(String name, String value) {
        this.exchange.getResponseHeaders().add(HttpString.tryFromString(name),value);
        return this;
    }

    @Override
    public ResponseDeleguate payload(String payload, String charset) {
        this.exchange.getResponseSender().send(payload, Charset.forName(charset));
        return this;
    }
}
