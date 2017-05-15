package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.value.objects.spec.Spec;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.File;

/**
 * Created by nelt on 5/15/17.
 */
public class HandlersGenerator {

    private final String typesPackage;
    private final String apiPackage;
    private final Naming naming = new Naming();
    private final File rootDirectory;

    public HandlersGenerator(String typesPackage, String apiPackage, File toDirectory) {
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
        this.rootDirectory = toDirectory;
    }

    public Spec generate(RamlModelResult ramlModel) throws RamlSpecException {
        Spec.Builder result = Spec.spec();
        for (Resource resource : ramlModel.getApiV10().resources()) {
//            this.generateResourceValues(result, resource);
        }

        return result.build();
    }

}
