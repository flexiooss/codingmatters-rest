package org.codingmatters.rest.undertow;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.server.acceptance.BaseAcceptanceTest;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Rule;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by nelt on 5/7/17.
 */
public class AbstractUndertowTest extends BaseAcceptanceTest {

    @Rule
    public UndertowResource undertow = new UndertowResource(new CdmHttpUndertowHandler(this::process));
    @Override
    public String baseUrl() {
        return this.undertow.baseUrl();
    }


}
