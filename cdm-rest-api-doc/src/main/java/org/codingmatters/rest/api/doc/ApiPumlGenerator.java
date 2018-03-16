package org.codingmatters.rest.api.doc;

import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.io.IOException;

public class ApiPumlGenerator {

    private final RamlModelResult ramlModel;
    private final String apiPackage;
    private final String typePackage;
    private final File toDirectory;

    public ApiPumlGenerator(RamlModelResult ramlModel, String apiPackage, String typePackage, File toDirectory) {
        this.ramlModel = ramlModel;
        this.apiPackage = apiPackage;
        this.typePackage = typePackage;
        this.toDirectory = toDirectory;
    }

    public void generateTo() throws IOException {

    }
}
