package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.generator.client.support.RequesterClientTestSetup;
import org.codingmatters.rest.api.generator.client.support.TestRequesterFactory;
import org.codingmatters.tests.compile.FileHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Array;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.API_PACK;
import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.CLIENT_PACK;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorResponsePayloadTest {
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
        this.fileHelper.printFile(this.dir.getRoot(), "PayloadListGetResponse.java");
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

        Object client = this.support.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.support.compiled().on(client).invoke("payloadList");

        Object requestBuilder = this.support.compiled()
                .onClass(API_PACK + ".PayloadListGetRequest")
                .invoke("builder");
        Object request = this.support.compiled().on(requestBuilder).invoke("build");

        Object response = this.support.compiled().on(resource)
                .invoke("get", this.support.compiled().getClass(API_PACK + ".PayloadListGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/payload-list"));

        Object status200 = this.support.compiled().on(response).castedTo(API_PACK + ".PayloadListGetResponse").invoke("status200");
        assertThat(
                status200,
                is(notNullValue(this.support.compiled().getClass(API_PACK + ".PayloadListGetResponse")))
        );

        Object payload = this.support.compiled().on(status200).castedTo(API_PACK + ".payloadlistgetresponse.Status200").invoke("payload");
        Object payloadAsArray = this.support.compiled().on(payload).invoke("toArray");

        assertThat(
                this.support.compiled().on(Array.get(payloadAsArray, 0)).castedTo(Object.class.getName()).invoke("toString"),
                is("Resp{prop=v1}")
        );
        assertThat(
                this.support.compiled().on(Array.get(payloadAsArray, 1)).castedTo(Object.class.getName()).invoke("toString"),
                is("Resp{prop=v2}")
        );
    }
}
