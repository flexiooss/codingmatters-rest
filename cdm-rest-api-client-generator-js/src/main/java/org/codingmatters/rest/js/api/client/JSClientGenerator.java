package org.codingmatters.rest.js.api.client;

import org.codingmatters.rest.js.api.client.visitors.JsApiResourcesGenerator;
import org.codingmatters.rest.js.api.client.visitors.JsApiRootClientGenerator;
import org.codingmatters.rest.js.api.client.visitors.RamlApiPreProcessor;
import org.codingmatters.rest.parser.RamlParser;
import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesGenerator;
import org.codingmatters.value.objects.js.generator.visitor.JsValueObjectGenerator;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;

import java.io.File;

public class JSClientGenerator {

    private final File rootDirectory;
    private final String typesPackage;
    private final String apiPackage;
    private final String resourcesPackage;
    private final String clientPackage;

    public JSClientGenerator( File rootDirectory, String rootPackage ) {
        this.rootDirectory = rootDirectory;
        this.typesPackage = rootPackage + ".types";
        this.apiPackage = rootPackage + ".api";
        this.resourcesPackage = rootPackage + ".client.resources";
        this.clientPackage = rootPackage + ".client";
    }

    public void generateClientApi( String ramlFilePath ) throws ProcessingException, GenerationException {
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();
        RamlParser ramlParser = new RamlParser();
        ParsedRaml parsedRaml = ramlParser.parseFile( ramlFilePath );
        RamlApiPreProcessor ramlApiPreProcessor = new RamlApiPreProcessor( apiPackage );
        ramlApiPreProcessor.process( parsedRaml );

        new JsValueObjectGenerator( rootDirectory, typesPackage, packageBuilder )
                .process( new ParsedYAMLSpec( parsedRaml.types() ) );

        for( String subPackage : ramlApiPreProcessor.processedValueObjects().keySet() ){
            new JsValueObjectGenerator( rootDirectory, subPackage, apiPackage, packageBuilder )
                    .process( new ParsedYAMLSpec( ramlApiPreProcessor.processedValueObjects().get( subPackage ) ) );
        }

        new JsApiResourcesGenerator( rootDirectory, resourcesPackage, apiPackage, typesPackage, packageBuilder )
                .process( parsedRaml );
        new JsApiRootClientGenerator( rootDirectory, clientPackage, resourcesPackage, packageBuilder )
                .process( parsedRaml );

        new PackageFilesGenerator( packageBuilder, rootDirectory.getPath() ).generateFiles();
    }
}
