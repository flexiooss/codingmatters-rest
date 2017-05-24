package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.tests.utils.FileHelper;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import static org.codingmatters.tests.reflect.ReflectMatchers.aPublic;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/23/17.
 */
public class ProcessorGeneratorTest {
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
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("processor/processor-base.raml"));
        Spec typesSpec = new ApiTypesGenerator().generate(raml);
        new SpecCodeGenerator(typesSpec, TYPES_PACK, this.dir.getRoot()).generate();

        Spec apiSpec = new ApiGenerator(TYPES_PACK).generate(raml);
        new SpecCodeGenerator(apiSpec, API_PACK, this.dir.getRoot()).generate();
        new HandlersGenerator(SERVER_PACK, TYPES_PACK, API_PACK, this.dir.getRoot()).generate(raml);

        new ProcessorGenerator(SERVER_PACK, TYPES_PACK, API_PACK, this.dir.getRoot()).generate(raml);

        this.compiled = CompiledCode.builder()
                .classpath(CompiledCode.findLibraryInClasspath("cdm-rest-api"))
                .source(this.dir.getRoot())
                .compile();

        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIProcessor.java");
    }

    @Test
    public void processorClass() throws Exception {
        assertThat(
                this.compiled.getClass("org.generated.server.TestAPIProcessor"),
                is(
                        aPublic().class_().implementing(Processor.class)
                )
        );
    }
}