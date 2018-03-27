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
import static org.codingmatters.tests.reflect.ReflectMatchers.aConstructor;
import static org.codingmatters.tests.reflect.ReflectMatchers.aPublic;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorBaseTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("simple-resource-tree.raml"));
        ClientGeneratorHelper.generateBase(raml, this.dir.getRoot());

        new ClientInterfaceGenerator(CLIENT_PACK, API_PACK, this.dir.getRoot()).generate(raml);
        new ClientRequesterImplementation(CLIENT_PACK, API_PACK, TYPES_PACK, this.dir.getRoot()).generate(raml);

        this.fileHelper.printJavaContent("", this.dir.getRoot());
//        this.fileHelper.printFile(this.dir.getRoot(), "SimpleResourceTreeAPIRequesterClient.java");
        this.fileHelper.printFile(this.dir.getRoot(), "RootResourceClient.java");
        this.fileHelper.printFile(this.dir.getRoot(), "FirstResourceClient.java");
//        this.fileHelper.printFile(this.dir.getRoot(), "RootResourceGetRequest.java");

        this.compiled = ClientGeneratorHelper.compile(this.dir.getRoot());

    }

    @Test
    public void clientClass() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIRequesterClient"),
                is(aPublic().class_()
                        .implementing(this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient"))
                )
        );
    }

    @Test
    public void resourcesClass() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".resources.RootResourceClient"),
                is(aPublic().class_()
                        .implementing(this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource"))
                )
        );
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".resources.MiddleResourceClient"),
                is(aPublic().class_()
                        .implementing(this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource"))
                )
        );
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".resources.FirstResourceClient"),
                is(aPublic().class_()
                        .implementing(this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$FirstResource"))
                )
        );
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".resources.SecondResourceClient"),
                is(aPublic().class_()
                        .implementing(this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$SecondResource"))
                )
        );
    }

    @Test
    public void clientConstructor() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIRequesterClient"),
                is(aPublic().class_()
                        .with(aConstructor().withParameters(RequesterFactory.class, JsonFactory.class, String.class))
                        .with(aConstructor().withParameters(RequesterFactory.class, JsonFactory.class, UrlProvider.class))
                )
        );
    }

    @Test
    public void resourceChaining() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        Object client = this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        assertThat(client, is(notNullValue()));

        assertThat(
                this.compiled.on(client).invoke("rootResource"),
                is(notNullValue())
        );
        assertThat(
                this.compiled.on(this.compiled.on(client).invoke("rootResource")).invoke("middleResource"),
                is(notNullValue())
        );
        assertThat(
                this.compiled.on(this.compiled.on(this.compiled.on(client).invoke("rootResource")).invoke("middleResource")).invoke("firstResource"),
                is(notNullValue())
        );
        assertThat(
                this.compiled.on(this.compiled.on(this.compiled.on(client).invoke("rootResource")).invoke("middleResource")).invoke("secondResource"),
                is(notNullValue())
        );
    }

    @Test
    public void rootResourceGet() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);
        Object resource = this.compiled.on(client).invoke("rootResource");

        Object request = this.compiled
                .on(this.compiled
                        .onClass(API_PACK + ".RootResourceGetRequest")
                        .invoke("builder"))
                .invoke("build");

        Object response = this.compiled.on(resource)
                .invoke("get", this.compiled.getClass(API_PACK + ".RootResourceGetRequest"))
                .with(request);

        assertThat(response, is(notNullValue(this.compiled.getClass(API_PACK + ".RootResourceGetResponse"))));
        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/root"));
    }

    @Test
    public void firstResourceGet() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);
        Object resource = this.compiled.on(this.compiled.on(this.compiled.on(client)
                .invoke("rootResource"))
                .invoke("middleResource"))
                .invoke("firstResource");

        Object request = this.compiled
                .on(this.compiled
                        .onClass(API_PACK + ".FirstResourceGetRequest")
                        .invoke("builder"))
                .invoke("build");

        Object response = this.compiled.on(resource)
                .invoke("get", this.compiled.getClass(API_PACK + ".FirstResourceGetRequest"))
                .with(request);

        assertThat(response, is(notNullValue(this.compiled.getClass(API_PACK + ".FirstResourceGetResponse"))));
        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/root/middle/leaf-1"));
    }



    @Test
    public void rootResourceGetWithUrlProvider() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";
        UrlProvider urlProvider = () -> baseUrl;

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, UrlProvider.class)
                .newInstance(requesterFactory, jsonFactory, urlProvider);
        Object resource = this.compiled.on(client).invoke("rootResource");

        Object request = this.compiled
                .on(this.compiled
                        .onClass(API_PACK + ".RootResourceGetRequest")
                        .invoke("builder"))
                .invoke("build");

        Object response = this.compiled.on(resource)
                .invoke("get", this.compiled.getClass(API_PACK + ".RootResourceGetRequest"))
                .with(request);

        assertThat(response, is(notNullValue(this.compiled.getClass(API_PACK + ".RootResourceGetResponse"))));
        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/root"));
    }
}
