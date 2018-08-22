package org.codingmatters.rest.api.generator.processor;

import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 6/2/17.
 */
public class ProcessorResponseHeadersSubstitutionTest extends AbstractProcessorHttpRequestTest {

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-response.raml");
        this.compiled = helper.compiled();
        this.classes = this.compiled.classLoader();
//        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIProcessor.java");
    }

    @Test
    public void single() throws Exception {
        this.setupProcessorWithHandler(
                "headersGetHandler",
                req -> this.createFilledHeadersGetResponse("%API_PATH%/subpath", new String[] {"%API_PATH%/subpath"})
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/headers")
                .get()
                .build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.header("stringParam"), is(this.undertow.baseUrl() + "/api/subpath"));
        assertThat(response.headers("arrayParam"), contains(this.undertow.baseUrl() + "/api/subpath"));
    }

    private Object createFilledHeadersGetResponse(String stringParamValue, String[] arrayParamValue) {
        Object response = null;
        try {
            Object status200Builder = this.compiled.getClass("org.generated.api.headersgetresponse.Status200$Builder").newInstance();
            this.compiled.on(status200Builder).invoke("stringParam", String.class).with(stringParamValue);
            this.compiled.on(status200Builder).invoke("arrayParam", String[].class).with(new Object[] {arrayParamValue});
            Object status200 = this.compiled.on(status200Builder).invoke("build");
            Object builder = this.compiled.getClass("org.generated.api.HeadersGetResponse$Builder").newInstance();
            this.compiled.on(builder).invoke("status200", this.compiled.getClass("org.generated.api.headersgetresponse.Status200")).with(status200);
            response = this.compiled.on(builder).invoke("build");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
