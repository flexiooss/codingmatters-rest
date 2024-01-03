package org.codingmatters.rest.server.netty.acceptance;

import org.codingmatters.rest.server.acceptance.RequestDelegateAcceptanceTest;
import org.junit.After;
import org.junit.Before;

public class NettyRequestDelegateAcceptanceTest extends RequestDelegateAcceptanceTest {

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
