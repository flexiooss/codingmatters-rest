package org.codingmatters.rest.server.netty.acceptance;

import org.codingmatters.rest.server.acceptance.ExceptionsFromProcessorAcceptanceTest;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class NettyExceptionsFromProcessorAcceptanceTest extends ExceptionsFromProcessorAcceptanceTest {

    public NettyExceptionsFromProcessorAcceptanceTest(String message, RaisingException raisingException) {
        super(message, raisingException);
    }

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
