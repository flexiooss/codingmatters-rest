package org.codingmatters.rest.undertow;

import okhttp3.OkHttpClient;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.server.acceptance.BaseAcceptanceTest;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by nelt on 5/7/17.
 */
public class UndertowRequestDelegateTest extends BaseAcceptanceTest {

    @Rule
    public UndertowResource undertow = new UndertowResource(new CdmHttpUndertowHandler(this::process));
    @Override
    public String baseUrl() {
        return this.undertow.baseUrl();
    }

}