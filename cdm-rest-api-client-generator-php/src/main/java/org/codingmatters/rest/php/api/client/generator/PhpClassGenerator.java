package org.codingmatters.rest.php.api.client.generator;

import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.rest.php.api.client.Utils;
import org.codingmatters.rest.php.api.client.model.HttpMethodDescriptor;
import org.codingmatters.rest.php.api.client.model.ResourceClientDescriptor;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PhpClassGenerator extends AbstractGenerator {

    private final String rootDir;
    private final String rootPackage;
    private final Utils utils;
    private final String typesPackage;
    private final Naming naming;
    private final String clienPackage;

    public PhpClassGenerator( String rootDir, String rootPackage, String typesPackage, String clientPackage ) {
        this.rootDir = rootDir + "/" + rootPackage.replace( ".", "/" );
        this.rootPackage = rootPackage.replace( ".", "\\" );
        this.typesPackage = typesPackage.replace( ".", "\\" );
        this.clienPackage = clientPackage.replace( ".", "\\" );
        this.utils = new Utils();
        this.naming = new Naming();
    }

    public void generateInterface( ResourceClientDescriptor resourceClientDescriptor ) throws IOException {
        String resourceNameLC = utils.firstLetterLowerCase( resourceClientDescriptor.getClassName() );
        System.out.println( rootDir );
        try( BufferedWriter writer = new BufferedWriter( new FileWriter( rootDir + "/" + resourceClientDescriptor.getClassName() + ".php" ) ) ) {
            writer.write( "<?php" );
            twoLine( writer, 0 );
            writer.write( "namespace " + rootPackage + ";" );
            twoLine( writer, 0 );

            // TODO IMPORTS

            writer.write( "interface " + resourceClientDescriptor.getClassName() + " {" );
            twoLine( writer, 1 );

            for( ResourceClientDescriptor clientDescriptor : resourceClientDescriptor.nextFloorResourceClientGetters() ) {
                String descriptorLowerCase = utils.firstLetterLowerCase( clientDescriptor.getClassName() );
                writer.write( "public function " + descriptorLowerCase + "(): " + clientDescriptor.getClassName() + ";" );
                twoLine( writer, 1 );
            }

            for( HttpMethodDescriptor httpMethodDescriptor : resourceClientDescriptor.methodDescriptors() ) {
                writer.write( "public function " +
                        resourceNameLC + utils.firstLetterUpperCase( httpMethodDescriptor.method().method() ) +
                        "( " + httpMethodDescriptor.getRequestType() + " $" + utils.firstLetterLowerCase( httpMethodDescriptor.getRequestType() ) + " ): " +
                        httpMethodDescriptor.getResponseType() + ";"
                );
                twoLine( writer, 1 );
            }
            newLine( writer, 0 );
            writer.write( "}" );
            writer.flush();
        }
    }

    public void generateImplementationClass( ResourceClientDescriptor resourceClientDescriptor ) throws IOException {
        String resourceNameLC = utils.firstLetterLowerCase( resourceClientDescriptor.getClassName() );
        try( BufferedWriter writer = new BufferedWriter( new FileWriter( rootDir + "/" + resourceClientDescriptor.getClassName() + "Impl.php" ) ) ) {
            writer.write( "<?php" );
            twoLine( writer, 0 );
            writer.write( "namespace " + rootPackage + ";" );
            twoLine( writer, 0 );

            writer.write( "use io\\flexio\\utils\\http\\HttpRequester;" );
            twoLine( writer, 0 );


            writer.write( "class " + resourceClientDescriptor.getClassName() + "Impl implements " + resourceClientDescriptor.getClassName() + " {" );
            twoLine( writer, 1 );

            addAttributes( writer, resourceClientDescriptor );
            createConstructor( writer, resourceClientDescriptor );

            for( ResourceClientDescriptor clientDescriptor : resourceClientDescriptor.nextFloorResourceClientGetters() ) {
                String descriptorLowerCase = utils.firstLetterLowerCase( clientDescriptor.getClassName() );
                writer.write( "public function " + descriptorLowerCase + "(): " + clientDescriptor.getClassName() + "{" );
                newLine( writer, 2 );
                writer.write( "return $this->" + descriptorLowerCase + ";" );
                newLine( writer, 1 );
                writer.write( "}" );
                twoLine( writer, 1 );
            }

            for( HttpMethodDescriptor httpMethodDescriptor : resourceClientDescriptor.methodDescriptors() ) {
                String requestVarName = utils.firstLetterLowerCase( httpMethodDescriptor.getRequestType() );
                writer.write( "public function " +
                        resourceNameLC + utils.firstLetterUpperCase( httpMethodDescriptor.method().method() ) +
                        "( " + httpMethodDescriptor.getRequestType() + " $" + requestVarName + " ): " +
                        httpMethodDescriptor.getResponseType() + " {"
                );
                String responseVar = "$" + utils.firstLetterLowerCase( httpMethodDescriptor.getResponseType() );
                newLine( writer, 2 );
                writer.write( "$path = $this -> gatewayUrl.'" + httpMethodDescriptor.path() + "';" );
                newLine( writer, 2 );
                if( httpMethodDescriptor.method().resource() != null ) {
                    for( TypeDeclaration typeDeclaration : httpMethodDescriptor.method().resource().uriParameters() ) {
                        writer.write( "$path = str_replace( '{" + typeDeclaration.name() + "}', $" + requestVarName + " -> " + typeDeclaration.name() + "(), $path );" );
                        newLine( writer, 2 );
                    }
                }
                writer.write( "$this -> httpRequester -> path( $path );" );
                newLine( writer, 2 );
                for( TypeDeclaration typeDeclaration : httpMethodDescriptor.method().queryParameters() ) {
                    String property = naming.property( typeDeclaration.name() );
                    writer.write( "if( $" + requestVarName + " -> " + property + "() !== null ){" );
                    newLine( writer, 3 );
                    String variableName = "$" + requestVarName + " -> " + property + "()";
                    if( typeDeclaration instanceof ArrayTypeDeclaration ) {
                        writer.write( "$this -> httpRequester -> arrayParameter( '" + typeDeclaration.name() + "', " + variableName + "->jsonSerialize() );" );
                    } else {
                        writer.write( "$this -> httpRequester -> parameter( '" + typeDeclaration.name() + "', " + getValue( typeDeclaration, variableName ) + " );" );
                    }
                    newLine( writer, 2 );
                    writer.write( "}" );
                    newLine( writer, 2 );
                }

                for( TypeDeclaration typeDeclaration : httpMethodDescriptor.method().headers() ) {
                    String property = naming.property( typeDeclaration.name() );
                    writer.write( "if( $" + requestVarName + " -> " + property + "() !== null ){" );
                    newLine( writer, 3 );
                    String variableName = "$" + requestVarName + " -> " + property + "()";
                    if( typeDeclaration instanceof ArrayTypeDeclaration ) {
                        writer.write( "$this -> httpRequester -> arrayHeader( '" + typeDeclaration.name() + "', " + variableName + "->jsonSerialize() );" );
                    } else {
                        writer.write( "$this -> httpRequester -> header( '" + typeDeclaration.name() + "', " + getValue( typeDeclaration, variableName ) + " );" );
                    }
                    newLine( writer, 2 );
                    writer.write( "}" );
                    newLine( writer, 2 );
                }

                String method = httpMethodDescriptor.method().method().toLowerCase( Locale.ENGLISH );
                if( httpMethodDescriptor.payload() != null ) {
                    if( httpMethodDescriptor.payload().typeRef().equals( "string" ) ) {
                        writer.write( "$content = $" + requestVarName + " -> payload();" );
                        newLine( writer, 2 );
                        writer.write( "$contentType = $" + requestVarName + " -> contentType();" );
                        newLine( writer, 2 );
                    } else {
                        writer.write( "$content = json_encode( $" + requestVarName + " -> payload() );" );
                        newLine( writer, 2 );
                        writer.write( "$contentType = 'application/json';" );
                        newLine( writer, 2 );
                    }
                    writer.write( "$responseDelegate = $this->httpRequester->" + method + "( $contentType, $content );" );
                } else {
                    writer.write( "$responseDelegate = $this->httpRequester->" + method + "();" );
                }
                twoLine( writer, 2 );
                writer.write( responseVar + " = new " + httpMethodDescriptor.getResponseType() + "();" );
                newLine( writer, 2 );
                for( Response response : httpMethodDescriptor.method().responses() ) {
                    writer.write( "if( $responseDelegate->code() == " + response.code().value() + "){" );
                    newLine( writer, 3 );
                    writer.write( "$status = new \\" + rootPackage + "\\" + httpMethodDescriptor.getResponseType().toLowerCase() + "\\Status" + response.code().value() + "();" );
                    newLine( writer, 3 );
                    if( response.body() != null && !response.body().isEmpty() ) {
                        if( response.body().get( 0 ).type().equals( "file" ) ) {
                            writer.write( "$body = $responseDelegate -> body();" );
                            newLine( writer, 3 );
                            writer.write( "$status -> withPayload( $body );" );
                            newLine( writer, 3 );
                            writer.write( "$status -> contentType( $responseDelegate -> header( 'Content-type' ));" );
                            newLine( writer, 3 );

                        } else if( this.isObjectOrArray( response.body().get( 0 ) ) ) {
                            if( response.body().get( 0 ) instanceof ArrayTypeDeclaration ) {
                                writer.write( "$body = json_decode( $responseDelegate -> body(), true );" );
                                newLine( writer, 3 );
                                writer.write( "$list = new \\" + rootPackage + "\\" + responseVar.substring( 1 ).toLowerCase() + "\\status" + response.code().value() + "\\" + naming.type( "status", response.code().value(), "payload", "list" ) + "( $body );" );
                                newLine( writer, 3 );
                                writer.write( "$status -> withPayload( $list );" );
                                newLine( writer, 3 );
                            } else {
                                writer.write( "$body = json_decode( $responseDelegate -> body(), true );" );
                                newLine( writer, 3 );
                                writer.write( "$status -> withPayload( $body );" );
                                newLine( writer, 3 );
                            }
                        } else {
                            if( response.body().get( 0 ) instanceof ArrayTypeDeclaration ) {

                                String type = ((ArrayTypeDeclaration) response.body().get( 0 )).items().type();
                                if( type.equals( "object" ) && ((ArrayTypeDeclaration) response.body().get( 0 )).items().name() != null ) {
                                    type = ((ArrayTypeDeclaration) response.body().get( 0 )).items().name();
                                }
                                writer.write( "$reader = new \\" + typesPackage + "\\json\\" + type + "Reader();" );
                                newLine( writer, 3 );
                                writer.write( "$body = json_decode( $responseDelegate -> body(), true );" );
                                newLine( writer, 3 );
                                writer.write( "$list = new \\" + rootPackage + "\\" + responseVar.substring( 1 ).toLowerCase() + "\\status" + response.code().value() + "\\" + naming.type( "status", response.code().value(), "payload", "list" ) + "();" );
                                newLine( writer, 3 );
                                writer.write( "foreach( $body as $item ) {" );
                                newLine( writer, 4 );
                                writer.write( "$list[] = $reader->readArray( $item );" );
                                newLine( writer, 3 );
                                writer.write( "}" );
                                newLine( writer, 3 );
                                writer.write( "$status->withPayload( $list );" );
                                newLine( writer, 3 );
                            } else {
                                writer.write( "$reader = new \\" + typesPackage + "\\json\\" + response.body().get( 0 ).type() + "Reader();" );
                                newLine( writer, 3 );
                                writer.write( "$body = $reader -> read( $responseDelegate->body() );" );
                                newLine( writer, 3 );
                                writer.write( "$status -> withPayload( $body );" );
                                newLine( writer, 3 );
                            }
                        }
                    }
                    for( TypeDeclaration typeDeclaration : response.headers() ) {
                        if( typeDeclaration instanceof ArrayTypeDeclaration ) {
                            writer.write( "$list = new \\" + rootPackage + "\\" + httpMethodDescriptor.getResponseType().toLowerCase() + "\\status" + response.code().value() + "\\" + naming.type( "status", response.code().value(), typeDeclaration.name(), "list" ) + "();" );
                            writer.write( "foreach( $responseDelegate -> header('" + typeDeclaration.name() + "') as $item ){" );
                            writer.write( "$list[] = " + readSingleValueFromArray( "$item", ((ArrayTypeDeclaration) typeDeclaration).items().type() ) + ";" );
                            writer.write( "}" );
                            writer.write( "$status -> with" + naming.type( typeDeclaration.name() ) + "( $list );" );
                        } else {
                            writer.write( "$status -> with" + naming.type( typeDeclaration.name() ) + "( " + readSingleValueFromArray( "$responseDelegate -> header('" + typeDeclaration.name() + "')[0]", typeDeclaration.type() ) + " );" );
                        }
                        newLine( writer, 3 );
                    }
                    writer.write( responseVar + " -> withStatus" + response.code().value() + "( $status );" );
                    newLine( writer, 2 );
                    writer.write( "}" );
                    newLine( writer, 2 );
                }
                writer.write( "return " + responseVar + ";" );
                newLine( writer, 1 );
                writer.write( "}" );
                twoLine( writer, 1 );
            }

            newLine( writer, 0 );
            writer.write( "}" );

            writer.flush();
        }

    }

    private String readSingleValueFromArray( String variableName, String type ) {
        if( type.equals( "boolean" ) ) {
            return variableName + " == 'true' ? true : false";
        } else if( type.equals( "date-only" ) ) {
            return "\\io\\flexio\\utils\\FlexDate::newDate( " + variableName + " )";
        } else if( type.equals( "time-only" ) ) {
            return "\\io\\flexio\\utils\\FlexDate::newTime( " + variableName + " )";
        } else if( type.equals( "datetime" ) ) {
            return "\\io\\flexio\\utils\\FlexDate::newDateTime( " + variableName + " )";
        } else {
            return variableName;
        }
    }

    private String getValue( TypeDeclaration typeDeclaration, String variableName ) {
        if( typeDeclaration.type().equals( "boolean" ) ) {
            return variableName + " ? 'true' : 'false'";
        }
        if( typeDeclaration.type().equals( "date-only" ) || typeDeclaration.type().equals( "time-only" ) || typeDeclaration.type().equals( "datetime" ) ) {
            return variableName + "->jsonSerialize()";
        }
        return variableName;
    }

    private boolean isObjectOrArray( TypeDeclaration typeDeclaration ) {
        if( typeDeclaration instanceof ArrayTypeDeclaration ) {
            ArrayTypeDeclaration arrayDeclaration = (ArrayTypeDeclaration) typeDeclaration;
            if( arrayDeclaration.items().type() == null ) {
                return arrayDeclaration.items().name().equals( "object[]" ) || arrayDeclaration.items().name().equals( "array[]" );
            } else {
                return isObjectOrArray( arrayDeclaration.items() );
            }
        }
        return naming.isArbitraryObject( typeDeclaration );
    }

    private boolean needBody( String method ) {
        return "post".equals( method ) || "patch".equals( method ) || "put".equals( method );
    }

    private void addAttributes( BufferedWriter writer, ResourceClientDescriptor resourceClientDescriptor ) throws IOException {
        writer.write( "private $httpRequester;" );
        newLine( writer, 1 );
        writer.write( "private $gatewayUrl;" );
        twoLine( writer, 1 );
        for( ResourceClientDescriptor clientDescriptor : resourceClientDescriptor.nextFloorResourceClientGetters() ) {
            writer.write( "private $" + utils.firstLetterLowerCase( clientDescriptor.getClassName() ) + ";" );
            twoLine( writer, 1 );
        }
    }

    private void createConstructor( BufferedWriter writer, ResourceClientDescriptor resourceClientDescriptor ) throws IOException {
        writer.write( "public function __construct( HttpRequester $httpRequester, string $gatewayUrl ){" );
        newLine( writer, 2 );
        writer.write( "$this->httpRequester = $httpRequester;" );
        newLine( writer, 2 );
        writer.write( "$this->gatewayUrl = $gatewayUrl;" );

        for( ResourceClientDescriptor clientDescriptor : resourceClientDescriptor.nextFloorResourceClientGetters() ) {
            newLine( writer, 2 );
            writer.write( "$this->" + utils.firstLetterLowerCase( clientDescriptor.getClassName() ) + " = new " + clientDescriptor.getClassName() + "Impl( $httpRequester, $gatewayUrl );" );
        }
        newLine( writer, 1 );

        writer.write( "}" );
        twoLine( writer, 1 );
    }

    public void generateRootClientInterface( String apiName, List<ResourceClientDescriptor> clientDescriptors ) throws IOException {
        String className = naming.type( apiName, "Client" );
        try( BufferedWriter writer = new BufferedWriter( new FileWriter( rootDir.replace( rootPackage.replace( "\\", "/" ), "" ) + "/" + clienPackage.replace( "\\", "/" ) + "/" + className + ".php" ) ) ) {
            writer.write( "<?php" );
            twoLine( writer, 0 );
            writer.write( "namespace " + clienPackage + ";" );
            twoLine( writer, 0 );
            writer.write( "interface " + className + " {" );
            twoLine( writer, 1 );
            for( ResourceClientDescriptor clientDescriptor : clientDescriptors ) {
                writer.write( "public function " + naming.property( clientDescriptor.getClassName() ) + "(): \\" + rootPackage + "\\" + naming.type( clientDescriptor.getClassName() ) + ";" );
                twoLine( writer, 1 );
            }
            newLine( writer, 0 );
            writer.write( "}" );
            writer.flush();
        }
    }

    public void generateRootClientRequesterImpl( String apiName, List<ResourceClientDescriptor> clientDescriptors ) throws IOException {
        String className = naming.type( apiName, "Client" );
        try( BufferedWriter writer = new BufferedWriter( new FileWriter( rootDir.replace( rootPackage.replace( "\\", "/" ), "" ) + "/" + clienPackage.replace( "\\", "/" ) + "/" + className + "Requester.php" ) ) ) {
            writer.write( "<?php" );
            twoLine( writer, 0 );
            writer.write( "namespace " + clienPackage + ";" );
            twoLine( writer, 0 );
            writer.write( "class " + className + "Requester implements \\" + clienPackage + "\\" + className + " {" );
            twoLine( writer, 1 );

            for( ResourceClientDescriptor clientDescriptor : clientDescriptors ) {
                writer.write( "private $" + naming.property( clientDescriptor.getClassName() ) + ";" );
                newLine( writer, 1 );
            }

            newLine( writer, 1 );
            writer.write( "public function __construct( \\io\\flexio\\utils\\http\\HttpRequester $requester, string $gatewayUrl ) {" );
            for( ResourceClientDescriptor clientDescriptor : clientDescriptors ) {
                newLine( writer, 2 );
                writer.write( "$this -> " + naming.property( clientDescriptor.getClassName() ) + " = new \\" + rootPackage + "\\" + naming.type( clientDescriptor.getClassName() ) + "Impl( $requester, $gatewayUrl );" );
            }
            newLine( writer, 1 );
            writer.write( "}" );

            for( ResourceClientDescriptor clientDescriptor : clientDescriptors ) {
                twoLine( writer, 1 );
                writer.write( "public function " + naming.property( clientDescriptor.getClassName() ) + "(): \\" + rootPackage + "\\" + naming.type( clientDescriptor.getClassName() ) + " {" );
                newLine( writer, 2 );
                writer.write( "return $this -> " + naming.property( clientDescriptor.getClassName() ) + ";" );
                newLine( writer, 1 );
                writer.write( "}" );
            }
            newLine( writer, 0 );
            writer.write( "}" );
            writer.flush();
        }
    }
}
