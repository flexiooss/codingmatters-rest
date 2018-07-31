package org.codingmatters.rest.php.api.client;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.php.api.client.generator.PhpClassGenerator;
import org.codingmatters.rest.php.api.client.model.ApiGeneratorPhp;
import org.codingmatters.rest.php.api.client.model.HttpMethodDescriptor;
import org.codingmatters.rest.php.api.client.model.ResourceClientDescriptor;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhpClientRequesterGenerator {

    private final String clientPackage;
    private final String apiPackage;
    private final String typesPackage;
    private final File rootDir;
    private final Utils utils;
    private final PhpClassGenerator phpClassGenerator;

    public PhpClientRequesterGenerator( String clientPackage, String apiPackage, String typesPackage, File rootDir ) {
        this.clientPackage = clientPackage;
        this.apiPackage = apiPackage;
        this.typesPackage = typesPackage;
        this.rootDir = rootDir;
        this.utils = new Utils();
        this.phpClassGenerator = new PhpClassGenerator( rootDir.getPath(), apiPackage, typesPackage, clientPackage );
    }

    public void generate( RamlModelResult model ) throws RamlSpecException, IOException {
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

    private void generateRootClient( String apiName, List<ResourceClientDescriptor> clientDescriptors ) throws IOException {
        new File( rootDir.getPath() + "/" + clientPackage.replace( ".", "/" ) ).mkdirs();
        phpClassGenerator.generateRootClientInterface( apiName, clientDescriptors );
        phpClassGenerator.generateRootClientRequesterImpl( apiName, clientDescriptors );
    }

    private void processGeneration( ResourceClientDescriptor clientDescriptor ) throws IOException {
        new File( rootDir.getPath() + "/" + apiPackage.replace( ".", "/" ) ).mkdirs();
        phpClassGenerator.generateInterface( clientDescriptor );
        phpClassGenerator.generateImplementationClass( clientDescriptor );
        for( ResourceClientDescriptor subResourceClientDescriptor : clientDescriptor.nextFloorResourceClientGetters() ) {
            processGeneration( subResourceClientDescriptor );
        }
    }

    private List<ResourceClientDescriptor> processApi( Api api ) throws RamlSpecException {
        List<ResourceClientDescriptor> clientDescriptors = new ArrayList<>();
        for( Resource resource : api.resources() ) {
            clientDescriptors.add( this.processResource( api, resource ) );
        }
        return clientDescriptors;
    }

    private ResourceClientDescriptor processResource( Api api, Resource resource ) throws RamlSpecException {
        String resourceName = utils.getJoinedName( resource.displayName().value() );
        ResourceClientDescriptor resourceDesc = new ResourceClientDescriptor( resourceName, apiPackage );

        for( Method method : resource.methods() ) {
            HttpMethodDescriptor httpMethod = new HttpMethodDescriptor( utils.firstLetterLowerCase( resourceName ) )
                    .withRequestType( resourceName + utils.firstLetterUpperCase( method.method() ) + "Request", apiPackage )
                    .withResponseType( resourceName + utils.firstLetterUpperCase( method.method() ) + "Response", apiPackage )
                    .withPath( resource.resourcePath() )
                    .withMethod( method );
            if( method.body() != null && !method.body().isEmpty() ) {
                TypeDeclaration body = method.body().get( 0 );
                PropertyTypeSpec.Builder type = new ApiGeneratorPhp( typesPackage ).payloadType( body, resourceName );
                httpMethod.withPayload( type.build() );
            }
            resourceDesc.addMethodDescriptor( httpMethod );
        }

        for( Resource subResource : resource.resources() ) {
            ResourceClientDescriptor subResourceDesc = processResource( api, subResource );
            resourceDesc.addNextFloorResource( subResourceDesc );
        }
        return resourceDesc;
    }

}
