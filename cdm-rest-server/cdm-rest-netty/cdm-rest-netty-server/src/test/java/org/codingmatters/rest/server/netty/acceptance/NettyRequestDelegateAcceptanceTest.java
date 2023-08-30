package org.codingmatters.rest.server.netty.acceptance;

import org.codingmatters.rest.netty.utils.HttpServer;
import org.codingmatters.rest.server.acceptance.RequestDelegateAcceptanceTest;
import org.codingmatters.rest.server.netty.ProcessorRequestHandler;
import org.junit.After;
import org.junit.Before;

public class NettyRequestDelegateAcceptanceTest extends RequestDelegateAcceptanceTest {

    private HttpServer httpServer;

    @Before
    public void setUp() throws Exception {
        this.httpServer = HttpServer.testServer((host, port) -> new ProcessorRequestHandler(
                (requestDelegate, responseDelegate) -> {
                    process(requestDelegate, responseDelegate);
                },
                host,
                port
        ));
        this.httpServer.start();
    }

    @After
    public void tearDown() throws Exception {
        this.httpServer.shutdown();
        this.httpServer.awaitTermination();
    }

    @Override
    public String baseUrl() {
        return "http://localhost:" + this.httpServer.port();
    }
}
