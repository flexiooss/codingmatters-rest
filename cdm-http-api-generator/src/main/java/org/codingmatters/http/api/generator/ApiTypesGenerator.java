package org.codingmatters.http.api.generator;

import org.codingmatters.http.api.generator.exception.RamlSpecException;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by nelt on 5/2/17.
 */
public class ApiTypesGenerator {
    private final TypeHelper typeHelper = new TypeHelper();

    public Spec generate(RamlModelResult ramlModel) throws RamlSpecException {
        Spec.Builder result = Spec.spec();
        for (TypeDeclaration typeDeclaration : ramlModel.getApiV10().types()) {
            if(typeDeclaration.type().equals("object")) {
                ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) typeDeclaration;
                ValueSpec.Builder valueSpec = ValueSpec.valueSpec()
                        .name(objectType.name());
                for (TypeDeclaration declaration : objectType.properties()) {
                    valueSpec.addProperty(PropertySpec.property()
                            .name(declaration.name())
                            .type(this.typeHelper.typeSpecFromDeclaration(declaration))
                    );
                }

                result.addValue(valueSpec);
            }
        }
        return result.build();
    }
}
