package org.codingmatters.rest.api.generator.processor;

import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 6/1/17.
 */
public class ProcessorResponseStatusTest extends AbstractProcessorHttpRequestTest {

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-response.raml");
        this.compiled = helper.compiled();
        this.classes = this.compiled.classLoader();
    }

    @Test
    public void status200() throws Exception {
        this.setupProcessorWithHandler(
                "statusGetHandler",
                req -> this.create200StatusGetResponse(200)
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/status")
                .get()
                .build()).execute();
        assertThat(response.code(), is(200));
    }

    @Test
    public void status201() throws Exception {
        this.setupProcessorWithHandler(
                "statusGetHandler",
                req -> this.create200StatusGetResponse(201)
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/status")
                .get()
                .build()).execute();
        assertThat(response.code(), is(201));
    }

    @Test
    public void status202() throws Exception {
        this.setupProcessorWithHandler(
                "statusGetHandler",
                req -> this.create200StatusGetResponse(202)
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/status")
                .get()
                .build()).execute();
        assertThat(response.code(), is(202));
    }

    private Object create200StatusGetResponse(int code) {
        Object response = null;
        try {
            Object statusBuilder = this.compiled.getClass("org.generated.api.statusgetresponse.Status" + code + "$Builder").newInstance();
            Object status = this.compiled.on(statusBuilder).invoke("build");
            Object builder = this.compiled.getClass("org.generated.api.StatusGetResponse$Builder").newInstance();
            this.compiled.on(builder).invoke("status" + code, this.compiled.getClass("org.generated.api.statusgetresponse.Status" + code)).with(status);
            response = this.compiled.on(builder).invoke("build");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
