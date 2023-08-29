package org.codingmatters.rest.undertow.acceptance;

import org.codingmatters.rest.server.acceptance.RequestDelegateAcceptanceTest;
import org.codingmatters.rest.undertow.CdmHttpUndertowHandler;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Rule;

/**
 * Created by nelt on 5/7/17.
 */
public class UndertowRequestDelegateTest extends RequestDelegateAcceptanceTest {
    @Rule
    public UndertowResource undertow = new UndertowResource(new CdmHttpUndertowHandler(this::process));
    @Override
    public String baseUrl() {
        return this.undertow.baseUrl();
    }
}