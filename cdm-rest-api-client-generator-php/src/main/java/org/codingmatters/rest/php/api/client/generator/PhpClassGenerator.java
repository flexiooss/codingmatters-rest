package org.codingmatters.rest.php.api.client.generator;

import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.rest.php.api.client.Utils;
import org.codingmatters.rest.php.api.client.model.HttpMethodDescriptor;
import org.codingmatters.rest.php.api.client.model.Payload;
import org.codingmatters.rest.php.api.client.model.ResourceClientDescriptor;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class PhpClassGenerator extends AbstractGenerator {

    private final String rootDir;
    private final String rootPackage;
    private final Utils utils;
    private final String typesPackage;
    private final Naming naming;

    public PhpClassGenerator( String rootDir, String rootPackage, String typesPackage ) {
        this.rootDir = rootDir + "/" + rootPackage.replace( ".", "/" );
        this.rootPackage = rootPackage.replace( ".", "\\" );
        this.typesPackage = typesPackage.replace( ".", "\\" );
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

            // TODO IMPORTS
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
                    writer.write( "$this -> httpRequester -> parameter( '" + typeDeclaration.name() + "', $" +requestVarName+" -> " + property + "() );" );
                    newLine( writer, 2 );
                    writer.write( "}" );
                    newLine( writer, 2 );
                }

                for( TypeDeclaration typeDeclaration : httpMethodDescriptor.method().headers() ) {
                    String property = naming.property( typeDeclaration.name() );
                    writer.write( "if( $" + requestVarName + " -> " + property + "() !== null ){" );
                    newLine( writer, 3 );
                    writer.write( "$this -> httpRequester -> header( '" + typeDeclaration.name() + "', $" +requestVarName+" -> " + property + "() );" );
                    newLine( writer, 2 );
                    writer.write( "}" );
                    newLine( writer, 2 );
                }

                String method = httpMethodDescriptor.method().method().toLowerCase( Locale.ENGLISH );
                if( needBody( method ) ) {
                    if( httpMethodDescriptor.getPayload() != null ) {
                        if( httpMethodDescriptor.getPayload().type() == Payload.Type.VALUE_OBJECT ) {
                            writer.write( "$writer = new \\" + typesPackage + "\\json\\" + httpMethodDescriptor.getPayload().typeRef() + "Writer();" );
                            newLine( writer, 2 );
                            writer.write( "$content = $writer->write( $" + requestVarName + " -> payload() );" );
                            newLine( writer, 2 );
                            writer.write( "$contentType = 'application/json';" );
                            newLine( writer, 2 );
                        }
                        // TODO :^)
                    } else {
                        writer.write( "$content = null;" );
                        newLine( writer, 2 );
                        writer.write( "$contentType = '';" );
                        newLine( writer, 2 );
                    }
                    writer.write( "$responseDelegate = $this->httpRequester->" + method + "( $contentType, $content );" );
                } else {
                    writer.write( "$responseDelegate = $this->httpRequester->" + method + "();" );
                }
                newLine( writer, 2 );

                for( Response response : httpMethodDescriptor.method().responses() ) {
                    writer.write( "if( $responseDelegate == " + response.code().value() + "){" );
                    newLine( writer, 3 );

                    newLine( writer, 2 );
                    writer.write( "}" );
                }

                writer.write( responseVar + " = new " + httpMethodDescriptor.getResponseType() + "();" );
                newLine( writer, 2 );
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


}
