package org.codingmatters.rest.js.api.client.writer;

import org.codingmatters.rest.js.api.client.visitors.TypedParamStringifier;
import org.codingmatters.rest.js.api.client.visitors.TypedParamUnStringifier;
import org.codingmatters.rest.parser.model.ParsedRequest;
import org.codingmatters.rest.parser.model.ParsedResponse;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.rest.parser.model.typed.TypedBody;
import org.codingmatters.rest.parser.model.typed.TypedHeader;
import org.codingmatters.rest.parser.model.typed.TypedQueryParam;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypeList;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType;

import java.io.IOException;

public class JsResourceWriter {

    private final String clientPackage;
    private final String apiPackage;
    private final String typesPackage;

    public JsResourceWriter( String clientPackage, String apiPackage, String typesPackage ) {
        this.clientPackage = clientPackage;
        this.apiPackage = apiPackage;
        this.typesPackage = typesPackage;
    }

    public void generateConstructor( ParsedRoute parsedRoute, JsFileWriter write ) throws IOException {
        write.line( "/**" );
        write.line( "* @constructor" );
        write.line( "* @param requester" );
        write.line( "* @param {string} gatewayUrl" );
        write.line( "*/" );
        write.line( "constructor(requester, gatewayUrl) {" );
        write.line( "this._gatewayUrl = gatewayUrl" );
        write.line( "this._requester = requester" );
        for( ParsedRoute subRoute : parsedRoute.subRoutes() ){
            String propertyName = NamingUtility.propertyName( subRoute.displayName() );
            String className = NamingUtility.classFullName( clientPackage + "." + subRoute.displayName() );
            write.line( "this._" + propertyName + " = new " + className + "(requester, gatewayUrl)" );
        }
        write.line( "}" );
    }

    public void sendRequest( JsFileWriter write, ParsedRequest parsedRequest, String requestVar, String methodName ) throws IOException {
        String httpMethod = parsedRequest.httpMethod().name().toLowerCase();
        if( parsedRequest.body().isPresent() ){
            boolean payloadIsBinary = payloadIsFile( parsedRequest.body().get().type() ) || payloadIsString( parsedRequest );
            if( payloadIsBinary ){
                write.line( "let contentType = " + requestVar + ".contentType()" );
            } else {
                write.line( "let contentType = 'application/json'" );
            }
            if( payloadIsBinary ){
                write.line( "let responseDelegate = this._requester." + httpMethod + "((responseDelegate) => {" );
                write.line( "let clientResponse = this." + methodName + "Parse(responseDelegate, callbackUser)" );
                write.unindent();
                write.line( "}, contentType, " + requestVar + ".payload())" );
            } else {
                write.line( "let responseDelegate = this._requester." + httpMethod + "((responseDelegate) => {" );
                write.line( "let clientResponse = this." + methodName + "Parse(responseDelegate, callbackUser)" );
                write.unindent();
                write.line( "}, contentType, new Blob([JSON.stringify(" + requestVar + ".payload())], {type: contentType}))" );
            }
        } else {
            write.line( "let responseDelegate = this._requester." + httpMethod + "((responseDelegate) => {" );
            write.line( "let clientResponse = this." + methodName + "Parse(responseDelegate, callbackUser)" );
            write.line( "}" );
            write.line( ")" );
        }
    }

    private boolean payloadIsString( ParsedRequest parsedRequest ) {
        return (parsedRequest.body().get().type() instanceof ValueObjectTypePrimitiveType)
                && (((ValueObjectTypePrimitiveType) parsedRequest.body().get().type()).type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING);
    }

    private boolean payloadIsFile( ValueObjectType type ) {
        return (type instanceof ValueObjectTypePrimitiveType)
                && (((ValueObjectTypePrimitiveType) type).type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.BYTES);
    }

    public void generateGetters( ParsedRoute parsedRoute, JsFileWriter write ) throws IOException {
        for( ParsedRoute subRoute : parsedRoute.subRoutes() ){
            String propertyName = NamingUtility.propertyName( subRoute.displayName() );
            String className = NamingUtility.className( subRoute.displayName() );
            write.newLine();
            write.line( "/**" );
            write.line( "* @returns {" + className + "}" );
            write.line( "*/" );
            write.line( propertyName + "() {" );
            write.line( "return this._" + propertyName);
            write.line( "}" );
        }
    }

    public void parseResponse( ParsedRequest parsedRequest, JsFileWriter write, String responseVar ) throws ProcessingException {
        try {
            write.line( "let status" );
            for( ParsedResponse parsedResponse : parsedRequest.responses() ){
                write.line( "if (responseDelegate.code() === " + parsedResponse.code() + ") {" );
                String statusBuilder = NamingUtility.builderFullName(
                        apiPackage + "." + responseVar.toLowerCase() + "." + NamingUtility.statusClassName( parsedResponse.code() )
                );
                write.line( "status = new " + statusBuilder + "()" );
                for( TypedHeader typedHeader : parsedResponse.headers() ){

                    TypedParamUnStringifier processor = new TypedParamUnStringifier( write, apiPackage );
                    write.line( "if (responseDelegate.header('" + typedHeader.name() + "') !== null) {" );
                    write.indent();
                    write.string( "status." + NamingUtility.propertyName( typedHeader.name() ) + "(" );
                    processor.currentVariable( "responseDelegate.header('" + typedHeader.name() + "')" );
                    typedHeader.type().process( processor );
                    write.string( ")" );
                    write.newLine();
                    write.line( "}" );
                }
                if( parsedResponse.body().isPresent() ){
                    TypedParamUnStringifier bodyProcessor = new TypedParamUnStringifier( write, typesPackage );
                    TypedBody body = parsedResponse.body().get();
                    if( payloadIsFile( body.type() )){
                        write.writeLine( "status.payload( responseDelegate.payload() )" );
                        write.line( responseVar + ".status" + parsedResponse.code() + "(status.build())" );
                        write.line( "callbackUser( " + responseVar + ".build() )" );
                    } else {
                        write.writeLine( "let blobReader = new FileReader()" );
                        write.line( "blobReader.onloadend = () => {" );
                        write.writeLine( "let payload = blobReader.result" );
                        write.line( "if( !isPayloadNull( payload )){" );
                        bodyProcessor.currentVariable( "payload" );
                        write.indent();
                        write.string( "status.payload(" );
                        body.type().process( bodyProcessor );
                        write.string( ")" );
                        write.newLine();
                        write.unindent();
                        write.line( "} else {" );
                        write.line( "status.payload(null)" );
                        write.line( "}" );
                        write.line( responseVar + ".status" + parsedResponse.code() + "(status.build())" );
                        write.line( "callbackUser( " + responseVar + ".build() )" );
                        write.line( "}" );
                        write.writeLine( "blobReader.readAsText( responseDelegate.payload() )" );
                    }
                } else {
                    write.line( responseVar + ".status" + parsedResponse.code() + "(status.build())" );
                    write.line( "callbackUser( " + responseVar + ".build() )" );
                }
                write.line( "}" );
            }
        } catch( IOException e ){
            throw new ProcessingException( "Error parsing response" );
        }
    }

    public void setHeaders( JsFileWriter write, ParsedRequest parsedRequest, String requestVar ) throws IOException, ProcessingException {
        for( TypedHeader typedHeader : parsedRequest.headers() ){
            String property = NamingUtility.propertyName( typedHeader.name() );
            String varName = requestVar + "." + property + "()";
            write.line( "if (" + varName + " !== null) {" );
            if( typedHeader.type() instanceof ValueObjectTypeList ){
                write.indent();
                write.string( "this._requester.arrayHeader('" + typedHeader.name() + "', " );
                typedHeader.type().process( new TypedParamStringifier( write, varName ) );
                write.string( ")" );
                write.newLine();
            } else {
                write.indent();
                write.string( "this._requester.header('" + typedHeader.name() + "', " );
                typedHeader.type().process( new TypedParamStringifier( write, varName ) );
                write.string( ")" );
                write.newLine();
            }
            write.line( "}" );
        }
    }

    public void setQueryParams( JsFileWriter write, ParsedRequest parsedRequest, String requestVar ) throws IOException, ProcessingException {
        for( TypedQueryParam typedQueryParam : parsedRequest.queryParameters() ){
            String property = NamingUtility.propertyName( typedQueryParam.name() );
            String varName = requestVar + "." + property + "()";
            write.line( "if (" + varName + " !== null) {" );
            if( typedQueryParam.type() instanceof ValueObjectTypeList ){
                write.indent();
                write.string( "this._requester.arrayParameter('" + typedQueryParam.name() + "', " );
                typedQueryParam.type().process( new TypedParamStringifier( write, varName ) );
                write.string( ")" );
                write.newLine();
            } else {
                write.indent();
                write.string( "this._requester.parameter('" + typedQueryParam.name() + "', " );
                typedQueryParam.type().process( new TypedParamStringifier( write, varName ) );
                write.string( ")" );
                write.newLine();
            }
            write.line( "}" );
        }
    }
}
