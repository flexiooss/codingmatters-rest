package org.codingmatters.rest.js.api.client;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.js.api.client.writers.JsRequesterClientWriter;
import org.codingmatters.rest.js.api.client.writers.JsRootClientWriter;
import org.codingmatters.rest.php.api.client.model.ApiGeneratorPhp;
import org.codingmatters.rest.php.api.client.model.HttpMethodDescriptor;
import org.codingmatters.rest.php.api.client.model.ResourceClientDescriptor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.visitor.JsClassGeneratorSpecProcessor;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
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

    public JsClientGenerator( PackagesConfiguration packagesConfiguration, File rootDir ) {
        this.packagesConfiguration = packagesConfiguration;
        this.rootDir = rootDir;
        this.apiTypesGenerator = new ApiTypesJsGenerator( packagesConfiguration );
    }

    public void generateTypes( RamlModelResult model ) throws RamlSpecException, ProcessingException {
        List<ParsedValueObject> valueObjects = apiTypesGenerator.parseValueObjects( model );
        ParsedYAMLSpec yamlSpec = new ParsedYAMLSpec();
        yamlSpec.valueObjects().addAll( valueObjects );
        JsClassGeneratorSpecProcessor processor = new JsClassGeneratorSpecProcessor( rootDir, packagesConfiguration.typesPackage() );
        processor.process( yamlSpec );
    }

    public void generateApi( RamlModelResult model ) throws Exception {
        Api api = model.getApiV10();
        if( api != null ) {
            List<ResourceClientDescriptor> clientDescriptors = processApi( api );
            for( ResourceClientDescriptor clientDescriptor : clientDescriptors ) {
                processGeneration( clientDescriptor );
            }
            generateRootClient( model.getApiV10().title().value(), clientDescriptors );
        } else {
            throw new RamlSpecException( "Cannot parse th raml spec v10" );
        }
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
            for( ResourceClientDescriptor subClient : clientDescriptor.nextFloorResourceClientGetters() ) {
                processGeneration( subClient );
            }
        }
    }

    private List<ResourceClientDescriptor> processApi( Api api ) throws RamlSpecException {
        List<ResourceClientDescriptor> clientDescriptors = new ArrayList<>();
        for( Resource resource : api.resources() ) {
            clientDescriptors.add( this.processResource( resource ) );
        }
        return clientDescriptors;
    }

    private ResourceClientDescriptor processResource( Resource resource ) throws RamlSpecException {
        String resourceName = NamingUtility.getJoinedName( resource.displayName().value() );
        ResourceClientDescriptor resourceDesc = new ResourceClientDescriptor( resourceName, packagesConfiguration.apiPackage() );

        for( Method method : resource.methods() ) {
            HttpMethodDescriptor httpMethod = new HttpMethodDescriptor( NamingUtility.firstLetterLowerCase( resourceName ) )
                    .withRequestType( resourceName + NamingUtility.firstLetterUpperCase( method.method() ) + "Request", packagesConfiguration.apiPackage() )
                    .withResponseType( resourceName + NamingUtility.firstLetterUpperCase( method.method() ) + "Response", packagesConfiguration.apiPackage() )
                    .withPath( resource.resourcePath() )
                    .withMethod( method );
            if( method.body() != null && !method.body().isEmpty() ) {
                TypeDeclaration body = method.body().get( 0 );
                PropertyTypeSpec.Builder type = new ApiGeneratorPhp( packagesConfiguration.typesPackage() ).payloadType( body, resourceName );
                httpMethod.withPayload( type.build() );
            }
            resourceDesc.addMethodDescriptor( httpMethod );
        }

        for( Resource subResource : resource.resources() ) {
            ResourceClientDescriptor subResourceDesc = processResource( subResource );
            resourceDesc.addNextFloorResource( subResourceDesc );
        }
        return resourceDesc;
    }

}
