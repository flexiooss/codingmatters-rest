package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.generator.util.Helper;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

/**
 * Created by nelt on 5/15/17.
 */
public class HandlersGeneratorTest {

    public static final String TYPES_PACK = "org.generated.types";
    public static final String API_PACK = "org.generated.api";

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

        new HandlersGenerator(API_PACK, TYPES_PACK, this.dir.getRoot()).generate(raml);

        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
    }

    @Test
    public void name() throws Exception {

    }
}