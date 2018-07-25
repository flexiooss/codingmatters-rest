package org.codingmatters.rest.php.api.client.generator;

import org.codingmatters.rest.php.api.client.Utils;
import org.codingmatters.rest.php.api.client.model.HttpMethodDescriptor;
import org.codingmatters.rest.php.api.client.model.ResourceClientDescriptor;
import org.raml.v2.api.model.v10.bodies.Response;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class PhpClassGenerator extends AbstractGenerator {

    private final String rootDir;
    private final String rootPackage;
    private final Utils utils;

    public PhpClassGenerator( String rootDir, String rootPackage ) {
        this.rootDir = rootDir + "/" + rootPackage.replace( ".", "/" );
        this.rootPackage = rootPackage.replace( ".", "\\" );
        this.utils = new Utils();
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
                        "( " + httpMethodDescriptor.getRequestType() + " " + utils.firstLetterLowerCase( httpMethodDescriptor.getRequestType() ) + " ): " +
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

            writer.write( "class " + resourceClientDescriptor.getClassName() + "Impl {" );
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
                writer.write( "public function " +
                        resourceNameLC + utils.firstLetterUpperCase( httpMethodDescriptor.method().method() ) +
                        "( " + httpMethodDescriptor.getRequestType() + " $" + utils.firstLetterLowerCase( httpMethodDescriptor.getRequestType() ) + " ): " +
                        httpMethodDescriptor.getResponseType() + " {"
                );
                String responseVar = "$" + utils.firstLetterLowerCase( httpMethodDescriptor.getResponseType() );
                newLine( writer, 2 );
                writer.write( "$path = " + httpMethodDescriptor.path() + ";" );
                newLine( writer, 2 );
                writer.write( "$this->httpRequester->path( $path );" );
                newLine( writer, 2 );
                writer.write( "$responseDelegate = $this->httpRequester->" + httpMethodDescriptor.method().method().toLowerCase( Locale.ENGLISH ) + "();" );
                newLine( writer, 2 );

                for( Response response : httpMethodDescriptor.method().responses() ) {
                    writer.write( "if( $responseDelegate == " + response.code().value() + "){"  );
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

    private void addAttributes( BufferedWriter writer, ResourceClientDescriptor resourceClientDescriptor ) throws IOException {
        writer.write( "private $httpRequester;" );
        newLine( writer, 1 );
        writer.write( "private $gatewayUrl;" );
        twoLine( writer, 1 );
        for( ResourceClientDescriptor clientDescriptor : resourceClientDescriptor.nextFloorResourceClientGetters() ) {
            writer.write( "private $" + utils.firstLetterLowerCase( clientDescriptor.getClassName() ) );
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
