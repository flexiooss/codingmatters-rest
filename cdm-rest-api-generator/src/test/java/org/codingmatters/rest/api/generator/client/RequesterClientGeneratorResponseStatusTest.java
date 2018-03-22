package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.client.test.TestRequesterFactory;
import org.codingmatters.rest.api.generator.client.support.RequesterClientTestSetup;
import org.codingmatters.tests.compile.FileHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.API_PACK;
import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.CLIENT_PACK;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorResponseStatusTest {
    public TemporaryFolder dir = new TemporaryFolder();
    public FileHelper fileHelper = new FileHelper();
    public RequesterClientTestSetup support = new RequesterClientTestSetup("processor/processor-response.raml", this.dir, this.fileHelper);

    @Rule
    public RuleChain chain= RuleChain
            .outerRule(this.dir)
            .around(this.fileHelper)
            .around(this.support);

    @Before
    public void setUp() throws Exception {
        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "StatusClient.java");
    }

    @Test
    public void status200() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.support.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.support.compiled().on(client).invoke("status");

        Object requestBuilder = this.support.compiled()
                .onClass(API_PACK + ".StatusGetRequest")
                .invoke("builder");
        Object request = this.support.compiled().on(requestBuilder).invoke("build");

        Object response = this.support.compiled().on(resource)
                .invoke("get", this.support.compiled().getClass(API_PACK + ".StatusGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/status"));

        assertThat(
                this.support.compiled().on(response).castedTo(API_PACK + ".StatusGetResponse").invoke("status200"),
                is(notNullValue(this.support.compiled().getClass(API_PACK + ".StatusGetResponse")))
        );
        assertThat(
                this.support.compiled().on(response).castedTo(API_PACK + ".StatusGetResponse").invoke("status201"),
                is(nullValue())
        );
        assertThat(
                this.support.compiled().on(response).castedTo(API_PACK + ".StatusGetResponse").invoke("status202"),
                is(nullValue())
        );
    }

    @Test
    public void status201() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 201);

        Object client = this.support.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.support.compiled().on(client).invoke("status");

        Object requestBuilder = this.support.compiled()
                .onClass(API_PACK + ".StatusGetRequest")
                .invoke("builder");
        Object request = this.support.compiled().on(requestBuilder).invoke("build");

        Object response = this.support.compiled().on(resource)
                .invoke("get", this.support.compiled().getClass(API_PACK + ".StatusGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/status"));

        assertThat(
                this.support.compiled().on(response).castedTo(API_PACK + ".StatusGetResponse").invoke("status200"),
                is(nullValue())
        );
        assertThat(
                this.support.compiled().on(response).castedTo(API_PACK + ".StatusGetResponse").invoke("status201"),
                is(notNullValue(this.support.compiled().getClass(API_PACK + ".StatusGetResponse")))
        );
        assertThat(
                this.support.compiled().on(response).castedTo(API_PACK + ".StatusGetResponse").invoke("status202"),
                is(nullValue())
        );
    }

    @Test
    public void status202() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 202);

        Object client = this.support.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.support.compiled().on(client).invoke("status");

        Object requestBuilder = this.support.compiled()
                .onClass(API_PACK + ".StatusGetRequest")
                .invoke("builder");
        Object request = this.support.compiled().on(requestBuilder).invoke("build");

        Object response = this.support.compiled().on(resource)
                .invoke("get", this.support.compiled().getClass(API_PACK + ".StatusGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/status"));

        assertThat(
                this.support.compiled().on(response).castedTo(API_PACK + ".StatusGetResponse").invoke("status200"),
                is(nullValue())
        );
        assertThat(
                this.support.compiled().on(response).castedTo(API_PACK + ".StatusGetResponse").invoke("status201"),
                is(nullValue())
        );
        assertThat(
                this.support.compiled().on(response).castedTo(API_PACK + ".StatusGetResponse").invoke("status202"),
                is(notNullValue(this.support.compiled().getClass(API_PACK + ".StatusGetResponse")))
        );
    }
}
