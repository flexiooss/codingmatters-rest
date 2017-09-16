package org.codingmatters.rest.api.generator;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.junit.Assert.assertThat;

public class ClientInterfaceGeneratorBaseTest {

    public static final String ROOT_PACK = "org.generated";
    public static final String TYPES_PACK = ROOT_PACK + ".types";
    public static final String API_PACK = ROOT_PACK + ".api";
    public static final String CLIENT_PACK = ROOT_PACK + ".client";

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("resources-without-method.raml"));
        Spec typesSpec = new ApiTypesGenerator().generate(raml);
        new SpecCodeGenerator(typesSpec, TYPES_PACK, this.dir.getRoot()).generate();

        Spec apiSpec = new ApiGenerator(TYPES_PACK).generate(raml);
        new SpecCodeGenerator(apiSpec, API_PACK, this.dir.getRoot()).generate();

        new ClientInterfaceGenerator(CLIENT_PACK, API_PACK, this.dir.getRoot()).generate(raml);

        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();

        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "ResourcesWithoutMethodAPIClient.java");
    }

    @Test
    public void clientInterface() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient"),
                aPublic().interface_()
        );
    }

    @Test
    public void clientResourceMethods() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient").getMethod("rootResource"),
                aMethod().withoutParameters().returning(this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource"))
        );
    }

    @Test
    public void ressourceInterfaces() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource"),
                aStatic().public_().interface_()
        );

        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource$MiddleResource"),
                aStatic().public_().interface_()
        );

        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource$MiddleResource$FirstResource"),
                aStatic().public_().interface_()
        );

        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource$MiddleResource$SecondResource"),
                aStatic().public_().interface_()
        );
    }

    @Test
    public void resourcesResourceMethods() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource").getMethod("middleResource"),
                aMethod().withoutParameters().returning(this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource$MiddleResource"))
        );
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource$MiddleResource").getMethod("firstResource"),
                aMethod().withoutParameters().returning(this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource$MiddleResource$FirstResource"))
        );
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource$MiddleResource").getMethod("secondResource"),
                aMethod().withoutParameters().returning(this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource$MiddleResource$SecondResource"))
        );
    }

    @Test
    public void resourceMethodMethods() throws Exception {
        assertThat(
                this.compiled.getClass(CLIENT_PACK + ".ResourcesWithoutMethodAPIClient$RootResource"),
                aStatic().public_().interface_().with(
                        aMethod().named("get")
                                .withParameters(this.compiled.getClass(API_PACK + ".RootResourceGetRequest"))
                                .returning(this.compiled.getClass(API_PACK + ".RootResourceGetResponse"))
                )
        );
    }
}