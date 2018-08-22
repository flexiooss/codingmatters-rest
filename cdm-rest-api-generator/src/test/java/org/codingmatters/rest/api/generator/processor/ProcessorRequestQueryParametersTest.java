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
public class ProcessorRequestQueryParametersTest extends AbstractProcessorHttpRequestTest {

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-request.raml");
        this.compiled = helper.compiled();
        this.classes = this.compiled.classLoader();
    }

    @Test
    public void singleParameter() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "queryParamsGetHandler",
                req -> {
                    requestHolder.set(req);
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/query-params?stringParam=val")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.compiled.on(request).castedTo("org.generated.api.QueryParamsGetRequest").invoke("stringParam"),
                is("val")
        );
    }

    @Test
    public void singleParameter_empty() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "queryParamsGetHandler",
                req -> {
                    requestHolder.set(req);
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/query-params?stringParam=")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.compiled.on(request).castedTo("org.generated.api.QueryParamsGetRequest").invoke("stringParam"),
                is("")
        );
    }

    @Test
    public void singleParameter_notSet() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "queryParamsGetHandler",
                req -> {
                    requestHolder.set(req);
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/query-params")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.compiled.on(request).castedTo("org.generated.api.QueryParamsGetRequest").invoke("stringParam"),
                is(nullValue())
        );
    }

    @Test
    public void arrayParameter() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "queryParamsGetHandler",
                req -> {
                    requestHolder.set(req);
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/query-params" +
                "?stringArrayParam=v1" +
                "&stringArrayParam=v2" +
                "&stringArrayParam=v3")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.compiled.on(request).castedTo("org.generated.api.QueryParamsGetRequest").invoke("stringArrayParam"),
                contains("v1", "v2", "v3")
        );
    }
}
