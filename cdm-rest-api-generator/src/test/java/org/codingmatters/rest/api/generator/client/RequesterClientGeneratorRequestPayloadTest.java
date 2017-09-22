package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.generator.ClientInterfaceGenerator;
import org.codingmatters.rest.api.generator.ClientRequesterImplementation;
import org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper;
import org.codingmatters.rest.api.generator.client.support.TestRequesterFactory;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorRequestPayloadTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("processor/processor-request.raml"));
        ClientGeneratorHelper.generateBase(raml, this.dir.getRoot());

        new ClientInterfaceGenerator(CLIENT_PACK, API_PACK, this.dir.getRoot()).generate(raml);
        new ClientRequesterImplementation(CLIENT_PACK, API_PACK, TYPES_PACK, this.dir.getRoot()).generate(raml);

        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIRequesterClient.java");
        this.fileHelper.printFile(this.dir.getRoot(), "PayloadClient.java");
        this.fileHelper.printFile(this.dir.getRoot(), "PayloadPostRequest.java");

        this.compiled = ClientGeneratorHelper.compile(this.dir.getRoot());
    }

    @Test
    public void payload() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.POST, 200);

        Object client = this.compiled.getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.compiled.on(client).invoke("payload");

        Object requestBuilder = this.compiled
                .onClass(API_PACK + ".PayloadPostRequest")
                .invoke("builder");
        Object req = this.compiled.on(this.compiled.on(this.compiled.onClass(TYPES_PACK + ".Req").invoke("builder"))
                .invoke("prop", String.class).with("val")).invoke("build");
        this.compiled.on(requestBuilder).invoke("payload", this.compiled.getClass(TYPES_PACK + ".Req")).with(req);

        Object request = this.compiled.on(requestBuilder).invoke("build");

        this.compiled.on(resource)
                .invoke("post", this.compiled.getClass(API_PACK + ".PayloadPostRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.POST));
        assertThat(requesterFactory.calls().get(0).requestContentType(), is("application/json"));
        assertThat(new String(requesterFactory.calls().get(0).requestBody()), is("{\"prop\":\"val\"}"));
    }
}