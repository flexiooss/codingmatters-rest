package org.codingmatters.rest.undertow;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.codingmatters.rest.api.ResponseDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by nelt on 4/27/17.
 */
public class UndertowResponseDelegate implements ResponseDelegate {
    static private final Logger log = LoggerFactory.getLogger(UndertowResponseDelegate.class);

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
        return this.payload(payload != null ? payload.getBytes(Charset.forName(charset)) : null);
    }

    @Override
    public ResponseDelegate payload(byte [] bytes) {
        this.exchange.startBlocking();
        try {
            this.exchange.getOutputStream().write(bytes);
        } catch (IOException e) {
            log.error("error writing response body", e);
        }
        return this;
    }
}
