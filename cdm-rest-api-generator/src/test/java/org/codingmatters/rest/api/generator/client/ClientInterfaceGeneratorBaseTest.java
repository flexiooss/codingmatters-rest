package org.codingmatters.rest.api.generator.client;

import org.codingmatters.rest.api.generator.ClientInterfaceGenerator;
import org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper;
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
import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.junit.Assert.assertThat;

public class ClientInterfaceGeneratorBaseTest {

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

        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();

        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "SimpleResourceTreeAPIClient.java");
    }

    @Test
    public void clientInterface() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient"),
                aPublic().interface_()
        );
    }

    @Test
    public void clientResourceMethods() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient").getMethod("rootResource"),
                aMethod().withoutParameters().returning(this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource"))
        );
    }

    @Test
    public void ressourceInterfaces() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource"),
                aStatic().public_().interface_()
        );

        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource"),
                aStatic().public_().interface_()
        );

        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$FirstResource"),
                aStatic().public_().interface_()
        );

        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$SecondResource"),
                aStatic().public_().interface_()
        );
    }

    @Test
    public void resourcesResourceMethods() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource").getMethod("middleResource"),
                aMethod().withoutParameters().returning(this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource"))
        );
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource").getMethod("firstResource"),
                aMethod().withoutParameters().returning(this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$FirstResource"))
        );
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource").getMethod("secondResource"),
                aMethod().withoutParameters().returning(this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$SecondResource"))
        );
    }

    @Test
    public void resourceMethodMethods() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource"),
                aStatic().public_().interface_().with(
                        aMethod().named("get")
                                .withParameters(this.compiled.getClass(API_PACK + ".RootResourceGetRequest"))
                                .returning(this.compiled.getClass(API_PACK + ".RootResourceGetResponse"))
                )
        );
    }
}