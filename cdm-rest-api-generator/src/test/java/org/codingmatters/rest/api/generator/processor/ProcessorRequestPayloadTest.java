package org.codingmatters.rest.api.generator.processor;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
import org.codingmatters.rest.api.types.File;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/26/17.
 */
public class ProcessorRequestPayloadTest extends AbstractProcessorHttpRequestTest {

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-request.raml");
        this.compiled = helper.compiled();
        this.classes = this.compiled.classLoader();
    }

    @Test
    public void requestPayload() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "payloadPostHandler",
                req -> {
                    requestHolder.set(req);
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/payload/")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf_8"), "{\"prop\":\"val\"}"))
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.compiled.on(request).castedTo("org.generated.api.PayloadPostRequest").invoke("payload"),
                isA(this.compiled.getClass("org.generated.types.Req"))
        );
    }

    @Test
    public void filePayload() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "filePayloadPostHandler",
                req -> {
                    requestHolder.set(req);
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/file-payload/")
                .post(RequestBody.create(MediaType.parse("application/octet-stream"), "just a bunch of bytes".getBytes()))
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.compiled.on(request).castedTo("org.generated.api.FilePayloadPostRequest").invoke("payload"),
                isA(this.compiled.getClass(File.class.getName()))
        );
    }

    @Test
    public void requestPayload_emptyPayload() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "payloadPostHandler",
                req -> {
                    requestHolder.set(req);
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/payload/")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf_8"), "{}"))
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.compiled.on(request).castedTo("org.generated.api.PayloadPostRequest").invoke("payload"),
                isA(this.compiled.getClass("org.generated.types.Req"))
        );
    }

    @Test
    public void requestPayload_nullPayload() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "payloadPostHandler",
                req -> {
                    requestHolder.set(req);
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/payload/")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf_8"), "null"))
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.compiled.on(request).castedTo("org.generated.api.PayloadPostRequest").invoke("payload"),
                is(nullValue())
        );
    }

    @Test
    public void requestPayload_unparseable() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "payloadPostHandler",
                req -> {
                    requestHolder.set(req);
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/payload/")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf_8"), "yopyop tagada"))
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(400));
        assertThat(response.body().string(), is("bad request body, see logs"));
        assertThat(request, is(nullValue()));
    }

}
