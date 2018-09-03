package org.codingmatters.rest.php.api.client;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RunPhpTest {

    private static ProcessBuilder processBuilder;

    @BeforeClass
    public static void setUp() throws Exception {
        String dir = System.getProperty( "project.build.directory" ) + "/php-test";
        processBuilder = new ProcessBuilder();
        processBuilder.directory( new File( dir ) );
        processBuilder.command( "composer", "install" );
        Process process = processBuilder.start();
        process.waitFor( 30, TimeUnit.SECONDS );
        if( process.exitValue() != 0 ) {
            printError( process.getInputStream() );
            printError( process.getErrorStream() );
        }
        assertThat( process.exitValue(), is( 0 ) );
    }

    private static void printError( InputStream stream ) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while( stream.read( buffer ) != -1 ) {
            out.write( buffer );
        }
        System.out.println( new String( out.toByteArray() ) );

    }

    @Test
    public void testPayloadAndParameters() throws InterruptedException, IOException {
        processBuilder.command( "./vendor/bin/phpunit", "test/testPayloadAndParameters.php" );
        Process process = processBuilder.start();
        process.waitFor( 10, TimeUnit.SECONDS );
        if( process.exitValue() != 0 ) {
            printError( process.getInputStream() );
            printError( process.getErrorStream() );
        }
        assertThat( process.exitValue(), is( 0 ) );
    }

}
