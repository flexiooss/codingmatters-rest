package org.codingmatters.rest.server.netty.acceptance;

import org.codingmatters.rest.netty.utils.HttpServer;
import org.codingmatters.rest.server.acceptance.ExceptionsFromProcessorAcceptanceTest;
import org.codingmatters.rest.server.netty.ProcessorRequestHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class NettyExceptionsFromProcessorAcceptanceTest extends ExceptionsFromProcessorAcceptanceTest {

    private HttpServer httpServer;

    public NettyExceptionsFromProcessorAcceptanceTest(String message, RaisingException raisingException) {
        super(message, raisingException);
    }

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
        return "http://" + this.httpServer.host() + ":" + this.httpServer.port();
    }
}
