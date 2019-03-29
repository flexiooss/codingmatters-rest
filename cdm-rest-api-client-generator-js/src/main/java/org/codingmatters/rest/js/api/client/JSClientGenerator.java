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
import org.raml.v2.api.RamlModelResult;

import java.io.File;

public class JSClientGenerator {

    private final File rootDirectory;
    private final String typesPackage;
    private final String apiPackage;
    private final String resourcesPackage;
    private final String clientPackage;
    private final RamlParser typesRamlParser;

    public JSClientGenerator( File rootDirectory, String rootPackage ) {
        this.rootDirectory = rootDirectory;
        this.typesPackage = rootPackage + ".types";
        this.apiPackage = rootPackage + ".api";
        this.resourcesPackage = rootPackage + ".client.resources";
        this.clientPackage = rootPackage + ".client";
        this.typesRamlParser = new RamlParser( typesPackage, apiPackage );
    }

    public void generateClientApi( String... ramlFilePath ) throws ProcessingException, GenerationException {
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();

        for( String ramlFile : ramlFilePath ){
            ParsedRaml parsedRaml = typesRamlParser.parseFile( ramlFile );
            generateClient( packageBuilder, parsedRaml );
        }
        new PackageFilesGenerator( packageBuilder, rootDirectory.getPath() ).generateFiles();
    }

    public void generateClientApi( RamlModelResult ramlModel ) throws ProcessingException, GenerationException {
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();
        ParsedRaml parsedRaml = typesRamlParser.parseRamlModel( ramlModel );
        generateClient( packageBuilder, parsedRaml );
        new PackageFilesGenerator( packageBuilder, rootDirectory.getPath() ).generateFiles();
    }


    private void generateClient( PackageFilesBuilder packageBuilder, ParsedRaml parsedRaml ) throws ProcessingException {
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
    }
}
