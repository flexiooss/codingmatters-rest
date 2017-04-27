package org.codingmatters.http;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.codingmatters.http.api.Processor;

/**
 * Created by nelt on 4/27/17.
 */
public class CdmHttpUndertowHandler implements HttpHandler {
    private final Processor processor;

    public CdmHttpUndertowHandler(Processor processor) {
        this.processor = processor;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        this.processor.process(new UndertowRequestDeleguate(exchange), new UndertowResponseDeleguate(exchange));
    }
}
