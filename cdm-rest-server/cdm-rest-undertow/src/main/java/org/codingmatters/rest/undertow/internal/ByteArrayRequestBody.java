package org.codingmatters.rest.undertow.internal;

import io.undertow.server.HttpServerExchange;
import org.codingmatters.rest.undertow.UndertowRequestDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayRequestBody implements RequestBody {
    static private final Logger log = LoggerFactory.getLogger(UndertowRequestDelegate.class);

    static public RequestBody from(HttpServerExchange exchange) {
        try {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream in = exchange.getInputStream()) {
                byte[] buffer = new byte[1024];
                for (int read = in.read(buffer); read != -1; read = in.read(buffer)) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                out.close();

                return new ByteArrayRequestBody(out.toByteArray());
            }
        } catch (IOException e) {
            log.error("failed reading body", e);
            return new ByteArrayRequestBody(new byte[0]);
        }

    }

    private final byte[] body;

    public ByteArrayRequestBody(byte[] body) {
        this.body = body;
    }

    @Override
    public InputStream inputStream() {
        return new ByteArrayInputStream(this.body);
    }

    @Override
    public void close() throws Exception {}
}
