package org.codingmatters.rest.js.api.client;

import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RunJsTest {

    private static ProcessBuilder processBuilder;

    @BeforeClass
    public static void setUp( ) throws Exception {
        String dir = System.getProperty( "project.build.directory" ) + "/js-test";
        processBuilder = new ProcessBuilder();
        processBuilder.directory( new File( dir ) );
        processBuilder.command( "yarn", "install" );
        System.out.println( "Running 'yarn install'" );
        Process process = processBuilder.start();
        process.waitFor( 60, TimeUnit.SECONDS );
        if( process.exitValue() != 0 ) {
            printError( process );
        }
        assertThat( process.exitValue(), is( 0 ) );

        processBuilder.command( "yarn", "link", "flexio-jshelpers" );
        System.out.println( "Running 'yarn link flexio-jshelpers'" );
        process = processBuilder.start();
        process.waitFor( 60, TimeUnit.SECONDS );
        if( process.exitValue() != 0 ) {
            printError( process );
        }
        assertThat( process.exitValue(), is( 0 ) );
    }

    private static void printError( Process process ) throws IOException {
        byte[] buffer = new byte[ 1024 ];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try( InputStream stream = process.getInputStream() ) {
            while( stream.read( buffer ) != -1 ) {
                out.write( buffer );
            }
            System.out.println( "Out = " + new String( out.toByteArray() ) );
        }
        try( InputStream stream = process.getErrorStream() ) {
            while( stream.read( buffer ) != -1 ) {
                out.write( buffer );
            }
            System.out.println( "Error = " + new String( out.toByteArray() ) );
        }
    }

    @Test
    public void testParameters( ) throws Exception {
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "parameters.raml" ).getPath();
        String dir = System.getProperty( "project.build.directory" ) + "/js-test";
        System.out.println( "Generating in " + dir );
        JSClientGenerator generator = new JSClientGenerator( new File( dir ), "org.generated" );
        generator.generateClientApi( ramlLocation );

        System.out.println( "Running 'yarn test' in " + dir );
        processBuilder.directory( new File( dir ) );
        processBuilder.command( "yarn", "test", "-v" );
        Process process = processBuilder.start();

        process.waitFor( 120, TimeUnit.SECONDS );
        if( process.exitValue() != 0 ) {
            printError( process );
        }
        assertThat( process.exitValue(), is( 0 ) );
        System.out.println( "EXIT == " + process.exitValue() );
    }

//    @Test
//    public void whenName_then( ) throws Exception {
//        PackagesConfiguration packagesConfiguration
//                = new PackagesConfiguration( "org.generated.client", "org.generated.api", "org.generated.api.types" );
//        String dir = System.getProperty( "project.build.directory" ) + "/js-test";
//        System.out.println( "Generating in " + dir );
//        JsClientGenerator generator = new JsClientGenerator( packagesConfiguration, new File( dir ) );
//
//        RamlModelResult ramlModel;
//        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "test.raml" ).getPath();
//        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );
//
//        generator.generateApi( ramlModel );
////        generator.generateTypes( ramlModel );
//
//        System.out.println( "Running 'yarn test' in " + dir );
//        processBuilder.directory( new File( dir ) );
//        processBuilder.command( "yarn", "test", "-v" );
//        Process process = processBuilder.start();
//
//        process.waitFor( 120, TimeUnit.SECONDS );
//        if( process.exitValue() != 0 ) {
//            printError( process );
//        }
//        assertThat( process.exitValue(), is( 0 ) );
//        System.out.println( "EXIT == " + process.exitValue() );
//    }

}
