package org.codingmatters.rest.js.api.client.writers;

import org.codingmatters.rest.php.api.client.model.HttpMethodDescriptor;
import org.codingmatters.rest.php.api.client.model.ResourceClientDescriptor;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsRequesterClientWriter extends JsFileWriter {

    private final ResourceClientDescriptor clientDescriptor;

    public JsRequesterClientWriter( String filePath, ResourceClientDescriptor clientDescriptor ) {
        super( filePath );
        this.clientDescriptor = clientDescriptor;
    }

    public void generateClient() throws IOException {
        line( "class " + clientDescriptor.getClassName() + " {" );
        generateConstructor();
        generateClientDescriptorGetters();
        generateClientDescriptorRequests();
        line( "}" );
        flush();
    }

    private void generateClientDescriptorRequests() throws IOException {
        for( HttpMethodDescriptor method : clientDescriptor.methodDescriptors() ) {
            line( "/**" );
            line( "* @param {" + method.getRequestType() + "} " + NamingUtility.propertyName( method.getRequestType() ) );
            line( "* @returns {" + method.getResponseType() + "}" );
            line( "*/" );
            String httpMethodName = NamingUtility.firstLetterLowerCase( clientDescriptor.getClassName() ) + NamingUtility.firstLetterUpperCase( method.method().method() );
            String requestVarName = NamingUtility.firstLetterLowerCase( method.getRequestType() );
            line( httpMethodName + "( " + requestVarName + " ){" );
            String responseVar = NamingUtility.firstLetterLowerCase( method.getResponseType() );
            line( "var " + responseVar + " = new " + NamingUtility.builderName( method.getResponseType() ) + "();" );
            line( "var path = $this._gatewayUrl + '" + method.path() + "';" );
            replaceUriParameters( method, requestVarName );
            setQueryParameters( method.method().queryParameters(), requestVarName );
            setHeaders( method.method().headers(), requestVarName );
            String httpMethod = method.method().method().toLowerCase();
            if( method.payload() != null ) {
                if( method.payload().typeRef().equals( "string" ) ) {
                    line( "var contentType = " + requestVarName + ".contentType();" );
                } else {
                    line( "var contentType = 'application/json';" );
                }
                line( "var responseDelegate = this._requester." + httpMethod + "( contentType, JSON.stringify( " + requestVarName + ".payload() ));" );
            } else {
                line( "var responseDelegate = this._requester." + httpMethod + "();" );
            }
            line( "var status;" );
            parseResponse( method );
            line( "}" );
        }
    }

    private void parseHeaders( Response response ) throws IOException {
        for( TypeDeclaration typeDeclaration : response.headers() ) {
            String property = NamingUtility.propertyName( typeDeclaration.name() );
            if( typeDeclaration instanceof ArrayTypeDeclaration ) {
                String type = ((ArrayTypeDeclaration) typeDeclaration).items().type();
                if( type.equals( "boolean" ) ) {
                    line( "status." + property + "( responseDelegate.header( '" + typeDeclaration.name() + "' ).map( item=>item == 'true' ) );" );
                } else if( type.equals( "date" ) ) {
                    line( "status." + property + "( responseDelegate.header( '" + typeDeclaration.name() + "' ).map( item=>new FlexDate( item )));" );
                } else if( type.equals( "datetime" ) ) {
                    line( "status." + property + "( responseDelegate.header( '" + typeDeclaration.name() + "' ).map( item=>new FlexDateTime( item )));" );
                } else if( type.equals( "time" ) ) {
                    line( "status." + property + "( responseDelegate.header( '" + typeDeclaration.name() + "' ).map( item=>new FlexTime( item )));" );
                } else if( type.equals( "tzdatetime" ) ) { // TODO Check this
                    line( "status." + property + "( responseDelegate.header( '" + typeDeclaration.name() + "' ).map( item=>new FlexZonedDateTime( item )));" );
                } else if( type.equals( "int" ) ) {
                    line( "status." + property + "( responseDelegate.header( '" + typeDeclaration.name() + "' ).map( item=>parseInt( item )));" );
                } else if( type.equals( "float" ) ) {
                    line( "status." + property + "( responseDelegate.header( '" + typeDeclaration.name() + "' ).map( item=>parseFloat( item )));" );
                }
            } else {
                String type = typeDeclaration.type();
                if( type.equals( "boolean" ) ) {
                    line( "status." + property + "( responseDelegate.header( '" + typeDeclaration.name() + "' ) === 'true' );" );
                } else if( type.equals( "date" ) ) {
                    line( "status." + property + "( new FlexDate( responseDelegate.header( '" + typeDeclaration.name() + "' )));" );
                } else if( type.equals( "datetime" ) ) {
                    line( "status." + property + "( new FlexDateTime( responseDelegate.header( '" + typeDeclaration.name() + "' )));" );
                } else if( type.equals( "time" ) ) {
                    line( "status." + property + "( new FlexTime( responseDelegate.header( '" + typeDeclaration.name() + "' )));" );
                } else if( type.equals( "tzdatetime" ) ) { // TODO Check this
                    line( "status." + property + "( new FlexZonedDateTime( responseDelegate.header( '" + typeDeclaration.name() + "' )));" );
                } else if( type.equals( "int" ) ) {
                    line( "status." + property + "( parseInt( responseDelegate.header( '" + typeDeclaration.name() + "' )));" );
                } else if( type.equals( "float" ) ) {
                    line( "status." + property + "( parseFloat( responseDelegate.header( '" + typeDeclaration.name() + "' )));" );
                }
            }
        }
    }

    private void parseResponse( HttpMethodDescriptor method ) throws IOException {
        line( "var response = new " + NamingUtility.builderName( method.getResponseType() ) + "()" );
        for( Response response : method.method().responses() ) {
            String httpCode = response.code().value();
            line( "if( responseDelegate.code() == " + httpCode + " ){" );
            line( "status = new " + NamingUtility.builderName( "Status" + httpCode ) + "();" );
            // parse body
            if( response.body() != null && !response.body().isEmpty() ) {
                if( response.body().get( 0 ).type().equals( "file" ) ) {
                    line( "status.payload( responseDelegate.payload() );" );
                    line( "status.contentType( responseDelegate.header( 'Content-type' ));" );
                } else {
                    line( "status.payload( " + NamingUtility.builderName( response.body().get( 0 ).type() ) + ".fromJson( responseDelegate.payload ));" );
                }
            }
            parseHeaders( response );
            line( "response.status" + httpCode + "( status.build() );" );
            line( "}" );
            line( "return response.build();" );
        }
    }

    private void setHeaders( List<TypeDeclaration> typeDeclarations, String requestVarName ) throws IOException {
        for( TypeDeclaration typeDeclaration : typeDeclarations ) {
            String property = NamingUtility.propertyName( typeDeclaration.name() );
            String varName = requestVarName + "." + property + "()";
            line( "if( " + varName + " !== null ){" );
            line( "this._requester.arrayHeader( '" + typeDeclaration.name() + "', " + varName + "->toJSON() );" );
            line( "}" );
        }
    }

    private void setQueryParameters( List<TypeDeclaration> typeDeclarations, String requestVarName ) throws IOException {
        for( TypeDeclaration typeDeclaration : typeDeclarations ) {
            String property = NamingUtility.propertyName( typeDeclaration.name() );
            String varName = requestVarName + "." + property + "()";
            line( "if( " + varName + " !== null ){" );
            if( typeDeclaration instanceof ArrayTypeDeclaration ) {
                line( "this._requester.arrayParameter( '" + typeDeclaration.name() + "', " + varName + "->toJSON() );" );
            } else {
                line( "this._requester.parameter( '" + typeDeclaration.name() + "', " + getParamValue( typeDeclaration, varName ) + " );" );
            }
            line( "}" );
        }
    }

    private void replaceUriParameters( HttpMethodDescriptor method, String requestVarName ) throws IOException {
        if( method.method().resource() != null ) {
            List<String> alreadyHandledParams = new ArrayList<>();
            Resource resource = method.method().resource();
            do {
                for( TypeDeclaration typeDeclaration : resource.uriParameters() ) {
                    if( typeDeclaration instanceof ArrayTypeDeclaration && !alreadyHandledParams.contains( typeDeclaration.name() ) ) {
                        line( requestVarName + ".forEach( function( element ){" );
                        line( "path.replace( '{" + typeDeclaration.name() + "}', " + getParamValue( ((ArrayTypeDeclaration) typeDeclaration).items(), "element" ) + " );" );
                        indent--;
                        line( "});" );
                        alreadyHandledParams.add( typeDeclaration.name() );
                    } else if( !alreadyHandledParams.contains( typeDeclaration.name() ) ) {
                        String variableName = requestVarName + "." + NamingUtility.propertyName( typeDeclaration.name() ) + "()";
                        line( "path.replace( '{" + typeDeclaration.name() + "}', " + getParamValue( typeDeclaration, variableName ) + " );" );
                        alreadyHandledParams.add( typeDeclaration.name() );
                    }
                }
                resource = resource.parentResource();
            } while( resource != null );
        }
        line( "this._requester.path( path );" );
    }

    private String getParamValue( TypeDeclaration typeDeclaration, String variableName ) {
        if( typeDeclaration.type().equals( "boolean" ) ) {
            return variableName + " ? 'true' : 'false'";
        }
        if( typeDeclaration.type().equals( "date-only" ) || typeDeclaration.type().equals( "time-only" ) || typeDeclaration.type().equals( "datetime" ) || typeDeclaration.type().equals( "tz-datetime" ) ) {
            return variableName + "->toJSON()";
        }
        return variableName;
    }

    private void generateConstructor() throws IOException {
        line( "/**" );
        line( "* @constructor" );
        line( "* @param {string} gatewayUrl" );
        line( "*/" );
        line( "constrcutor( requester, gatewayUrl ) {" );
        for( ResourceClientDescriptor clientDescriptor : clientDescriptor.nextFloorResourceClientGetters() ) {
            line( "this." + NamingUtility.attributeName( clientDescriptor.getClassName() ) + " = new " + clientDescriptor.getClassName() + "( $request, $gateway );" );
        }
        line( "this._gatewayUrl = gatewayUrl;" );
        line( "this._requester = requester" );
        line( "}" );
    }

    private void generateClientDescriptorGetters() throws IOException {
        for( ResourceClientDescriptor subClient : clientDescriptor.nextFloorResourceClientGetters() ) {
            line( "/**" );
            line( "* @returns " + subClient.getClassName() );
            line( "*/" );
            line( NamingUtility.propertyName( subClient.getClassName() ) + "() {" );
            line( "return this." + NamingUtility.attributeName( clientDescriptor.getClassName() ) + ";" );
            line( "}" );
        }
    }

}
