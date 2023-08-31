package org.codingmatters.rest.server.netty.acceptance;

import org.codingmatters.rest.netty.utils.HttpServer;
import org.codingmatters.rest.server.acceptance.ResponseDelegateAcceptanceTest;
import org.codingmatters.rest.server.netty.ProcessorRequestHandler;
import org.junit.After;
import org.junit.Before;

public class NettyResponseDelegateAcceptanceTest extends ResponseDelegateAcceptanceTest {

    private NettyTestServer testServer = new NettyTestServer();

    @Before
    public void setUp() throws Exception {
        this.testServer.setUp(this::process);
    }

    @After
    public void tearDown() throws Exception {
        this.testServer.tearDown();
    }

    @Override
    public String baseUrl() {
        return this.testServer.baseUrl();
    }
}
