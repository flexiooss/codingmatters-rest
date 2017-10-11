package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.generator.processor.ProcessorGeneratorTestHelper;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.json.JsonFrameworkGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.*;
import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ClientHandlerImplementationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private ClassLoaderHelper classes;


    @Before
    public void setUp() throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("simple-resource-tree.raml"));

        Spec typesSpec = new ApiTypesGenerator().generate(raml);
        new SpecCodeGenerator(typesSpec, ProcessorGeneratorTestHelper.TYPES_PACK, this.dir.getRoot()).generate();
        new JsonFrameworkGenerator(typesSpec, ProcessorGeneratorTestHelper.TYPES_PACK, this.dir.getRoot()).generate();

        Spec apiSpec = new ApiGenerator(ProcessorGeneratorTestHelper.TYPES_PACK).generate(raml);
        new SpecCodeGenerator(apiSpec, ProcessorGeneratorTestHelper.API_PACK, this.dir.getRoot()).generate();
        new HandlersGenerator(ProcessorGeneratorTestHelper.API_PACK, ProcessorGeneratorTestHelper.TYPES_PACK, ProcessorGeneratorTestHelper.API_PACK, this.dir.getRoot()).generate(raml);

        new ClientInterfaceGenerator(CLIENT_PACK, API_PACK, this.dir.getRoot()).generate(raml);
        new ClientHandlerImplementation(CLIENT_PACK, API_PACK, TYPES_PACK, this.dir.getRoot()).generate(raml);

        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "SimpleResourceTreeAPIHandlersClient.java");

        CompiledCode compiled = CompiledCode.builder()
                .classpath(CompiledCode.findLibraryInClasspath("cdm-rest-api"))
                .classpath(CompiledCode.findLibraryInClasspath("jackson-core"))
                .classpath(CompiledCode.findLibraryInClasspath("slf4j-api"))
                .source(this.dir.getRoot())
                .compile();


        this.classes = compiled.classLoader();
    }

    @Test
    public void implementsClient() throws Exception {
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIHandlersClient").get(),
                is(aPublic().class_().implementing(this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient").get()))
        );
    }

    @Test
    public void resourceImplementation() throws Exception {
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIHandlersClient$RootResourceImpl").get(),
                is(aPrivate().class_().implementing(this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource").get()))
        );
        //MiddleResource
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIHandlersClient$RootResourceImpl$MiddleResourceImpl").get(),
                is(aPrivate().class_().implementing(this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource").get()))
        );
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIHandlersClient$RootResourceImpl$MiddleResourceImpl$FirstResourceImpl").get(),
                is(aPrivate().class_().implementing(this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$FirstResource").get()))
        );
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIHandlersClient$RootResourceImpl$MiddleResourceImpl$SecondResourceImpl").get(),
                is(aPrivate().class_().implementing(this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource$MiddleResource$SecondResource").get()))
        );
    }

    @Test
    public void fieldsAndConstructor() throws Exception {
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIHandlersClient").get(),
                is(aPublic().class_()
                        .with(aPublic().constructor().withParameters(this.classes.get(API_PACK + ".SimpleResourceTreeAPIHandlers").get(), ExecutorService.class))
                        .with(aPrivate().field()
                                .final_()
                                .named("handlers")
                                .withType(this.classes.get(API_PACK + ".SimpleResourceTreeAPIHandlers").get())
                        )
                        .with(aPrivate().field()
                                .final_()
                                .named("executor")
                                .withType(ExecutorService.class)
                        )
                )
        );
    }

    @Test
    public void callUtilMethod() throws Exception {
        assertThat(
                this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIHandlersClient").get(),
                is(aPublic().class_().with(aPrivate().method()
                        .withVariable(variableType().named("T"))
                        .returning(variableType().named("T"))
                        .named("call")
                        .withParameters(
                                genericType().baseClass(Callable.class).withParameters(typeParameter().named("T")),
                                nonGenericType().baseClass(String.class)
                        )
                        .throwing(IOException.class)
                        )
                )
        );
    }

    @Test
    public void callWithRequest() throws Exception {
        ObjectHelper client = this.createClient();

        assertThat(
                client.call("rootResource").as(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource")
                        .call("get", this.classes.get(API_PACK + ".RootResourceGetRequest").get())
                        .with(this.classes.get(API_PACK + ".RootResourceGetRequest").call("builder").call("build").get())
                        .get().toString(),
                is("RootResourceGetResponse{}")
        );
    }

    @Test
    public void callWithRequestBuilder() throws Exception {
        ObjectHelper client = this.createClient();

        Consumer consumer = builder -> {};

        assertThat(
                client.call("rootResource").as(CLIENT_PACK + ".SimpleResourceTreeAPIClient$RootResource")
                        .call("get", Consumer.class)
                        .with(consumer)
                        .get().toString(),
                is("RootResourceGetResponse{}")
        );
    }

    private ObjectHelper createClient() {
        Function rootResourceGetHandler = o -> classes.get(API_PACK + ".RootResourceGetResponse").call("builder").call("build").get();
        Function firstResourceGetHandler = o -> classes.get(API_PACK + ".FirstResourceGetResponse").call("builder").call("build").get();
        Function secondResourceGetHandler = o -> classes.get(API_PACK + ".SecondResourceGetResponse").call("builder").call("build").get();

        ObjectHelper handlers = this.classes.get(API_PACK + ".SimpleResourceTreeAPIHandlers$Builder").newInstance()
                .call("rootResourceGetHandler", Function.class).with(rootResourceGetHandler)
                .call("firstResourceGetHandler", Function.class).with(firstResourceGetHandler)
                .call("secondResourceGetHandler", Function.class).with(secondResourceGetHandler)
                .call("build");
        ExecutorService executor = Executors.newSingleThreadExecutor();

        return this.classes.get(CLIENT_PACK + ".SimpleResourceTreeAPIHandlersClient")
                .newInstance(this.classes.get(API_PACK + ".SimpleResourceTreeAPIHandlers").get(), ExecutorService.class)
                .with(handlers.get(), executor);
    }
}