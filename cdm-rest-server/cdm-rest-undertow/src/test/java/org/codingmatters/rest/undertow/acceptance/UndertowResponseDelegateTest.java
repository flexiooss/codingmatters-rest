package org.codingmatters.rest.undertow.acceptance;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.codingmatters.rest.server.acceptance.ResponseDelegateAcceptanceTest;
import org.codingmatters.rest.undertow.CdmHttpUndertowHandler;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/7/17.
 */
public class UndertowResponseDelegateTest extends ResponseDelegateAcceptanceTest {
    @Rule
    public UndertowResource undertow = new UndertowResource(new CdmHttpUndertowHandler(this::process));
    @Override
    public String baseUrl() {
        return this.undertow.baseUrl();
    }
}
