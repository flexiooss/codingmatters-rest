package org.codingmatters.rest.undertow.acceptance;

import org.codingmatters.rest.server.acceptance.SseAcceptanceTest;
import org.codingmatters.rest.undertow.CdmHttpUndertowHandler;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Rule;

public class UndertowSseAcceptanceTest extends SseAcceptanceTest {

    @Rule
    public UndertowResource undertow = new UndertowResource(new CdmHttpUndertowHandler(this::process));

    @Override
    public String baseUrl() {
        return this.undertow.baseUrl();
    }
}
