package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.generator.client.support.TestRequesterFactory;
import org.junit.Test;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorRequestPayloadTest extends AbstractRequesterClientGeneratorTest {

    @Test
    public void payload() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.POST, 200);

        Object client = this.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.compiled().on(client).invoke("payload");

        Object requestBuilder = this.compiled()
                .onClass(API_PACK + ".PayloadPostRequest")
                .invoke("builder");
        Object req = this.compiled().on(this.compiled().on(this.compiled().onClass(TYPES_PACK + ".Req").invoke("builder"))
                .invoke("prop", String.class).with("val")).invoke("build");
        this.compiled().on(requestBuilder).invoke("payload", this.compiled().getClass(TYPES_PACK + ".Req")).with(req);

        Object request = this.compiled().on(requestBuilder).invoke("build");

        this.compiled().on(resource)
                .invoke("post", this.compiled().getClass(API_PACK + ".PayloadPostRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.POST));
        assertThat(requesterFactory.calls().get(0).requestContentType(), is("application/json"));
        assertThat(new String(requesterFactory.calls().get(0).requestBody()), is("{\"prop\":\"val\"}"));
    }
}