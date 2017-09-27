package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.generator.client.support.RequesterClientTestSetup;
import org.codingmatters.rest.api.generator.client.support.TestRequesterFactory;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectArrayHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.API_PACK;
import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.CLIENT_PACK;
import static org.codingmatters.tests.compile.helpers.ClassLoaderHelper.c;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorResponsePayloadTest {
    public TemporaryFolder dir = new TemporaryFolder();
    public FileHelper fileHelper = new FileHelper();
    public RequesterClientTestSetup support = new RequesterClientTestSetup("processor/processor-response.raml", this.dir, this.fileHelper);

    private ClassLoaderHelper classes;

    @Rule
    public RuleChain chain= RuleChain
            .outerRule(this.dir)
            .around(this.fileHelper)
            .around(this.support);

    @Before
    public void setUp() throws Exception {
        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "PayloadListGetResponse.java");
        this.classes = this.support.compiled().classLoader();
    }

    @Test
    public void payload() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200, "{\"prop\":\"value\"}".getBytes("UTF-8"));

        Object client = this.support.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.support.compiled().on(client).invoke("payload");

        Object requestBuilder = this.support.compiled()
                .onClass(API_PACK + ".PayloadGetRequest")
                .invoke("builder");
        Object request = this.support.compiled().on(requestBuilder).invoke("build");

        Object response = this.support.compiled().on(resource)
                .invoke("get", this.support.compiled().getClass(API_PACK + ".PayloadGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/payload"));

        Object status200 = this.support.compiled().on(response).castedTo(API_PACK + ".PayloadGetResponse").invoke("status200");
        assertThat(
                status200,
                is(notNullValue(this.support.compiled().getClass(API_PACK + ".PayloadGetResponse")))
        );

        Object payload = this.support.compiled().on(status200).castedTo(API_PACK + ".payloadgetresponse.Status200").invoke("payload");
        assertThat(
                this.support.compiled().on(payload).castedTo(Object.class.getName()).invoke("toString"),
                is("Resp{prop=value}")
        );
    }



    @Test
    public void payloadList() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200, "[{\"prop\":\"v1\"},{\"prop\":\"v2\"}]".getBytes("UTF-8"));


        ObjectHelper resource =
                this.classes.get(CLIENT_PACK + ".TestAPIRequesterClient")
                    .newInstance(c(RequesterFactory.class), c(JsonFactory.class), c(String.class))
                    .with(requesterFactory, jsonFactory, baseUrl)
                .call("payloadList");

        ObjectHelper response = resource
                .call("get", c(API_PACK + ".PayloadListGetRequest"))
                .with(this.classes.get(API_PACK + ".PayloadListGetRequest")
                        .call("builder")
                        .call("build").get()
                );

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/payload-list"));

        ObjectArrayHelper payload =
                response.as(API_PACK + ".PayloadListGetResponse")
                        .call("status200")
                        .as(API_PACK + ".payloadlistgetresponse.Status200")
                            .call("payload")
                            .call("toArray").asArray();

        assertThat(
                payload.get(0).as(Object.class).call("toString").get(),
                is("Resp{prop=v1}")
        );
        assertThat(
                payload.get(1).as(Object.class).call("toString").get(),
                is("Resp{prop=v2}")
        );
    }
}
