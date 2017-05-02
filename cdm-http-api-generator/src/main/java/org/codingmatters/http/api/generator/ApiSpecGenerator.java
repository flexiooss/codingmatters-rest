package org.codingmatters.http.api.generator;

import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by nelt on 5/2/17.
 */
public class ApiSpecGenerator {
    public Spec generate(RamlModelResult ramlModel) {
        Spec.Builder result = Spec.spec();
        for (Resource resource : ramlModel.getApiV10().resources()) {
            this.generateResourceValues(result, resource);
        }

        return result.build();
    }

    private void generateResourceValues(Spec.Builder result, Resource resource) {
        String resourceName = resource.displayName().value();
        for (Method method : resource.methods()) {
            result.addValue(this.generateMethodRequestValue(resourceName, method));
            result.addValue(this.generateMethodResponseValue(resourceName, method));
        }
        for (Resource subResource : resource.resources()) {
            this.generateResourceValues(result, subResource);
        }
    }

    private ValueSpec generateMethodRequestValue(String resourceName, Method method) {
        return ValueSpec.valueSpec()
                .name(resourceName + this.upperCaseFirst(method.method()) + "Request")
                .build();
    }

    private ValueSpec generateMethodResponseValue(String resourceName, Method method) {
        return ValueSpec.valueSpec()
                .name(resourceName + this.upperCaseFirst(method.method()) + "Response")
                .build();
    }

    private String upperCaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
