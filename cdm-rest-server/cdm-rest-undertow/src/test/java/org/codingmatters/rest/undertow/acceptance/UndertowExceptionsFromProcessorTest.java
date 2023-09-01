package org.codingmatters.rest.undertow.acceptance;

import org.codingmatters.rest.server.acceptance.ExceptionsFromProcessorAcceptanceTest;
import org.codingmatters.rest.undertow.CdmHttpUndertowHandler;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class UndertowExceptionsFromProcessorTest extends ExceptionsFromProcessorAcceptanceTest {
    @Rule
    public UndertowResource undertow = new UndertowResource(new CdmHttpUndertowHandler(this::process));

    public UndertowExceptionsFromProcessorTest(String message, ExceptionsFromProcessorAcceptanceTest.RaisingException raisingException) {
        super(message, raisingException);
    }

    @Override
    public String baseUrl() {
        return this.undertow.baseUrl();
    }



}
