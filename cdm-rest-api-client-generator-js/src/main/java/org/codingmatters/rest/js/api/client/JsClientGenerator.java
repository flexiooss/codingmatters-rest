package org.codingmatters.rest.js.api.client;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.api.generator.utils.Resolver;
import org.codingmatters.rest.js.api.client.writers.JsRequesterClientWriter;
import org.codingmatters.rest.js.api.client.writers.JsRootClientWriter;
import org.codingmatters.rest.php.api.client.model.ApiGeneratorPhp;
import org.codingmatters.rest.php.api.client.model.HttpMethodDescriptor;
import org.codingmatters.rest.php.api.client.model.ResourceClientDescriptor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesGenerator;
import org.codingmatters.value.objects.js.generator.visitor.JsClassGeneratorSpecProcessor;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeNested;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsClientGenerator {

    private final ApiTypesJsGenerator apiTypesGenerator;
    private final PackagesConfiguration packagesConfiguration;
    private final File rootDir;
    private final ApiJsGenerator apiGenerator;

    public JsClientGenerator( PackagesConfiguration packagesConfiguration, File rootDir ) {
        this.packagesConfiguration = packagesConfiguration;
        this.rootDir = rootDir;
        this.apiTypesGenerator = new ApiTypesJsGenerator( packagesConfiguration );
        this.apiGenerator = new ApiJsGenerator( packagesConfiguration );
    }

    public void generateTypes( RamlModelResult model ) throws RamlSpecException, ProcessingException, GenerationException {
        List<ParsedValueObject> valueObjects = apiTypesGenerator.parseRamlTypes( model );
        ParsedYAMLSpec yamlSpec = new ParsedYAMLSpec();
        yamlSpec.valueObjects().addAll( valueObjects );
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();
        JsClassGeneratorSpecProcessor processor = new JsClassGeneratorSpecProcessor( rootDir, packagesConfiguration.typesPackage(), packageBuilder );
        processor.process( yamlSpec );

        List<ParsedValueObject> requestsResponseObjects = parseRequests( model );
        yamlSpec = new ParsedYAMLSpec();
        yamlSpec.valueObjects().addAll( requestsResponseObjects );
        processor = new JsClassGeneratorSpecProcessor( rootDir, packagesConfiguration.apiPackage(), packageBuilder );
        processor.process( yamlSpec );

        PackageFilesGenerator packageFilesGenerator = new PackageFilesGenerator( packageBuilder, rootDir.getPath() );
        packageFilesGenerator.generateFiles();
    }

    public void generateApi( RamlModelResult model ) throws Exception {
        Api api = model.getApiV10();
        if( api != null ){
            List<ResourceClientDescriptor> clientDescriptors = processApi( api );
            for( ResourceClientDescriptor clientDescriptor : clientDescriptors ){
                processGeneration( clientDescriptor );
            }
            generateRootClient( model.getApiV10().title().value(), clientDescriptors );
        } else {
            throw new RamlSpecException( "Cannot parse th raml spec v10" );
        }
    }

    private List<ParsedValueObject> parseRequests( RamlModelResult model ) throws RamlSpecException {
        List<ParsedValueObject> valueObjects = new ArrayList<>();
        for( Resource resource : model.getApiV10().resources() ){
            for( Method method : resource.methods() ){
                valueObjects.add( generateRequest( resource, method ) );
                valueObjects.addAll( generateResponse( resource, method ) );
            }
        }
        return valueObjects;
    }

    private ParsedValueObject generateRequest( Resource resource, Method method ) throws RamlSpecException {
        String requestClassName = NamingUtility.className( resource.displayName().value(), method.method(), "Request" );
        ParsedValueObject valueObject = new ParsedValueObject( requestClassName );
        PackagesConfiguration config = new PackagesConfiguration( packagesConfiguration.clientPackage(), packagesConfiguration.apiPackage(), null );
        ApiTypesJsGenerator typeParser = new ApiTypesJsGenerator( config );

        for( TypeDeclaration typeDeclaration : method.queryParameters() ){
            valueObject.properties().add( new ValueObjectProperty( NamingUtility.propertyName( typeDeclaration.name() ), typeParser.parseType( requestClassName, typeDeclaration ) ) );
        }
        for( TypeDeclaration typeDeclaration : method.headers() ){
            valueObject.properties().add( new ValueObjectProperty( NamingUtility.propertyName( typeDeclaration.name() ), typeParser.parseType( requestClassName, typeDeclaration ) ) );
        }
        if( method.body() != null && !method.body().isEmpty() ){
            valueObject.properties().add( new ValueObjectProperty(
                    "payload",
                    typeParser.parseType( requestClassName, method.body().get( 0 ) )
            ) );
        }
        List<TypeDeclaration> typeDeclarations = Resolver.resolvedUriParameters( resource );
        for( TypeDeclaration typeDeclaration : typeDeclarations ){
            valueObject.properties().add( new ValueObjectProperty( NamingUtility.propertyName( typeDeclaration.name() ), typeParser.parseType( requestClassName, typeDeclaration ) ) );
        }
        return valueObject;
    }

    private List<ParsedValueObject> generateResponse( Resource resource, Method method ) throws RamlSpecException {
        PackagesConfiguration config = new PackagesConfiguration( packagesConfiguration.clientPackage(), packagesConfiguration.apiPackage(), null );
        ApiTypesJsGenerator typeGenerator = new ApiTypesJsGenerator( config );
        List<ParsedValueObject> valueObjects = new ArrayList<>();
        String requestClassName = NamingUtility.className( resource.displayName().value(), method.method(), "Response" );
        ParsedValueObject responseValueObject = new ParsedValueObject( requestClassName );

        for( Response response : method.responses() ){
            ParsedValueObject statusValueObject = new ParsedValueObject( "Status" + response.code().value() );
            for( TypeDeclaration typeDeclaration : response.headers() ){
                statusValueObject.properties().add( new ValueObjectProperty(
                        NamingUtility.propertyName( typeDeclaration.name() ),
                        typeGenerator.parseType( statusValueObject.name(), typeDeclaration )
                ) );
            }
            if( response.body() != null && !response.body().isEmpty() ){
                statusValueObject.properties().add( new ValueObjectProperty(
                        "payload",
                        typeGenerator.parseType( statusValueObject.name(), response.body().get( 0 ) )
                ) );
            }
            responseValueObject.properties().add( new ValueObjectProperty( "status" + response.code().value(), new ObjectTypeNested( statusValueObject, requestClassName ) ) );
        }
        valueObjects.add( responseValueObject );
        return valueObjects;
    }

    private void generateRootClient( String apiName, List<ResourceClientDescriptor> clientDescriptors ) throws Exception {
        String filePath = rootDir.getPath() + "/" + packagesConfiguration.clientPackage().replace( ".", "/" ) + "/" + NamingUtility.className( apiName ) + "Client.js";
        System.out.println( "Generating root client at " + filePath );
        try( JsRootClientWriter jsClientWriter = new JsRootClientWriter( filePath, apiName, clientDescriptors ) ) {
            jsClientWriter.generateRootClient();
        }
    }

    private void processGeneration( ResourceClientDescriptor clientDescriptor ) throws Exception {
        String filePath = rootDir.getPath() + "/" + packagesConfiguration.apiPackage().replace( ".", "/" ) + "/" + NamingUtility.className( clientDescriptor.getClassName() ) + ".js";
        System.out.println( "Generating client at " + filePath );
        try( JsRequesterClientWriter jsClientWriter = new JsRequesterClientWriter( filePath, clientDescriptor ) ) {
            jsClientWriter.generateClient();
            for( ResourceClientDescriptor subClient : clientDescriptor.nextFloorResourceClientGetters() ){
                processGeneration( subClient );
            }
        }
    }

    private List<ResourceClientDescriptor> processApi( Api api ) throws RamlSpecException {
        List<ResourceClientDescriptor> clientDescriptors = new ArrayList<>();
        for( Resource resource : api.resources() ){
            clientDescriptors.add( this.processResource( resource ) );
        }
        return clientDescriptors;
    }

    private ResourceClientDescriptor processResource( Resource resource ) throws RamlSpecException {
        String resourceName = NamingUtility.getJoinedName( resource.displayName().value() );
        ResourceClientDescriptor resourceDesc = new ResourceClientDescriptor( resourceName, packagesConfiguration.apiPackage() );

        for( Method method : resource.methods() ){
            HttpMethodDescriptor httpMethod = new HttpMethodDescriptor( NamingUtility.firstLetterLowerCase( resourceName ) )
                    .withRequestType( resourceName + NamingUtility.firstLetterUpperCase( method.method() ) + "Request", packagesConfiguration.apiPackage() )
                    .withResponseType( resourceName + NamingUtility.firstLetterUpperCase( method.method() ) + "Response", packagesConfiguration.apiPackage() )
                    .withPath( resource.resourcePath() )
                    .withMethod( method );
            if( method.body() != null && !method.body().isEmpty() ){
                TypeDeclaration body = method.body().get( 0 );
                PropertyTypeSpec.Builder type = new ApiGeneratorPhp( packagesConfiguration.typesPackage() ).payloadType( body, resourceName );
                httpMethod.withPayload( type.build() );
            }
            resourceDesc.addMethodDescriptor( httpMethod );
        }

        for( Resource subResource : resource.resources() ){
            ResourceClientDescriptor subResourceDesc = processResource( subResource );
            resourceDesc.addNextFloorResource( subResourceDesc );
        }
        return resourceDesc;
    }

}
