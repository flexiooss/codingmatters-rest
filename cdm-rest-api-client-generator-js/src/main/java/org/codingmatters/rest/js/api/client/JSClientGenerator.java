package org.codingmatters.rest.js.api.client;

import org.codingmatters.rest.js.api.client.visitors.JsApiResourcesGenerator;
import org.codingmatters.rest.js.api.client.visitors.JsApiRootClientGenerator;
import org.codingmatters.rest.js.api.client.visitors.RamlApiPreProcessor;
import org.codingmatters.rest.parser.RamlParser;
import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.js.generator.packages.PackageConfiguration;
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
    private final String vendor;
    private final String version;
    private final String artifactId;

    public JSClientGenerator( File rootDirectory, String clientPackage, String typesPackage, String apiPackage, String vendor, String artifactId, String version ) {
        this.rootDirectory = rootDirectory;
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
        this.clientPackage = clientPackage;
        this.resourcesPackage = clientPackage + ".resources";
        this.typesRamlParser = new RamlParser( typesPackage, apiPackage );
        this.vendor = vendor;
        this.version = version;
        this.artifactId = artifactId;
    }

    public void generateClientApi( String... ramlFilePath ) throws GenerationException, ProcessingException {
        this.generateClientApi( true, ramlFilePath );
    }

    public void generateClientApi( boolean generatePackage, String... ramlFilePath ) throws ProcessingException, GenerationException {
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();

        for( String ramlFile : ramlFilePath ){
            ParsedRaml parsedRaml = typesRamlParser.parseFile( ramlFile );
            generateClient( packageBuilder, parsedRaml, generatePackage );
        }
        new PackageFilesGenerator( packageBuilder, rootDirectory.getPath() ).generateFiles();
    }

    public void generateClientApi( RamlModelResult ramlModel ) throws ProcessingException, GenerationException {
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();
        ParsedRaml parsedRaml = typesRamlParser.parseRamlModel( ramlModel );
        generateClient( packageBuilder, parsedRaml, true );
        new PackageFilesGenerator( packageBuilder, rootDirectory.getPath() ).generateFiles();
    }

    public void generateClientApi( RamlModelResult[] ramlModels ) throws ProcessingException, GenerationException {
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();
        for( RamlModelResult ramlModel : ramlModels ){
            ParsedRaml parsedRaml = typesRamlParser.parseRamlModel( ramlModel );
            generateClient( packageBuilder, parsedRaml, false );
        }
        new PackageFilesGenerator( packageBuilder, rootDirectory.getPath() ).generateFiles();
        new JsonPackageGenerator( rootDirectory ).generatePackageJson( vendor, artifactId, version, typesPackage.split( "\\." )[0] );
    }


    private void generateClient( PackageFilesBuilder packageBuilder, ParsedRaml parsedRaml, boolean generatePackage ) throws ProcessingException {
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

        if( generatePackage ){
            new JsonPackageGenerator( rootDirectory ).generatePackageJson( vendor, artifactId, version, typesPackage.split( "\\." )[0] );
        }
    }


}
