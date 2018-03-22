package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.client.test.TestRequesterFactory;
import org.codingmatters.rest.api.generator.client.support.RequesterClientTestSetup;
import org.codingmatters.tests.compile.FileHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorRequestPayloadTest {
    public TemporaryFolder dir = new TemporaryFolder();
    public FileHelper fileHelper = new FileHelper();
    public RequesterClientTestSetup testSetup = new RequesterClientTestSetup("processor/processor-request.raml", this.dir, this.fileHelper);

    @Rule
    public RuleChain chain= RuleChain
            .outerRule(this.dir)
            .around(this.fileHelper)
            .around(this.testSetup);

    @Test
    public void payload() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.POST, 200);

        Object client = this.testSetup.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.testSetup.compiled().on(client).invoke("payload");

        Object requestBuilder = this.testSetup.compiled()
                .onClass(API_PACK + ".PayloadPostRequest")
                .invoke("builder");
        Object req = this.testSetup.compiled().on(this.testSetup.compiled().on(this.testSetup.compiled().onClass(TYPES_PACK + ".Req").invoke("builder"))
                .invoke("prop", String.class).with("val")).invoke("build");
        this.testSetup.compiled().on(requestBuilder).invoke("payload", this.testSetup.compiled().getClass(TYPES_PACK + ".Req")).with(req);

        Object request = this.testSetup.compiled().on(requestBuilder).invoke("build");

        this.testSetup.compiled().on(resource)
                .invoke("post", this.testSetup.compiled().getClass(API_PACK + ".PayloadPostRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.POST));
        assertThat(requesterFactory.calls().get(0).requestContentType(), is("application/json"));
        assertThat(new String(requesterFactory.calls().get(0).requestBody()), is("{\"prop\":\"val\"}"));
    }
}