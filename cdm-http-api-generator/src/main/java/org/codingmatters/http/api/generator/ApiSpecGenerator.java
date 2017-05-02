package org.codingmatters.http.api.generator;

import org.codingmatters.http.api.generator.exception.RamlSpecException;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by nelt on 5/2/17.
 */
public class ApiSpecGenerator {
    private final TypeHelper typeHelper = new TypeHelper();

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

        for (TypeDeclaration typeDeclaration : method.queryParameters()) {
            this.addPropertyFromTypeDeclaration(result, typeDeclaration);
        }
        for (TypeDeclaration typeDeclaration : method.headers()) {
            this.addPropertyFromTypeDeclaration(result, typeDeclaration);
        }


        return result
                .build();
    }

    private void addPropertyFromTypeDeclaration(ValueSpec.Builder result, TypeDeclaration typeDeclaration) throws RamlSpecException {
        PropertyTypeSpec.Builder typeSpec = this.typeHelper.typeSpecFromDeclaration(typeDeclaration);
        result.addProperty(PropertySpec.property()
                .name(typeDeclaration.name())
                .type(typeSpec)
                .build());
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
