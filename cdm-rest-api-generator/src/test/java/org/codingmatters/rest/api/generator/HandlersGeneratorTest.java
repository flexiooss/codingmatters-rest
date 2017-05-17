package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.generator.util.Helper;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.reflect.ReflectMatchers;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.io.*;
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
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(Helper.fileResource("handlers/handlers.raml"));
        Spec typesSpec = new ApiTypesGenerator().generate(raml);
        new SpecCodeGenerator(typesSpec, TYPES_PACK, this.dir.getRoot()).generate();

        Spec apiSpec = new ApiGenerator(TYPES_PACK).generate(raml);
        new SpecCodeGenerator(apiSpec, API_PACK, this.dir.getRoot()).generate();

        new HandlersGenerator(SERVER_PACK, TYPES_PACK, API_PACK, this.dir.getRoot()).generate(raml);

        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();

        this.printContent("", this.dir.getRoot());
        this.printFile(this.dir.getRoot(), "TestAPIHandlers.java");
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
                )
        );
    }

    private void printContent(String prefix, File root) {
        if(root.isDirectory()) {
            System.out.println(prefix + " + " + root.getName());
            for (File file : root.listFiles()) {
                this.printContent(prefix + "   ", file);
            }

        } else if(root.getName().endsWith(".java")){
            System.out.println(prefix + "   " + root.getName());
        }
    }

    private void printFile(File root, String name) throws IOException {
        if(root.getName().equals(name)) {
            System.out.println("FILE CONTENT - " + root.getAbsolutePath());
            try(InputStream in = new FileInputStream(root) ; Reader reader = new InputStreamReader(in)) {
                char [] buffer = new char[1024];
                StringBuilder content = new StringBuilder();
                for(int read  = reader.read(buffer) ; read != -1 ; read = reader.read(buffer)) {
                    content.append(buffer, 0, read);
                }
                System.out.println(content);
            }
            System.out.println("--------------------------------");
        } else if(root.listFiles() != null) {
            for (File file : root.listFiles()) {
                this.printFile(file, name);
            }
        }
    }
}