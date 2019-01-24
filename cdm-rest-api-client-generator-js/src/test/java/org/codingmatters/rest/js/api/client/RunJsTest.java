package org.codingmatters.rest.js.api.client;

import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.io.File;

public class RunJsTest {

    @Test
    public void whenName_then() throws Exception {
        PackagesConfiguration packagesConfiguration
                = new PackagesConfiguration( "org.generated.client", "org.generated.api", "org.generated.types" );
        String dir = System.getProperty( "project.build.directory", "/home/nico/workspace/codingmatters-rest/cdm-rest-api-client-generator-js/target" ) + "/js-test";
        System.out.println( "Generating in " + dir );
        JsClientGenerator generator = new JsClientGenerator( packagesConfiguration, new File( dir ) );

        RamlModelResult ramlModel;
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "test.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        generator.generateApi( ramlModel );
        generator.generateTypes( ramlModel );


    }

}
