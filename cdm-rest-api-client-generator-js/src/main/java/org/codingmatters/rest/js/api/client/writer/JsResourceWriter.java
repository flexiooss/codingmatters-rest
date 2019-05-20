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
        write.line( "constructor( requester, gatewayUrl ) {" );
        write.line( "this._gatewayUrl = gatewayUrl;" );
        write.line( "this._requester = requester;" );
        for( ParsedRoute subRoute : parsedRoute.subRoutes() ){
            String propertyName = NamingUtility.propertyName( subRoute.displayName() );
            String className = NamingUtility.classFullName( clientPackage + "." + subRoute.displayName() );
            write.line( "this._" + propertyName + " = new " + className + "( requester, gatewayUrl );" );
        }
        write.line( "}" );
    }

    public void sendRequest( JsFileWriter write, ParsedRequest parsedRequest, String requestVar, String methodName ) throws IOException {
        String httpMethod = parsedRequest.httpMethod().name().toLowerCase();
        if( parsedRequest.body().isPresent() ){
            if( payloadIsFile( parsedRequest ) ){
                write.line( "var contentType = " + requestVar + ".contentType();" );
            } else {
                write.line( "var contentType = 'application/json';" );
            }
            if( payloadIsFile( parsedRequest ) || payloadIsString( parsedRequest ) ){
                write.line( "var responseDelegate = this._requester." + httpMethod + "( contentType, " + requestVar + ".payload(), (responseDelegate)=>{" );
                write.line( "var clientResponse = this." + methodName + "Parse( responseDelegate );" );
                write.line( "callbackUser( clientResponse );" );
                write.line( " });" );
            } else {
                write.line( "var responseDelegate = this._requester." + httpMethod + "( contentType, JSON.stringify( " + requestVar + ".payload() ), (responseDelegate)=>{" );
                write.line( "var clientResponse = this." + methodName + "Parse( responseDelegate );" );
                write.line( "callbackUser( clientResponse );" );
                write.line( " });" );
            }
        } else {
            write.line( "var responseDelegate = this._requester." + httpMethod + "( (responseDelegate)=>{" );
            write.line( "var clientResponse = this." + methodName + "Parse( responseDelegate );" );
            write.line( "callbackUser( clientResponse );" );
            write.line( " });" );
        }
    }

    private boolean payloadIsString( ParsedRequest parsedRequest ) {
        return (parsedRequest.body().get().type() instanceof ValueObjectTypePrimitiveType)
                && (((ValueObjectTypePrimitiveType) parsedRequest.body().get().type()).type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING);
    }

    private boolean payloadIsFile( ParsedRequest parsedRequest ) {
        return (parsedRequest.body().get().type() instanceof ValueObjectTypePrimitiveType)
                && (((ValueObjectTypePrimitiveType) parsedRequest.body().get().type()).type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.BYTES);
    }

    public void generateGetters( ParsedRoute parsedRoute, JsFileWriter write ) throws IOException {
        for( ParsedRoute subRoute : parsedRoute.subRoutes() ){
            String propertyName = NamingUtility.propertyName( subRoute.displayName() );
            String className = NamingUtility.className( subRoute.displayName() );
            write.newLine();
            write.line( "/**" );
            write.line( "* @returns{" + className + "}" );
            write.line( "*/" );
            write.line( propertyName + "() {" );
            write.line( "return this._" + propertyName + ";" );
            write.line( "}" );
        }
    }

    public void parseResponse( ParsedRequest parsedRequest, JsFileWriter write, String responseVar ) throws ProcessingException {
        try {
            write.line( "var status;" );
            for( ParsedResponse parsedResponse : parsedRequest.responses() ){
                write.line( "if( responseDelegate.code() == " + parsedResponse.code() + " ){" );
                String statusBuilder = NamingUtility.builderFullName(
                        apiPackage + "." + responseVar.toLowerCase() + "." + NamingUtility.statusClassName( parsedResponse.code() )
                );
                write.line( "status = new " + statusBuilder + "();" );
                for( TypedHeader typedHeader : parsedResponse.headers() ){

                    TypedParamUnStringifier processor = new TypedParamUnStringifier( write, apiPackage );
                    write.line( "if( responseDelegate.header( '" + typedHeader.name() + "') != null ) {" );
                    write.indent();
                    write.string( "status." + NamingUtility.propertyName( typedHeader.name() ) + "( " );
                    processor.currentVariable( "responseDelegate.header( '" + typedHeader.name() + "' )" );
                    typedHeader.type().process( processor );
                    write.string( ");" );
                    write.newLine();
                    write.line( "}" );
                }
                if( parsedResponse.body().isPresent() ){
                    TypedParamUnStringifier bodyProcessor = new TypedParamUnStringifier( write, typesPackage );
                    write.indent();
                    write.string( "status.payload( " );
                    bodyProcessor.currentVariable( "responseDelegate.payload()" );
                    TypedBody body = parsedResponse.body().get();
                    body.type().process( bodyProcessor );
                    write.string( ");" );
                    write.newLine();
                    if( body.type() instanceof ValueObjectTypePrimitiveType && ((ValueObjectTypePrimitiveType) body.type()).type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.BYTES ){
                        write.line( "status.contentType( responseDelegate.header( 'Content-Type' ));" );
                    }
                }
                write.line( responseVar + ".status" + parsedResponse.code() + "( status.build() );" );
                write.line( "}" );
            }
            write.line( "return " + responseVar + ".build();" );
        } catch( IOException e ){
            throw new ProcessingException( "Error parsing response" );
        }
    }

    public void setHeaders( JsFileWriter write, ParsedRequest parsedRequest, String requestVar ) throws IOException, ProcessingException {
        for( TypedHeader typedHeader : parsedRequest.headers() ){
            String property = NamingUtility.propertyName( typedHeader.name() );
            String varName = requestVar + "." + property + "()";
            write.line( "if( " + varName + " !== null ){" );
            if( typedHeader.type() instanceof ValueObjectTypeList ){
                write.indent();
                write.string( "this._requester.arrayHeader( '" + typedHeader.name() + "', " );
                typedHeader.type().process( new TypedParamStringifier( write, varName ) );
                write.string( " );" );
                write.newLine();
            } else {
                write.indent();
                write.string( "this._requester.header( '" + typedHeader.name() + "', " );
                typedHeader.type().process( new TypedParamStringifier( write, varName ) );
                write.string( " );" );
                write.newLine();
            }
            write.line( "}" );
        }
    }

    public void setQueryParams( JsFileWriter write, ParsedRequest parsedRequest, String requestVar ) throws IOException, ProcessingException {
        for( TypedQueryParam typedQueryParam : parsedRequest.queryParameters() ){
            String property = NamingUtility.propertyName( typedQueryParam.name() );
            String varName = requestVar + "." + property + "()";
            write.line( "if( " + varName + " !== null ){" );
            if( typedQueryParam.type() instanceof ValueObjectTypeList ){
                write.indent();
                write.string( "this._requester.arrayParameter( '" + typedQueryParam.name() + "', " );
                typedQueryParam.type().process( new TypedParamStringifier( write, varName ) );
                write.string( " );" );
                write.newLine();
            } else {
                write.indent();
                write.string( "this._requester.parameter( '" + typedQueryParam.name() + "', " );
                typedQueryParam.type().process( new TypedParamStringifier( write, varName ) );
                write.string( " );" );
                write.newLine();
            }
            write.line( "}" );
        }
    }
}
