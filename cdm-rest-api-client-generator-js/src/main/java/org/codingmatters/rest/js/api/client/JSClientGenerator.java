package org.codingmatters.rest.js.api.client;

import org.codingmatters.rest.js.api.client.visitors.RamlApiGenerator;
import org.codingmatters.rest.js.api.client.visitors.RamlApiPreProcessor;
import org.codingmatters.rest.parser.RamlParser;
import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesGenerator;
import org.codingmatters.value.objects.js.generator.visitor.JsClassGeneratorSpecProcessor;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;

import java.io.File;

public class JSClientGenerator {

    private final File rootDirectory;
    private final String typesPackage;
    private final String apiPackage;
    private final String clientPackage;

    public JSClientGenerator( File rootDirectory, String rootPackage ) {
        this.rootDirectory = rootDirectory;
        this.typesPackage = rootPackage + ".types";
        this.apiPackage = rootPackage + ".api";
        this.clientPackage = rootPackage + ".client";
    }

    public void generateClientApi( String ramlFilePath ) throws ProcessingException, GenerationException {
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();
        RamlParser ramlParser = new RamlParser();
        ParsedRaml parsedRaml = ramlParser.parseFile( ramlFilePath );
        RamlApiPreProcessor ramlApiPreProcessor = new RamlApiPreProcessor( rootDirectory, typesPackage, packageBuilder );
        ramlApiPreProcessor.process( parsedRaml );

        ParsedYAMLSpec spec = new ParsedYAMLSpec();
        spec.valueObjects().addAll( parsedRaml.types() );
        new JsClassGeneratorSpecProcessor( rootDirectory, typesPackage, packageBuilder ).process( spec );
        spec = new ParsedYAMLSpec();
        spec.valueObjects().addAll( ramlApiPreProcessor.processedValueObjects() );
        new JsClassGeneratorSpecProcessor( rootDirectory, apiPackage, packageBuilder ).process( spec );
        new RamlApiGenerator( rootDirectory, clientPackage, apiPackage, packageBuilder ).process( parsedRaml );

        new PackageFilesGenerator( packageBuilder, rootDirectory.getPath() ).generateFiles();

    }
}
