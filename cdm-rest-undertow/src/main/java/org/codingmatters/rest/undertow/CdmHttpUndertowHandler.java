package org.codingmatters.rest.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.io.Content;

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

        try(
                UndertowRequestDelegate requestDelegate = new UndertowRequestDelegate(exchange);
                UndertowResponseDelegate responseDelegate = new UndertowResponseDelegate(exchange)) {
            this.processor.process(requestDelegate, responseDelegate);
        }
        exchange.endExchange();

        Content.cleanupTemporaryFiles();
    }
}
