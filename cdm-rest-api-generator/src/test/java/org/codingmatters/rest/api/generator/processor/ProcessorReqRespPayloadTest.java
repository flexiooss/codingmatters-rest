package org.codingmatters.rest.api.generator.processor;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
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
                this.compiled.on(request).castedTo("org.generated.api.PayloadPostRequest").invoke("payload"),
                isA(this.compiled.getClass("org.generated.types.Req"))
        );
        assertThat(response.body().string(), is("{\"prop\":\"val\"}"));
    }


    private Object createFilledPayloadGetResponse() {
        Object response = null;
        try {
            Object payloadBuilder = this.compiled.getClass("org.generated.types.Resp$Builder").newInstance();
            this.compiled.on(payloadBuilder).invoke("prop", String.class).with("val");
            Object payload = this.compiled.on(payloadBuilder).invoke("build");
            Object status200Builder = this.compiled.getClass("org.generated.api.payloadpostresponse.Status200$Builder").newInstance();
            this.compiled.on(status200Builder).invoke("payload", this.compiled.getClass("org.generated.types.Resp")).with(payload);
            Object status200 = this.compiled.on(status200Builder).invoke("build");
            Object builder = this.compiled.getClass("org.generated.api.PayloadPostResponse$Builder").newInstance();
            this.compiled.on(builder).invoke("status200", this.compiled.getClass("org.generated.api.payloadpostresponse.Status200")).with(status200);
            response = this.compiled.on(builder).invoke("build");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(response);
        return response;
    }

}
