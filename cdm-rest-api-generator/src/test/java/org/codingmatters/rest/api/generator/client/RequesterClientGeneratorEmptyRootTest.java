package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.client.UrlProvider;
import org.codingmatters.rest.api.client.test.TestRequesterFactory;
import org.codingmatters.rest.api.generator.ClientInterfaceGenerator;
import org.codingmatters.rest.api.generator.ClientRequesterImplementation;
import org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

public class RequesterClientGeneratorEmptyRootTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("processor/processor-empty-root.raml"));
        ClientGeneratorHelper.generateBase(raml, this.dir.getRoot());

        new ClientInterfaceGenerator(CLIENT_PACK, API_PACK, this.dir.getRoot()).generate(raml);
        new ClientRequesterImplementation(CLIENT_PACK, API_PACK, TYPES_PACK, this.dir.getRoot()).generate(raml);

//        this.fileHelper.printJavaContent("", this.dir.getRoot());
//        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIRequesterClient.java");
//        this.fileHelper.printFile(this.dir.getRoot(), "ChildClient.java");

        this.compiled = ClientGeneratorHelper.compile(this.dir.getRoot());
    }


    @Test
    public void rootGet() throws Exception {
        UrlProvider baseUrl = () -> "https://path.to/me";
        TestRequesterFactory requesterFactory = new TestRequesterFactory(baseUrl);
        JsonFactory jsonFactory = new JsonFactory();

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.compiled.getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, UrlProvider.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);
        Object resource = this.compiled.on(client).invoke("root");

        Object request = this.compiled
                .on(this.compiled
                        .onClass(API_PACK + ".RootGetRequest")
                        .invoke("builder"))
                .invoke("build");

        Object response = this.compiled.on(resource)
                .invoke("get", this.compiled.getClass(API_PACK + ".RootGetRequest"))
                .with(request);

        assertThat(response, is(notNullValue(this.compiled.getClass(API_PACK + ".RootGetResponse"))));
        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/"));
    }
    @Test
    public void childGet() throws Exception {
        UrlProvider baseUrl = () -> "https://path.to/me";
        TestRequesterFactory requesterFactory = new TestRequesterFactory(baseUrl);
        JsonFactory jsonFactory = new JsonFactory();

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.compiled.getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, UrlProvider.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);
        Object resource = this.compiled.on(this.compiled.on(client)
                .invoke("root"))
                .invoke("child");

        Object request = this.compiled
                .on(this.compiled
                        .onClass(API_PACK + ".ChildGetRequest")
                        .invoke("builder"))
                .invoke("build");

        Object response = this.compiled.on(resource)
                .invoke("get", this.compiled.getClass(API_PACK + ".ChildGetRequest"))
                .with(request);

        assertThat(response, is(notNullValue(this.compiled.getClass(API_PACK + ".ChildGetResponse"))));
        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/child"));
    }
}
