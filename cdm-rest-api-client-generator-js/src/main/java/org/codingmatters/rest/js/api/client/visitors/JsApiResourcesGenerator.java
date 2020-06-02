package org.codingmatters.rest.js.api.client.visitors;

import org.codingmatters.rest.js.api.client.writer.JsResourceWriter;
import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.rest.parser.model.ParsedRequest;
import org.codingmatters.rest.parser.model.ParsedResponse;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.rest.parser.model.typed.*;
import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsApiResourcesGenerator implements ParsedRamlProcessor {

    private final PackageFilesBuilder packageBuilder;
    private final File rootDirectory;
    private final String clientPackage;
    private final String apiPackage;
    private final String typesPackage;
    private JsResourceWriter jsResourceWriter;
    private JsFileWriter write;
    private String requestVar;
    private String methodName;
    private String responseVar;
    private Set<String> importsTypes;

    public JsApiResourcesGenerator( File rootDirectory, String clientPackage, String apiPackage, String typesPackage, PackageFilesBuilder packageBuilder ) {
        this.rootDirectory = rootDirectory;
        this.clientPackage = clientPackage;
        this.apiPackage = apiPackage;
        this.typesPackage = typesPackage;
        this.packageBuilder = packageBuilder;
        this.jsResourceWriter = new JsResourceWriter( clientPackage, apiPackage, typesPackage );
    }

    @Override
    public void process( ParsedRaml parsedRaml ) throws ProcessingException {
        for( ParsedRoute parsedRoute : parsedRaml.routes() ){
            this.process( parsedRoute );
        }
    }

    @Override
    public void process( ParsedRoute parsedRoute ) throws ProcessingException {
        try( JsFileWriter write = new JsFileWriter( rootDirectory + "/" + clientPackage.replace( ".", "/" ) + "/" + parsedRoute.displayName() + ".js" ) ) {
            packageBuilder.addEnum( clientPackage, parsedRoute.displayName(), false );
            this.write = write;
            this.importsTypes = new HashSet<>();
            collectAllImports( parsedRoute );
            write.line( "import { globalFlexioImport } from '@flexio-oss/js-commons-bundle/global-import-registry'" );
            write.line( "import {isPayloadNull} from '@flexio-oss/js-commons-bundle/js-generator-helpers'" );
            if(! importsTypes.isEmpty()){
                write.line( "import { " + String.join( ", ", importsTypes ) + " } from '@flexio-oss/js-commons-bundle/flex-types'" );
            }
            write.line( "class " + parsedRoute.displayName() + " {" );
            jsResourceWriter.generateConstructor( parsedRoute, write );
            jsResourceWriter.generateGetters( parsedRoute, write );
            for( ParsedRequest parsedRequest : parsedRoute.requests() ){
                this.methodName = NamingUtility.propertyName( parsedRoute.displayName() + NamingUtility.firstLetterUpperCase( parsedRequest.httpMethod().name().toLowerCase() ) );
                String requestClass = NamingUtility.requestName( parsedRoute.displayName(), parsedRequest.httpMethod().name() );
                String responseClass = NamingUtility.responseName( parsedRoute.displayName(), parsedRequest.httpMethod().name() );
                this.requestVar = NamingUtility.firstLetterLowerCase( requestClass );
                this.responseVar = NamingUtility.firstLetterLowerCase( responseClass );
                write.line( "/**" );
                write.line( " * @param {" + requestClass + "} " + requestVar );
                write.line( " * @param { function("  + responseVar + ":" + responseClass  + ") } callbackUser" );
                write.line( " */" );
                write.line( methodName + "(" + requestVar + ", callbackUser) {" );
                write.line("let response = this." + methodName + "Execute( " + requestVar + ", callbackUser)");
                write.line("}");

                write.line( "/**" );
                write.line( " * @param {" + requestClass + "} " + requestVar );
                write.line( " * @param {function(" + responseVar + ":" + responseClass + ")} callbackUser" );
                write.line( " */" );
                write.line( methodName + "Execute(" + requestVar + ", callbackUser) {" );
                write.line( "let path = this._gatewayUrl + '" + parsedRoute.path() + "'" );
                for( TypedUriParams uriParam : parsedRoute.uriParameters() ){
                    uriParam.type().process( new TypedParamUriReplacer( uriParam, write, requestVar ) );
                }
                write.line( "this._requester.path(path)" );
                jsResourceWriter.setHeaders( write, parsedRequest, requestVar );
                jsResourceWriter.setQueryParams( write, parsedRequest, requestVar );
                jsResourceWriter.sendRequest( write, parsedRequest, requestVar, methodName );
                write.line("}");

                write.line( "/**" );
                write.line( " * @param {ResponseDelegate} responseDelegate"  );
                write.line( " * @param {function(" + responseVar + ":" + responseClass + ")} callbackUser" );
                write.line( " */" );
                write.line( methodName + "Parse(responseDelegate, callbackUser) {" );
                write.line( "let " + responseVar + " = new " + NamingUtility.builderFullName( apiPackage + "." + responseClass ) + "()" );
                jsResourceWriter.parseResponse( parsedRequest, write, responseVar );
                write.line( "}" );
            }
            write.line( "}" );
            write.line( "export { " + parsedRoute.displayName() + " }" );

        } catch( Exception e ){
            throw new ProcessingException( "Error processing route " + parsedRoute.displayName(), e );
        }
        for( ParsedRoute subRoute : parsedRoute.subRoutes() ){
            this.process( subRoute );
        }
    }

    private void collectAllImports( ParsedRoute parsedRoute ) {
        collectImports( parsedRoute.uriParameters().stream().map( TypedParameter::type ).collect( Collectors.toList() ) );
        for( ParsedRequest parsedRequest : parsedRoute.requests() ){
            collectImports( parsedRequest.headers().stream().map( TypedParameter::type ).collect( Collectors.toList() ) );
            collectImports( parsedRequest.queryParameters().stream().map( TypedParameter::type ).collect( Collectors.toList() ) );
            for( ParsedResponse parsedResponse : parsedRequest.responses() ){
                collectImports( parsedResponse.headers().stream().map( TypedParameter::type ).collect( Collectors.toList() ) );
            }
        }
    }

    private void collectImports( List<ValueObjectType> types ) {
        for( ValueObjectType type : types ){
            if( isDate( type ) ){
                importsTypes.add( "FlexDate" );
            } else if( isDateTime( type ) ){
                importsTypes.add( "FlexDateTime" );
            } else if( isTime( type ) ){
                importsTypes.add( "FlexTime" );
            } else if( isTzDateTime( type ) ){
                importsTypes.add( "FlexZonedDateTime" );
            }
        }
    }

    private boolean isDate( ValueObjectType type ) {
        return type instanceof ValueObjectTypePrimitiveType
                && ((ValueObjectTypePrimitiveType) type).type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE;
    }

    private boolean isDateTime( ValueObjectType type ) {
        return type instanceof ValueObjectTypePrimitiveType
                && ((ValueObjectTypePrimitiveType) type).type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE_TIME;
    }

    private boolean isTime( ValueObjectType type ) {
        return type instanceof ValueObjectTypePrimitiveType
                && ((ValueObjectTypePrimitiveType) type).type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TIME;
    }

    private boolean isTzDateTime( ValueObjectType type ) {
        return type instanceof ValueObjectTypePrimitiveType
                && ((ValueObjectTypePrimitiveType) type).type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TZ_DATE_TIME;
    }
}
