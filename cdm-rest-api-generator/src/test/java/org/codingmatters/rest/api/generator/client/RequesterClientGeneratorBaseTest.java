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

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.API_PACK;
import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.CLIENT_PACK;
import static org.codingmatters.tests.reflect.ReflectMatchers.aConstructor;
import static org.codingmatters.tests.reflect.ReflectMatchers.aPublic;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
        new ClientRequesterImplementation(CLIENT_PACK, API_PACK, this.dir.getRoot()).generate(raml);

        this.fileHelper.printJavaContent("", this.dir.getRoot());
//        this.fileHelper.printFile(this.dir.getRoot(), "SimpleResourceTreeAPIClient.java");
        this.fileHelper.printFile(this.dir.getRoot(), "SimpleResourceTreeAPIRequesterClient.java");
        this.fileHelper.printFile(this.dir.getRoot(), "RootResourceClient.java");
//        this.fileHelper.printFile(this.dir.getRoot(), "MiddleResourceClient.java");
//        this.fileHelper.printFile(this.dir.getRoot(), "FirstResourceClient.java");

        this.compiled = CompiledCode.builder()
                .classpath(CompiledCode.findLibraryInClasspath("jackson-core"))
                .classpath(CompiledCode.findLibraryInClasspath("cdm-rest-client-api"))
                .source(this.dir.getRoot()).compile();

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
                is(aPublic().class_().with(aConstructor().withParameters(RequesterFactory.class, JsonFactory.class, String.class)))
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
}
