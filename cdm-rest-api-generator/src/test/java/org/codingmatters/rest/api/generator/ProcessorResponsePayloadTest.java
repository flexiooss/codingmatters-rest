package org.codingmatters.rest.api.generator;

import okhttp3.Request;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 6/1/17.
 */
public class ProcessorResponsePayloadTest extends AbstractProcessorHttpRequestTest {

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-response.raml");
        this.compiled = helper.compiled();
    }

    @Test
    public void payload() throws Exception {
        this.setupProcessorWithHandler(
                "payloadGetHandler",
                req -> this.createFilledPayloadGetResponse()
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/payload")
                .get()
                .build()).execute();
        assertThat(response.code(), is(200));
        assertThat(response.body().string(), is("{\"prop\":\"val\"}"));
    }

    @Test
    public void payload_empty() throws Exception {
        this.setupProcessorWithHandler(
                "payloadGetHandler",
                req -> this.createNullPayloadGetResponse()
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/payload")
                .get()
                .build()).execute();
        assertThat(response.code(), is(200));
        assertThat(response.body().string(), is(""));
    }

    private Object createFilledPayloadGetResponse() {
        Object response = null;
        try {
            Object payloadBuilder = this.compiled.getClass("org.generated.types.Resp$Builder").newInstance();
            this.compiled.on(payloadBuilder).invoke("prop", String.class).with("val");
            Object payload = this.compiled.on(payloadBuilder).invoke("build");
            Object status200Builder = this.compiled.getClass("org.generated.api.payloadgetresponse.Status200$Builder").newInstance();
            this.compiled.on(status200Builder).invoke("payload", this.compiled.getClass("org.generated.types.Resp")).with(payload);
            Object status200 = this.compiled.on(status200Builder).invoke("build");
            Object builder = this.compiled.getClass("org.generated.api.PayloadGetResponse$Builder").newInstance();
            this.compiled.on(builder).invoke("status200", this.compiled.getClass("org.generated.api.payloadgetresponse.Status200")).with(status200);
            response = this.compiled.on(builder).invoke("build");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(response);
        return response;
    }

    private Object createNullPayloadGetResponse() {
        Object response = null;
        try {
            Object status200Builder = this.compiled.getClass("org.generated.api.payloadgetresponse.Status200$Builder").newInstance();
            Object status200 = this.compiled.on(status200Builder).invoke("build");
            Object builder = this.compiled.getClass("org.generated.api.PayloadGetResponse$Builder").newInstance();
            this.compiled.on(builder).invoke("status200", this.compiled.getClass("org.generated.api.payloadgetresponse.Status200")).with(status200);
            response = this.compiled.on(builder).invoke("build");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(response);
        return response;
    }
}
