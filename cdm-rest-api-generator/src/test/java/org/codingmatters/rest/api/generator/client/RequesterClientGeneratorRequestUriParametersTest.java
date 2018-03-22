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

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.API_PACK;
import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.CLIENT_PACK;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorRequestUriParametersTest {
    public TemporaryFolder dir = new TemporaryFolder();
    public FileHelper fileHelper = new FileHelper();
    public RequesterClientTestSetup testSetup = new RequesterClientTestSetup("processor/processor-request.raml", this.dir, this.fileHelper);

    @Rule
    public RuleChain chain= RuleChain
            .outerRule(this.dir)
            .around(this.fileHelper)
            .around(this.testSetup);

    @Test
    public void uriParams() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.testSetup.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.testSetup.compiled().on(client).invoke("uriParams");

        Object requestBuilder = this.testSetup.compiled()
                .onClass(API_PACK + ".UriParamsGetRequest")
                .invoke("builder");

        this.testSetup.compiled().on(requestBuilder).invoke("param", String.class).with("val");

        Object request = this.testSetup.compiled().on(requestBuilder).invoke("build");

        this.testSetup.compiled().on(resource)
                .invoke("get", this.testSetup.compiled().getClass(API_PACK + ".UriParamsGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/uri-param/val"));
    }

    @Test
    public void twoUriParams() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.testSetup.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.testSetup.compiled().on(client).invoke("uriParams");
        resource = this.testSetup.compiled().on(resource).invoke("twoUriParams");

        Object requestBuilder = this.testSetup.compiled()
                .onClass(API_PACK + ".TwoUriParamsGetRequest")
                .invoke("builder");

        this.testSetup.compiled().on(requestBuilder).invoke("param", String.class).with("val");
        this.testSetup.compiled().on(requestBuilder).invoke("param2", String.class).with("val2");

        Object request = this.testSetup.compiled().on(requestBuilder).invoke("build");

        this.testSetup.compiled().on(resource)
                .invoke("get", this.testSetup.compiled().getClass(API_PACK + ".TwoUriParamsGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/uri-param/val/another/val2"));
    }

    @Test
    public void arrayUriParams() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.testSetup.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.testSetup.compiled().on(client).invoke("uriParams");
        resource = this.testSetup.compiled().on(resource).invoke("arrayUriParams");

        Object requestBuilder = this.testSetup.compiled()
                .onClass(API_PACK + ".ArrayUriParamsGetRequest")
                .invoke("builder");

        this.testSetup.compiled().on(requestBuilder).invoke("param", String[].class).with(new Object[] {new String [] {"v1", "v2"}});

        Object request = this.testSetup.compiled().on(requestBuilder).invoke("build");

        this.testSetup.compiled().on(resource)
                .invoke("get", this.testSetup.compiled().getClass(API_PACK + ".ArrayUriParamsGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/uri-param/v1/another-one/v2"));
    }
}