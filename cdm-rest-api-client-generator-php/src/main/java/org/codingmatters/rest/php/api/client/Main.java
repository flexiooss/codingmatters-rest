package org.codingmatters.rest.php.api.client;

import org.codingmatters.rest.php.api.client.model.ApiGeneratorPhp;
import org.codingmatters.rest.php.api.client.model.ApiTypesPhpGenerator;
import org.codingmatters.value.objects.php.generator.SpecPhpGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.io.File;

public class Main {

    public static void main( String[] args ) {
        try {
            String targetDir = System.getProperty( "generationTargetDir" );
            if( targetDir == null ) {
                System.out.println( "Property \'generationTargetDir\' not found" );
                System.exit( -1 );
            }

            File rootDir = new File( targetDir );

            String clientPackage = "org.generated.client";
            String apiPackage = "org.generated.api";
            String typesPackage = "org.generated.types";

            PhpClientRequesterGenerator requesterGenerator = new PhpClientRequesterGenerator( clientPackage, apiPackage, typesPackage, rootDir );

            RamlModelResult model = new RamlModelBuilder().buildApi( rootDir.getPath() + "/test.raml" );

            System.out.println( "Generating api in " + rootDir );

            Spec spec = new ApiTypesPhpGenerator( typesPackage ).generate( model );
            new SpecPhpGenerator( spec, typesPackage, rootDir ).generate();

            spec = new ApiGeneratorPhp( typesPackage ).generate( model );
            new SpecPhpGenerator( spec, apiPackage, rootDir ).generate();

            requesterGenerator.generate( model );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

}
