package org.codingmatters.rest.js.api.client.visitors;

import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.rest.parser.model.ParsedRequest;
import org.codingmatters.rest.parser.model.ParsedResponse;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.rest.parser.model.typed.TypedBody;
import org.codingmatters.rest.parser.model.typed.TypedHeader;
import org.codingmatters.rest.parser.model.typed.TypedQueryParam;
import org.codingmatters.rest.parser.model.typed.TypedUriParams;
import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType;

import java.io.File;
import java.io.IOException;

public class RamlApiGenerator implements ParsedRamlProcessor {

    private final PackageFilesBuilder packageBuilder;
    private final File rootDirectory;
    private final String clientPackage;
    private final String apiPackage;
    private JsFileWriter write;
    private String requestVar;
    private String methodName;

    public RamlApiGenerator( File rootDirectory, String clientPackage, String apiPackage, PackageFilesBuilder packageBuilder ) {
        this.rootDirectory = rootDirectory;
        this.clientPackage = clientPackage;
        this.packageBuilder = packageBuilder;
        this.apiPackage = apiPackage;
    }

    @Override
    public void process( ParsedRaml parsedRaml ) throws ProcessingException {
        for( ParsedRoute parsedRoute : parsedRaml.routes() ) {
            this.process( parsedRoute );
        }
    }

    @Override
    public void process( ParsedRoute parsedRoute ) throws ProcessingException {
        try( JsFileWriter write = new JsFileWriter( rootDirectory + "/" + clientPackage.replace( ".", "/" ) + "/" + parsedRoute.displayName() + ".js" ) ) {
            this.write = write;
            generateConstructor( parsedRoute, write );
            for( ParsedRequest parsedRequest : parsedRoute.requests() ) {
                this.methodName = NamingUtility.propertyName( parsedRoute.displayName() + parsedRequest.httpMethod() );
                String requestClass = NamingUtility.requestName( parsedRoute.displayName(), parsedRequest.httpMethod().name() );
                String responseClass = NamingUtility.responseName( parsedRoute.displayName(), parsedRequest.httpMethod().name() );
                this.requestVar = NamingUtility.firstLetterLowerCase( requestClass );
                String responseVar = NamingUtility.firstLetterLowerCase( responseClass );
                write.line( "/**" );
                write.line( " * @param {" + requestClass + "} " + requestVar );
                write.line( " * @returns {" + responseClass + "}" );
                write.line( " */" );
                write.line( methodName + "( " + requestVar + " ){" );
                write.line( "var " + responseVar + " = " + NamingUtility.builderFullName( apiPackage + "." + responseClass ) );
                write.line( "var path = $this._gatewayUrl + '" + parsedRoute.path() + "';" );
                TypedParamUriReplacer uriParamReplacer;
                for( TypedUriParams uriParams : parsedRoute.uriParameters() ) {
                    uriParamReplacer = new TypedParamUriReplacer( uriParams, write, requestVar );
                    uriParams.type().process( uriParamReplacer );
                }
                write.line( "this._requester.path( path );" );
                for( TypedHeader typedHeader : parsedRequest.headers() ) {
                    // TODO
                }
                for( TypedQueryParam typedQueryParam : parsedRequest.queryParameters() ) {
                    // TODO
                }
                parsedRequest.process( this );
                write.line( "var responseDelegate = this._requester." + parsedRequest.httpMethod().name().toLowerCase() + "( contentType, JSON.stringify( typeArrayShortPostRequest.payload() ));" );
                write.line( "}" );
            }
            write.line( "}" );
        } catch( Exception e ) {
            throw new ProcessingException( "Error processing route " + parsedRoute.displayName(), e );
        }

    }

    private void generateConstructor( ParsedRoute parsedRoute, JsFileWriter write ) throws IOException {
        write.line( "/**" );
        write.line( "* @constructor" );
//        write.line( "* @param requester" );
        write.line( "* @param {string} gatewayUrl" );
        write.line( "*/" );
        write.line( "class " + parsedRoute.displayName() + " {" );
        write.line( "constructor( requester, gatewayUrl ) {" );
        write.line( "this._gatewayUrl = gatewayUrl;" );
        write.line( "this._requester = requester;" );
        write.line( "}" );
    }

    @Override
    public void process( ParsedRequest parsedRequest ) throws ProcessingException {
        try {
            if( parsedRequest.body().isPresent() ) {
                if( parsedRequest.body().get().type() instanceof ValueObjectTypePrimitiveType ) {
                    if( ( (ValueObjectTypePrimitiveType) parsedRequest.body().get().type() ).type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ) {
                        write.line( "var contentType = " + requestVar + ".contentType();" );
                    } else {
                        write.line( "var contentType = 'application/json';" );
                    }
                }
                write.line( "var responseDelegate = this._requester." + methodName.toLowerCase() + "( contentType, JSON.stringify( " + requestVar + ".payload() ));" );
            } else {
                write.line( "var responseDelegate = this._requester." + methodName.toLowerCase() + "();" );
            }
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing request", e );
        }
    }

    @Override
    public void process( ParsedResponse parsedResponse ) {

    }

    @Override
    public void process( TypedBody typedBody ) {

    }

    @Override
    public void process( TypedHeader typedHeader ) {

    }

    @Override
    public void process( TypedQueryParam typedQueryParam ) {

    }

    @Override
    public void process( TypedUriParams typedUriParams ) {

    }
}
