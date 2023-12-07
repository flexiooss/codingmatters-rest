package org.codingmatters.rest.server.netty.acceptance;

import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.netty.utils.Http1Server;
import org.codingmatters.rest.server.netty.ProcessorRequestHandler;

public class NettyTestServer {
    private Http1Server httpServer;

    public void setUp(Http1Server.HandlerSupplier handlerSupplier) throws Exception {
        this.httpServer = Http1Server.testServer(handlerSupplier);
        this.httpServer.start();
    }

    public void setUp(Processor processor) throws Exception {
        Http1Server.HandlerSupplier handlerSupplier = (host, port) -> new ProcessorRequestHandler(
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
