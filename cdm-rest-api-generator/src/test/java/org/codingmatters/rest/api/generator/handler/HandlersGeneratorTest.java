package org.codingmatters.rest.api.generator.handler;

import org.codingmatters.rest.api.generator.ApiGenerator;
import org.codingmatters.rest.api.generator.ApiTypesGenerator;
import org.codingmatters.rest.api.generator.HandlersGenerator;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.reflect.ReflectMatchers;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.util.function.Function;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/15/17.
 */
public class HandlersGeneratorTest {

    public static final String ROOT_PACK = "org.generated";
    public static final String TYPES_PACK = ROOT_PACK + ".types";
    public static final String API_PACK = ROOT_PACK + ".api";
    public static final String SERVER_PACK = ROOT_PACK + ".server";

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("handlers/handlers.raml"));
        Spec typesSpec = new ApiTypesGenerator().generate(raml);
        new SpecCodeGenerator(typesSpec, TYPES_PACK, this.dir.getRoot()).generate();

        Spec apiSpec = new ApiGenerator(TYPES_PACK).generate(raml);
        new SpecCodeGenerator(apiSpec, API_PACK, this.dir.getRoot()).generate();

        new HandlersGenerator(SERVER_PACK, TYPES_PACK, API_PACK, this.dir.getRoot()).generate(raml);

        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();

        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIHandlers.java");
    }

    @Test
    public void interface_() throws Exception {
        assertThat(
            this.compiled.getClass(SERVER_PACK + ".TestAPIHandlers"),
            is(aPublic().interface_()
                    .with(aPublic().method()
                            .named("rootGetHandler")
                            .returning(genericType()
                                    .baseClass(Function.class)
                                    .withParameters(
                                            classTypeParameter(this.compiled.getClass(API_PACK + ".RootGetRequest")),
                                            classTypeParameter(this.compiled.getClass(API_PACK + ".RootGetResponse"))
                                    ))
                    )
                    .with(aPublic().method()
                            .named("rootPostHandler")
                            .returning(genericType()
                                    .baseClass(Function.class)
                                    .withParameters(
                                            classTypeParameter(this.compiled.getClass(API_PACK + ".RootPostRequest")),
                                            classTypeParameter(this.compiled.getClass(API_PACK + ".RootPostResponse"))
                                    ))
                    )
                    .with(aPublic().method()
                            .named("subGetHandler")
                            .returning(genericType()
                                    .baseClass(Function.class)
                                    .withParameters(
                                            classTypeParameter(this.compiled.getClass(API_PACK + ".SubGetRequest")),
                                            classTypeParameter(this.compiled.getClass(API_PACK + ".SubGetResponse"))
                                    ))
                    )

            )
        );
    }

    @Test
    public void builder() throws Exception {
        assertThat(
                this.compiled.getClass(SERVER_PACK + ".TestAPIHandlers$Builder"),
                is(aStatic().public_().class_()
                        .with(aPublic().method().named("rootGetHandler")
                                .withParameters(genericType()
                                        .baseClass(Function.class)
                                        .withParameters(
                                                classTypeParameter(this.compiled.getClass(API_PACK + ".RootGetRequest")),
                                                classTypeParameter(this.compiled.getClass(API_PACK + ".RootGetResponse"))
                                        ))
                                .returning(this.compiled.getClass(SERVER_PACK + ".TestAPIHandlers$Builder"))
                        )
                        .with(aPublic().method().named("rootPostHandler")
                                .withParameters(genericType()
                                        .baseClass(Function.class)
                                        .withParameters(
                                                classTypeParameter(this.compiled.getClass(API_PACK + ".RootPostRequest")),
                                                classTypeParameter(this.compiled.getClass(API_PACK + ".RootPostResponse"))
                                        ))
                                .returning(this.compiled.getClass(SERVER_PACK + ".TestAPIHandlers$Builder"))
                        )
                        .with(aPublic().method().named("subGetHandler")
                                .withParameters(genericType()
                                        .baseClass(Function.class)
                                        .withParameters(
                                                classTypeParameter(this.compiled.getClass(API_PACK + ".SubGetRequest")),
                                                classTypeParameter(this.compiled.getClass(API_PACK + ".SubGetResponse"))
                                        ))
                                .returning(this.compiled.getClass(SERVER_PACK + ".TestAPIHandlers$Builder"))
                        )
                        .with(aPublic().method().named("build")
                                .withoutParameters()
                                .returning(this.compiled.getClass(SERVER_PACK + ".TestAPIHandlers")))
                )
        );
    }

    @Test
    public void defaultImplementation() throws Exception {
        assertThat(
                this.compiled.getClass(SERVER_PACK + ".TestAPIHandlers$Builder$DefaultImpl"),
                is(
                        aStatic().private_().class_()
                                .implementing(this.compiled.getClass(SERVER_PACK + ".TestAPIHandlers"))
                                .with(aPrivate().constructor().withParameters(
                                        genericType()
                                                .baseClass(Function.class)
                                                .withParameters(
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".RootGetRequest")),
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".RootGetResponse"))
                                                ),
                                        genericType()
                                                .baseClass(Function.class)
                                                .withParameters(
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".RootPostRequest")),
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".RootPostResponse"))
                                                ),
                                        genericType()
                                                .baseClass(Function.class)
                                                .withParameters(
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".SubGetRequest")),
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".SubGetResponse"))
                                                )
                                ))
                                .with(ReflectMatchers.aPrivate().field().named("rootGetHandler")
                                        .withType(genericType()
                                                .baseClass(Function.class)
                                                .withParameters(
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".RootGetRequest")),
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".RootGetResponse"))
                                                )))
                                .with(ReflectMatchers.aPrivate().field().named("rootPostHandler")
                                        .withType(genericType()
                                                .baseClass(Function.class)
                                                .withParameters(
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".RootPostRequest")),
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".RootPostResponse"))
                                                )))
                                .with(ReflectMatchers.aPrivate().field().named("subGetHandler")
                                        .withType(genericType()
                                                .baseClass(Function.class)
                                                .withParameters(
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".SubGetRequest")),
                                                        classTypeParameter(this.compiled.getClass(API_PACK + ".SubGetResponse"))
                                                )))
                )
        );
    }

}