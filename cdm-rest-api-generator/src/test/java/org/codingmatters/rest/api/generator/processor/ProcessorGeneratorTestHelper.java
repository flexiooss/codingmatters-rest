package org.codingmatters.rest.api.generator.processor;

import org.codingmatters.rest.api.generator.ApiGenerator;
import org.codingmatters.rest.api.generator.ApiTypesGenerator;
import org.codingmatters.rest.api.generator.HandlersGenerator;
import org.codingmatters.rest.api.generator.ProcessorGenerator;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.json.JsonFrameworkGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private boolean printFileTree = false;

    public ProcessorGeneratorTestHelper(TemporaryFolder dir, FileHelper fileHelper) {
        this.dir = dir;
        this.fileHelper = fileHelper;
    }

    public ProcessorGeneratorTestHelper printFileTree(boolean print) {
        this.printFileTree = print;
        return this;
    }

    public ProcessorGeneratorTestHelper setUpWithResource(String ramlRessource) throws Exception {
        Spec anAlreadyDefinedValueObject = new Spec.Builder()
                .addValue(ValueSpec.valueSpec()
                        .name("AnAlreadyDefinedValueObject")
                )
                .build();
        new SpecCodeGenerator(anAlreadyDefinedValueObject,
                "org.codingmatters", this.dir.getRoot()).generate();
        new JsonFrameworkGenerator(anAlreadyDefinedValueObject, "org.codingmatters", this.dir.getRoot()).generate();

        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource(ramlRessource));
        Spec typesSpec = new ApiTypesGenerator().generate(raml);
        new SpecCodeGenerator(typesSpec, ProcessorGeneratorTestHelper.TYPES_PACK, this.dir.getRoot()).generate();
        new JsonFrameworkGenerator(typesSpec, ProcessorGeneratorTestHelper.TYPES_PACK, this.dir.getRoot()).generate();

        Spec apiSpec = new ApiGenerator(ProcessorGeneratorTestHelper.TYPES_PACK).generate(raml);
        new SpecCodeGenerator(apiSpec, ProcessorGeneratorTestHelper.API_PACK, this.dir.getRoot()).generate();
        new HandlersGenerator(ProcessorGeneratorTestHelper.API_PACK, ProcessorGeneratorTestHelper.TYPES_PACK, ProcessorGeneratorTestHelper.API_PACK, this.dir.getRoot()).generate(raml);

        new ProcessorGenerator(ProcessorGeneratorTestHelper.SERVER_PACK, ProcessorGeneratorTestHelper.TYPES_PACK, ProcessorGeneratorTestHelper.API_PACK, this.dir.getRoot()).generate(raml);

        if(this.printFileTree) {
            this.printTree(this.dir.getRoot(), "");
        }

        this.compiled = CompiledCode.builder()
                .classpath(CompiledCode.findLibraryInClasspath("cdm-rest-api"))
                .classpath(CompiledCode.findLibraryInClasspath("cdm-value-objects-values"))
                .classpath(CompiledCode.findLibraryInClasspath("jackson-core"))
                .classpath(CompiledCode.findLibraryInClasspath("slf4j-api"))
                .source(this.dir.getRoot())
                .compile();
        return this;
    }

    private void printTree(File root, String prefix) {
        if(root.isFile()) {
            System.out.println(prefix + "- " + root.getName());
        } else {
            System.out.println(prefix + "+ " + root.getName());
            List<File> files = Arrays.asList(root.listFiles());
            Collections.sort(files, Comparator.comparing(o -> o.getName())) ;
            for (File file : files) {
                this.printTree(file, prefix + "\t");
            }

        }
    }

    public CompiledCode compiled() {
        return compiled;
    }
}
