package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.generator.client.support.TestRequesterFactory;
import org.junit.Test;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.API_PACK;
import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.CLIENT_PACK;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorRequestHeadersTest extends AbstractRequesterClientGeneratorRequestTest {
    @Test
    public void headers() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.compiled().on(client).invoke("headerParams");

        Object requestBuilder = this.compiled()
                .onClass(API_PACK + ".HeaderParamsGetRequest")
                .invoke("builder");

        this.compiled().on(requestBuilder).invoke("stringParam", String.class).with("val");
        this.compiled().on(requestBuilder).invoke("arrayParam", String[].class).with(new Object[]{new String[]{"v1", "v2"}});

        Object request = this.compiled().on(requestBuilder).invoke("build");

        this.compiled().on(resource)
                .invoke("get", this.compiled().getClass(API_PACK + ".HeaderParamsGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).headers().get("stringParam"), is(arrayContaining("val")));
        assertThat(requesterFactory.calls().get(0).headers().get("arrayParam"), is(arrayContaining("v1", "v2")));
    }
}