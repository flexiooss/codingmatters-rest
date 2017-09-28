package org.codingmatters.rest.api.generator.client;

import org.codingmatters.rest.api.generator.ClientInterfaceGenerator;
import org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.io.IOException;
import java.util.function.Consumer;

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
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("simple-resource-tree.raml"));
        ClientGeneratorHelper.generateBase(raml, this.dir.getRoot());

        new ClientInterfaceGenerator(CLIENT_PACK, API_PACK, this.dir.getRoot()).generate(raml);

        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
        this.classes = this.compiled.classLoader();

        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "SimpleResourceTreeAPIClient.java");
    }

    @Test
    public void clientInterface() throws Exception {
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient").get(),
                aPublic().interface_()
        );
    }

    @Test
    public void clientResourceMethods() throws Exception {
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient").getMethod("rootResource"),
                aMethod().withoutParameters().returning(this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource").get())
        );
    }

    @Test
    public void ressourceInterfaces() throws Exception {
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource").get(),
                aStatic().public_().interface_()
        );

        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource").get(),
                aStatic().public_().interface_()
        );

        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$FirstResource").get(),
                aStatic().public_().interface_()
        );

        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$SecondResource").get(),
                aStatic().public_().interface_()
        );
    }

    @Test
    public void resourcesResourceMethods() throws Exception {
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource").getMethod("middleResource"),
                aMethod().withoutParameters().returning(this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource").get())
        );
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource").getMethod("firstResource"),
                aMethod().withoutParameters().returning(this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$FirstResource").get())
        );
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource").getMethod("secondResource"),
                aMethod().withoutParameters().returning(this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$SecondResource").get())
        );
    }

    @Test
    public void resourceMethodMethods() throws Exception {
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource").get(),
                aStatic().public_().interface_().with(
                        aMethod().named("get")
                                .withParameters(this.classes.get(API_PACK + ".RootResourceGetRequest").get())
                                .returning(this.classes.get(API_PACK + ".RootResourceGetResponse").get())
                                .throwing(IOException.class)
                )
        );
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource").get(),
                aStatic().public_().interface_().with(
                        aMethod().named("get")
                                .withParameters(
                                        genericType().baseClass(Consumer.class)
                                                .withParameters(typeParameter()
                                                        .aClass(this.classes.get(API_PACK + ".RootResourceGetRequest$Builder").get())
                                                )
                                )
                                .returning(this.classes.get(API_PACK + ".RootResourceGetResponse").get())
                                .throwing(IOException.class)
                )
        );
    }
}