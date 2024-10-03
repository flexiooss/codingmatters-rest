package org.codingmatters.rest.js.api.client;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Ignore // hbshed not runnable by ci runner
public class RunJsTest {

    private static ProcessBuilder processBuilder;

    @BeforeClass
    public static void setUp() throws Exception {
        String dir = System.getProperty( "project.build.directory" ) + "/js-test";
        processBuilder = new ProcessBuilder();
        processBuilder.directory( new File( dir ) );
        processBuilder.command( "hbshed", "install" );
        System.out.println( "Running 'hbshed install'" );
        Process process = processBuilder.start();
        process.waitFor( 60, TimeUnit.SECONDS );
        if( process.exitValue() != 0 ){
            printError( process );
        }
        assertThat( process.exitValue(), is( 0 ) );
    }

    private static void printError( Process process ) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try( InputStream stream = process.getInputStream() ){
            while( stream.read( buffer ) != -1 ){
                out.write( buffer );
            }
            System.out.println( "Out = " + new String( out.toByteArray() ) );
        }
        try( InputStream stream = process.getErrorStream() ){
            while( stream.read( buffer ) != -1 ){
                out.write( buffer );
            }
            System.out.println( "Error = " + new String( out.toByteArray() ) );
        }
    }

    @Test
    public void runTests() throws Exception {
        String[] ramlLocation = {
                Thread.currentThread().getContextClassLoader().getResource( "parameters.raml" ).getPath(),
                Thread.currentThread().getContextClassLoader().getResource( "requestBody.raml" ).getPath(),
                Thread.currentThread().getContextClassLoader().getResource( "factorized_enum.raml" ).getPath(),
                Thread.currentThread().getContextClassLoader().getResource( "complextype.raml" ).getPath()
        };

        String dir = System.getProperty( "project.build.directory" ) + "/js-test";
        System.out.println( "Generating in " + dir );
        JSClientGenerator generator = new JSClientGenerator( new File( dir ), "org.generated.client", "org.generated.api.types", "org.generated.api", "phpGeneration", "unitTests", "1.0" );
        generator.generateClientApi( false, ramlLocation );

        System.out.println( "Running 'hbshed test' in " + dir );
        processBuilder.directory( new File( dir ) );
        processBuilder.command( "hbshed", "test", "-V", "-C", "--source-map", "--strict" );
        Process process = processBuilder.start();

        process.waitFor( 120, TimeUnit.SECONDS );
        if( process.exitValue() != 0 ){
        }
        printError( process );
        assertThat( process.exitValue(), is( 0 ) );
        System.out.println( "EXIT == " + process.exitValue() );
    }

}
