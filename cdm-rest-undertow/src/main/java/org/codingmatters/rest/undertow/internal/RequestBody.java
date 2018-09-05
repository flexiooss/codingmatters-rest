package org.codingmatters.rest.undertow.internal;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;

import java.io.InputStream;

public interface RequestBody extends AutoCloseable {

    static RequestBody from(HttpServerExchange exchange) {
        if(isSmall(exchange.getRequestHeaders().get("content-length"))) {
            return ByteArrayRequestBody.from(exchange);
        } else {
            return CountedReferenceTemporaryFileRequestBody.from(exchange);
        }
    }

    static boolean isSmall(HeaderValues contentLength) {
        if(contentLength == null) return true;
        try {
            if (Integer.parseInt(contentLength.getFirst()) <= 1024) {
                return true;
            }
        } catch (NumberFormatException e) {}
        return false;
    }

    InputStream inputStream();

}
