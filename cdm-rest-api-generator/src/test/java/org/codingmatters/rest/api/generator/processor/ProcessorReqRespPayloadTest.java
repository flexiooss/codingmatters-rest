package org.codingmatters.rest.api.generator.processor;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 6/16/17.
 */
public class ProcessorReqRespPayloadTest extends AbstractProcessorHttpRequestTest {

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-request-response.raml");
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
                    return this.createFilledPayloadGetResponse();
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/payload/")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf_8"), "{\"prop\":\"val\"}"))
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.PayloadPostRequest").call("payload").get(),
                isA(this.classes.get("org.generated.types.Req").get())
        );
        assertThat(response.body().string(), is("{\"prop\":\"val\"}"));
    }


    @Test
    public void already() throws Exception {
        AtomicReference requestHolder = new AtomicReference();
        this.setupProcessorWithHandler(
                "alreadyPostHandler",
                req -> {
                    requestHolder.set(req);
                    return this.createFilledAlreadyGetResponse();
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/already/")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf_8"), "{}"))
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.AlreadyPostRequest").call("payload").get(),
                isA(this.classes.get("org.codingmatters.AnAlreadyDefinedValueObject").get())
        );
        assertThat(response.body().string(), is("{}"));
    }

    private Object createFilledPayloadGetResponse() {
        ObjectHelper response = null;
        try {
            ObjectHelper payload = this.classes.get("org.generated.types.Resp$Builder")
                    .newInstance()
                    .call("prop", String.class).with("val")
                    .call("build");
            ObjectHelper status200 = this.classes.get("org.generated.api.payloadpostresponse.Status200$Builder")
                    .newInstance()
                    .call("payload", this.classes.get("org.generated.types.Resp").get()).with(payload.get())
                    .call("build");
            response = this.classes.get("org.generated.api.PayloadPostResponse$Builder")
                    .newInstance()
                    .call("status200", this.classes.get("org.generated.api.payloadpostresponse.Status200").get()).with(status200.get())
                    .call("build");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.get();
    }

    private Object createFilledAlreadyGetResponse() {
        ObjectHelper response = null;
        try {
            ObjectHelper payload = this.classes.get("org.codingmatters.AnAlreadyDefinedValueObject$Builder")
                    .newInstance()
                    .call("build");
            ObjectHelper status200 = this.classes.get("org.generated.api.alreadypostresponse.Status200$Builder")
                    .newInstance()
                    .call("payload", this.classes.get("org.codingmatters.AnAlreadyDefinedValueObject").get()).with(payload.get())
                    .call("build");
            response = this.classes.get("org.generated.api.AlreadyPostResponse$Builder")
                    .newInstance()
                    .call("status200", this.classes.get("org.generated.api.alreadypostresponse.Status200").get()).with(status200.get())
                    .call("build");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.get();
    }



}
