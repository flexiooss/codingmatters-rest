package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.client.test.TestRequesterFactory;
import org.codingmatters.rest.api.generator.client.support.RequesterClientTestSetup;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.API_PACK;
import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.CLIENT_PACK;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorRequestQueryParametersTest {
    public TemporaryFolder dir = new TemporaryFolder();
    public FileHelper fileHelper = new FileHelper();
    public RequesterClientTestSetup testSetup = new RequesterClientTestSetup("processor/processor-request.raml", this.dir, this.fileHelper);

    @Rule
    public RuleChain chain= RuleChain
            .outerRule(this.dir)
            .around(this.fileHelper)
            .around(this.testSetup);

    @Test
    public void queryParams() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        ClassLoaderHelper classes = this.testSetup.compiled().classLoader();

        ObjectHelper client = classes.get(CLIENT_PACK + ".TestAPIRequesterClient")
                .newInstance(RequesterFactory.class, JsonFactory.class, String.class)
                .with(requesterFactory, jsonFactory, baseUrl);

        ObjectHelper resource = client.call("queryParams");

        ObjectHelper request = classes.get(API_PACK + ".QueryParamsGetRequest").call("builder")
                .call("stringParam", String.class).with("val")
                .call("stringArrayParam", String[].class).with(new Object[] {new String[] {"v1", "v2"}})
                .call("build");

        resource.call("get", classes.get(API_PACK + ".QueryParamsGetRequest").get()).with(request.get());

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).parameters().get("stringParam"), is(arrayContaining("val")));
        assertThat(requesterFactory.calls().get(0).parameters().get("stringArrayParam"), is(arrayContaining("v1", "v2")));
    }

    @Test
    public void queryParamsWithBooleans() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        ClassLoaderHelper classes = this.testSetup.compiled().classLoader();

        ObjectHelper client = classes.get(CLIENT_PACK + ".TestAPIRequesterClient")
                .newInstance(RequesterFactory.class, JsonFactory.class, String.class)
                .with(requesterFactory, jsonFactory, baseUrl);

        ObjectHelper resource = client.call("queryParamsWithBooleans");

        ObjectHelper request = classes.get(API_PACK + ".QueryParamsWithBooleansGetRequest").call("builder")
                .call("booleanParam", Boolean.class).with(Boolean.TRUE)
                .call("booleanArrayParam", Boolean[].class).with(new Object[] {new Boolean[] {Boolean.TRUE, Boolean.FALSE}})
                .call("build");

        resource.call("get", classes.get(API_PACK + ".QueryParamsWithBooleansGetRequest").get()).with(request.get());

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).parameters().get("booleanParam"), is(arrayContaining("true")));
        assertThat(requesterFactory.calls().get(0).parameters().get("booleanArrayParam"), is(arrayContaining("true", "false")));
    }
}
