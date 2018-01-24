package org.codingmatters.rest.api.generator.client.support;

import org.codingmatters.rest.api.generator.ApiGenerator;
import org.codingmatters.rest.api.generator.ApiTypesGenerator;
import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.api.generator.processor.ProcessorGeneratorTestHelper;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.json.JsonFrameworkGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.io.IOException;

public class ClientGeneratorHelper {

    public static final String ROOT_PACK = "org.generated";
    public static final String TYPES_PACK = ROOT_PACK + ".types";
    public static final String API_PACK = ROOT_PACK + ".api";
    public static final String CLIENT_PACK = ROOT_PACK + ".client";

    public static void generateBase(RamlModelResult raml, File toDir) throws RamlSpecException, IOException {
        Spec typesSpec = new ApiTypesGenerator().generate(raml);
        new SpecCodeGenerator(typesSpec, TYPES_PACK, toDir).generate();
        new JsonFrameworkGenerator(typesSpec, ProcessorGeneratorTestHelper.TYPES_PACK, toDir).generate();

        Spec apiSpec = new ApiGenerator(TYPES_PACK).generate(raml);
        new SpecCodeGenerator(apiSpec, API_PACK, toDir).generate();
    }

    public static CompiledCode compile(File dir) throws Exception {
        return CompiledCode.builder()
                .classpath(CompiledCode.findLibraryInClasspath("jackson-core"))
                .classpath(CompiledCode.findLibraryInClasspath("cdm-value-objects-values"))
                .classpath(CompiledCode.findLibraryInClasspath("cdm-rest-client-api"))
                .classpath(CompiledCode.findLibraryInClasspath("cdm-rest-api"))
                .classpath(CompiledCode.findLibraryInClasspath("jackson-core"))
                .classpath(CompiledCode.findLibraryInClasspath("slf4j-api"))
                .source(dir).compile();
    }

}
