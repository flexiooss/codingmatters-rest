package org.codingmatters.http.api.generator;

import org.codingmatters.http.api.generator.exception.RamlSpecException;
import org.codingmatters.value.objects.spec.*;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
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
            PropertyTypeSpec.Builder typeSpec = PropertyTypeSpec.type();
            if(parameter.type().equals("array")) {
                typeSpec.cardinality(PropertyCardinality.LIST)
                        .typeKind(TypeKind.JAVA_TYPE)
                        .typeRef(this.javaType(((ArrayTypeDeclaration)parameter).items().type()));
            } else {
                typeSpec.cardinality(PropertyCardinality.SINGLE)
                        .typeKind(TypeKind.JAVA_TYPE)
                        .typeRef(this.javaType(parameter.type()));
            }


//            System.out.println(parameter.type());
//            if(parameter.type().equals("array")) {
//                System.out.println(((ArrayTypeDeclaration)parameter).items().type());
//            }
            result.addProperty(PropertySpec.property()
                    .name(parameter.name())
                    .type(typeSpec)
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
