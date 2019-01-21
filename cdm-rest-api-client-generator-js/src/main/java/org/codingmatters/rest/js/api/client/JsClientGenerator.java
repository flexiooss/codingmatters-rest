package org.codingmatters.rest.js.api.client;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.js.api.client.types.ApiTypesJsGenerator;
import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.io.IOException;

public class JsClientGenerator {

    private final ApiTypesJsGenerator apiTypesGenerator;
    private final PackagesConfiguration packagesConfiguration;
    private final File rootDir;

    public JsClientGenerator( PackagesConfiguration packagesConfiguration, File rootDir ) {
        this.packagesConfiguration = packagesConfiguration;
        this.rootDir = rootDir;
        this.apiTypesGenerator = new ApiTypesJsGenerator( packagesConfiguration );
    }


    public void generate( RamlModelResult model ) throws RamlSpecException, IOException {
        apiTypesGenerator.generate( model );
    }

}
