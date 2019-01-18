package org.codingmatters.rest.js.api.client;

import java.io.File;

public class Main {

    public static void main( String[] args ) {
        String targetDir = System.getProperty( "generationTargetDir"
                , "/home/nico/workspace/codingmatters-rest/cdm-rest-api-client-generator-php/target/php-test"
        );
        if( targetDir == null ) {
            System.out.println( "Property \'generationTargetDir\' not found" );
            System.exit( -1 );
        }

        File rootDir = new File( targetDir );

    }

}
