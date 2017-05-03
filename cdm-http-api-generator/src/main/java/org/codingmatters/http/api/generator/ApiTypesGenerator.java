package org.codingmatters.http.api.generator;

import org.codingmatters.http.api.generator.exception.RamlSpecException;
import org.codingmatters.http.api.generator.type.RamlType;
import org.codingmatters.value.objects.spec.*;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by nelt on 5/2/17.
 */
public class ApiTypesGenerator {

    public Spec generate(RamlModelResult ramlModel) throws RamlSpecException {
        Spec.Builder result = Spec.spec();
        for (TypeDeclaration typeDeclaration : ramlModel.getApiV10().types()) {
            if(typeDeclaration.type().equals("object")) {
                ValueSpec.Builder valueSpec = ValueSpec.valueSpec()
                        .name(typeDeclaration.name());
                for (TypeDeclaration declaration : ((ObjectTypeDeclaration) typeDeclaration).properties()) {
                    valueSpec.addProperty(PropertySpec.property()
                            .name(declaration.name())
                            .type(this.typeSpecFromDeclaration(declaration))
                    );
                }

                result.addValue(valueSpec);
            }
        }
        return result.build();
    }

    private PropertyTypeSpec.Builder typeSpecFromDeclaration(TypeDeclaration declaration) throws RamlSpecException {
        if(declaration.type().equals("array")) {
            if(((ArrayTypeDeclaration)declaration).items().type().equals("object")) {
                return PropertyTypeSpec.type()
                        .cardinality(PropertyCardinality.LIST)
                        .typeKind(TypeKind.EMBEDDED)
                        .embeddedValueSpec(this.nestedType((ObjectTypeDeclaration) ((ArrayTypeDeclaration)declaration).items()));
            } else {
                return PropertyTypeSpec.type()
                        .cardinality(PropertyCardinality.LIST)
                        .typeKind(TypeKind.JAVA_TYPE)
                        .typeRef(RamlType.from(((ArrayTypeDeclaration)declaration).items()).javaType());
            }
        }

        if(declaration.type().equals("object")) {
            return PropertyTypeSpec.type()
                            .cardinality(PropertyCardinality.SINGLE)
                            .typeKind(TypeKind.EMBEDDED)
                            .embeddedValueSpec(this.nestedType((ObjectTypeDeclaration) declaration)
                    );
        } else {
            return PropertyTypeSpec.type()
                    .cardinality(PropertyCardinality.SINGLE)
                    .typeKind(TypeKind.JAVA_TYPE)
                    .typeRef(RamlType.from(declaration).javaType());
        }
    }

    private AnonymousValueSpec.Builder nestedType(ObjectTypeDeclaration declaration) throws RamlSpecException {
        AnonymousValueSpec.Builder embedded = AnonymousValueSpec.anonymousValueSpec();
        for (TypeDeclaration objectProp : declaration.properties()) {
            if(objectProp.type().equals("object")) {
                embedded.addProperty(PropertySpec.property()
                        .name(objectProp.name())
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.SINGLE)
                                .typeKind(TypeKind.EMBEDDED)
                                .embeddedValueSpec(this.nestedType((ObjectTypeDeclaration) objectProp))
                        )
                );
            } else {
                embedded.addProperty(PropertySpec.property()
                        .name(objectProp.name())
                        .type(this.typeSpecFromDeclaration(objectProp))
                );
            }
        }
        return embedded;
    }
}
