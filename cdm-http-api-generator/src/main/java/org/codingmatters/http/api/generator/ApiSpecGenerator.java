package org.codingmatters.http.api.generator;

import org.codingmatters.http.api.generator.exception.RamlSpecException;
import org.codingmatters.value.objects.spec.*;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by nelt on 5/2/17.
 */
public class ApiSpecGenerator {
    public Spec generate(RamlModelResult ramlModel) throws RamlSpecException {
        Spec.Builder result = Spec.spec();
        for (Resource resource : ramlModel.getApiV10().resources()) {
            this.generateResourceValues(result, resource);
        }

        return result.build();
    }

    private void generateResourceValues(Spec.Builder result, Resource resource) throws RamlSpecException {
        String resourceName = resource.displayName().value();
        for (Method method : resource.methods()) {
            result.addValue(this.generateMethodRequestValue(resourceName, method));
            result.addValue(this.generateMethodResponseValue(resourceName, method));
        }
        for (Resource subResource : resource.resources()) {
            this.generateResourceValues(result, subResource);
        }
    }

    private ValueSpec generateMethodRequestValue(String resourceName, Method method) throws RamlSpecException {
        ValueSpec.Builder result = ValueSpec.valueSpec()
                .name(resourceName + this.upperCaseFirst(method.method()) + "Request");

        for (TypeDeclaration parameter : method.queryParameters()) {
            System.out.println(parameter.type());
            result.addProperty(PropertySpec.property()
                    .name(parameter.name())
                    .type(PropertyTypeSpec.type()
                            .cardinality(PropertyCardinality.SINGLE)
                            .typeKind(TypeKind.JAVA_TYPE)
                            .typeRef(this.javaType(parameter.type()))
                    )
                    .build());
        }

        return result
                .build();
    }

    private String javaType(String ramlType) throws RamlSpecException {
        switch (ramlType) {
            case "string":
                return String.class.getName();
            case "integer":
                return Long.class.getName();
        }
        throw new RamlSpecException("not implemented type : " + ramlType);
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
