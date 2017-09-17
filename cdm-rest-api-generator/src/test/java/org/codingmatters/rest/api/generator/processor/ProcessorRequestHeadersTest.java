package org.codingmatters.rest.api.generator.processor;

import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/30/17.
 */
public class ProcessorRequestHeadersTest extends AbstractProcessorHttpRequestTest {

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-request.raml");
        this.compiled = helper.compiled();
    }

    @Test
    public void singleParameter() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "headerParamsGetHandler",
                req -> {
                    requestHolder.set(req);
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("stringParam", "val")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.compiled.on(request).castedTo("org.generated.api.HeaderParamsGetRequest").invoke("stringParam"),
                is("val")
        );
    }

    @Test
    public void arrayParameter() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "headerParamsGetHandler",
                req -> {
                    requestHolder.set(req);
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("arrayParam", "val1")
                .addHeader("arrayParam", "val2")
                .addHeader("arrayParam", "val3")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.compiled.on(request).castedTo("org.generated.api.HeaderParamsGetRequest").invoke("arrayParam"),
                contains("val1", "val2", "val3")
        );
    }
}
