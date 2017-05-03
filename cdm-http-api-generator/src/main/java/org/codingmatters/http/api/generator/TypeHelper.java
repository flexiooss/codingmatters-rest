package org.codingmatters.http.api.generator;

import org.codingmatters.http.api.generator.exception.RamlSpecException;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by nelt on 5/2/17.
 */
public class TypeHelper {

    public PropertyTypeSpec.Builder typeSpecFromDeclaration(TypeDeclaration typeDeclaration) throws RamlSpecException {
        PropertyTypeSpec.Builder typeSpec = PropertyTypeSpec.type();
        if(typeDeclaration.type().equals("array")) {
            typeSpec.cardinality(PropertyCardinality.LIST)
                    .typeKind(TypeKind.JAVA_TYPE)
                    .typeRef(this.javaType(((ArrayTypeDeclaration)typeDeclaration).items().type()));
        } else {
            typeSpec.cardinality(PropertyCardinality.SINGLE)
                    .typeKind(TypeKind.JAVA_TYPE)
                    .typeRef(this.javaType(typeDeclaration.type()));
        }
        return typeSpec;
    }

    public String javaType(String ramlType) throws RamlSpecException {
        switch (ramlType) {
            case "string":
                return String.class.getName();
            case "integer":
                return Long.class.getName();
        }
        throw new RamlSpecException("not implemented type : " + ramlType);
    }
}
