package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.tests.utils.FileHelper;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

/**
 * Created by nelt on 5/25/17.
 */
public class ProcessorGeneratorTestHelper {
    static public String ROOT_PACK = "org.generated";
    static public String SERVER_PACK = ROOT_PACK + ".server";
    static public String API_PACK = ROOT_PACK + ".api";
    static public String TYPES_PACK = ROOT_PACK + ".types";

    public final TemporaryFolder dir;
    public final FileHelper fileHelper;
    private CompiledCode compiled;

    public ProcessorGeneratorTestHelper(TemporaryFolder dir, FileHelper fileHelper) {
        this.dir = dir;
        this.fileHelper = fileHelper;
    }

    public ProcessorGeneratorTestHelper setUpWithResource(String ramlRessource) throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource(ramlRessource));
        Spec typesSpec = new ApiTypesGenerator().generate(raml);
        new SpecCodeGenerator(typesSpec, ProcessorGeneratorTestHelper.TYPES_PACK, this.dir.getRoot()).generate();

        Spec apiSpec = new ApiGenerator(ProcessorGeneratorTestHelper.TYPES_PACK).generate(raml);
        new SpecCodeGenerator(apiSpec, ProcessorGeneratorTestHelper.API_PACK, this.dir.getRoot()).generate();
        new HandlersGenerator(ProcessorGeneratorTestHelper.SERVER_PACK, ProcessorGeneratorTestHelper.TYPES_PACK, ProcessorGeneratorTestHelper.API_PACK, this.dir.getRoot()).generate(raml);

        new ProcessorGenerator(ProcessorGeneratorTestHelper.SERVER_PACK, ProcessorGeneratorTestHelper.TYPES_PACK, ProcessorGeneratorTestHelper.API_PACK, this.dir.getRoot()).generate(raml);

        this.compiled = CompiledCode.builder()
                .classpath(CompiledCode.findLibraryInClasspath("cdm-rest-api"))
                .classpath(CompiledCode.findLibraryInClasspath("jackson-core"))
                .source(this.dir.getRoot())
                .compile();
        return this;
    }

    public CompiledCode compiled() {
        return compiled;
    }
}
