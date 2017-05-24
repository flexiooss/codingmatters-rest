package org.codingmatters.rest.api.generator;

import com.squareup.javapoet.TypeSpec;
import org.codingmatters.rest.api.generator.handlers.HandlersHelper;
import org.codingmatters.rest.api.generator.processors.ProcessorClass;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.io.IOException;

import static org.codingmatters.value.objects.generation.GenerationUtils.packageDir;
import static org.codingmatters.value.objects.generation.GenerationUtils.writeJavaFile;

/**
 * Created by nelt on 5/15/17.
 */
public class ProcessorGenerator {
    private final String serverPackage;
    private final String typesPackage;
    private final String apiPackage;
    private final Naming naming;
    private final File rootDirectory;
    private final HandlersHelper helper;

    public ProcessorGenerator(String serverPackage, String typesPackage, String apiPackage, File toDirectory) {
        this.serverPackage = serverPackage;
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
        this.rootDirectory = toDirectory;
        this.naming = new Naming();
        this.helper = new HandlersHelper(this.apiPackage, this.naming);
    }

    public void generate(RamlModelResult ramlModel) throws IOException {
        TypeSpec processorClass = new ProcessorClass(this.typesPackage, this.apiPackage, this.naming, this.helper).type(ramlModel);
        writeJavaFile(
                packageDir(this.rootDirectory, this.serverPackage),
                this.serverPackage,
                processorClass);
    }
}
