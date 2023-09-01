package org.codingmatters.rest.server.netty.acceptance;

import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.netty.utils.HttpServer;
import org.codingmatters.rest.server.netty.ProcessorRequestHandler;

public class NettyTestServer {
    private HttpServer httpServer;

    public void setUp(HttpServer.HandlerSupplier handlerSupplier) throws Exception {
        this.httpServer = HttpServer.testServer(handlerSupplier);
        this.httpServer.start();
    }

    public void setUp(Processor processor) throws Exception {
        HttpServer.HandlerSupplier handlerSupplier = (host, port) -> new ProcessorRequestHandler(
                processor,
                host,
                port
        );
        this.setUp(handlerSupplier);
    }

    public void tearDown() throws Exception {
        this.httpServer.shutdown();
        this.httpServer.awaitTermination();
    }

    public String baseUrl() {
        return "http://" + this.httpServer.host() + ":" + this.httpServer.port();
    }
}
