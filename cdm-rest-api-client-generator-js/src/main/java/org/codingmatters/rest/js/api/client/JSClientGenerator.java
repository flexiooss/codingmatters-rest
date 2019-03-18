package org.codingmatters.rest.js.api.client;

import org.codingmatters.rest.js.api.client.visitors.RamlApiPreProcessor;
import org.codingmatters.rest.parser.RamlParser;
import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesGenerator;

import java.io.File;

public class JSClientGenerator {

    private final File rootDirectory;
    private final String typesPackage;

    public JSClientGenerator( File rootDirectory, String typesPackage ) {
        this.rootDirectory = rootDirectory;
        this.typesPackage = typesPackage;
    }

    public void generateClientApi( String ramlFilePath ) throws ProcessingException, GenerationException {
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();
        RamlParser ramlParser = new RamlParser();
        ParsedRaml parsedRaml = ramlParser.parseFile( ramlFilePath );
        new RamlApiPreProcessor( rootDirectory, typesPackage, packageBuilder ).process( parsedRaml );
        new PackageFilesGenerator( packageBuilder, rootDirectory.getPath() ).generateFiles();
    }
}
