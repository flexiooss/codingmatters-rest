package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.api.generator.type.RamlType;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.value.objects.spec.*;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;

/**
 * Created by nelt on 5/2/17.
 */
public class ApiTypesGenerator {

    private final Naming naming = new Naming();

    public Spec generate(RamlModelResult ramlModel) throws RamlSpecException {
        Spec.Builder result = Spec.spec();
        for (TypeDeclaration typeDeclaration : ramlModel.getApiV10().types()) {
            if(typeDeclaration.type().equals("object")) {
                ValueSpec.Builder valueSpec = ValueSpec.valueSpec()
                        .name(this.naming.type(typeDeclaration.name()));
                for (TypeDeclaration declaration : ((ObjectTypeDeclaration) typeDeclaration).properties()) {
                    valueSpec.addProperty(PropertySpec.property()
                            .name(this.naming.property(declaration.name()))
                            .type(this.typeSpecFromDeclaration(declaration))
                    );
                }

                result.addValue(valueSpec);
            }
        }
        return result.build();
    }

    private PropertyTypeSpec.Builder typeSpecFromDeclaration(TypeDeclaration declaration) throws RamlSpecException {
        if(declaration instanceof ArrayTypeDeclaration) {
            if(((ArrayTypeDeclaration)declaration).items().type().equals("object")) {
                if(((ArrayTypeDeclaration)declaration).items().name().equals("object")) {
                    return PropertyTypeSpec.type()
                            .cardinality(PropertyCardinality.LIST)
                            .typeKind(TypeKind.EMBEDDED)
                            .embeddedValueSpec(this.nestedType((ObjectTypeDeclaration) ((ArrayTypeDeclaration) declaration).items()));
                } else {
                    return PropertyTypeSpec.type()
                            .cardinality(PropertyCardinality.LIST)
                            .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                            .typeRef(((ArrayTypeDeclaration)declaration).items().name());
                }
            } else {
                return this.simpleProperty(((ArrayTypeDeclaration)declaration).items(), PropertyCardinality.LIST);
            }
        }

        if(declaration.type().equals("object")) {
            return PropertyTypeSpec.type()
                    .cardinality(PropertyCardinality.SINGLE)
                    .typeKind(TypeKind.EMBEDDED)
                    .embeddedValueSpec(this.nestedType((ObjectTypeDeclaration) declaration)
                    );
        } else {
            return this.simpleProperty(declaration, RamlType.isArrayType(declaration) ? PropertyCardinality.LIST : PropertyCardinality.SINGLE);
        }
    }

    private PropertyTypeSpec.Builder simpleProperty(TypeDeclaration declaration, PropertyCardinality withCardinality) throws RamlSpecException {
        if(this.isEnum(declaration)) {
            List<String> values = ((StringTypeDeclaration) declaration).enumValues();
            return PropertyTypeSpec.type()
                    .cardinality(withCardinality)
                    .typeKind(TypeKind.ENUM)
                    .enumValues(values.toArray(new String[values.size()]));
        } else if(RamlType.isRamlType(declaration)){
            return PropertyTypeSpec.type()
                    .cardinality(withCardinality)
                    .typeKind(TypeKind.JAVA_TYPE)
                    .typeRef(RamlType.from(declaration).javaType());
        } else {
            return PropertyTypeSpec.type()
                    .cardinality(withCardinality)
                    .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                    .typeRef(declaration.type());
        }
    }

    private boolean isEnum(TypeDeclaration declaration) {
        return declaration instanceof StringTypeDeclaration && !((StringTypeDeclaration)declaration).enumValues().isEmpty();
    }

    private AnonymousValueSpec.Builder nestedType(ObjectTypeDeclaration declaration) throws RamlSpecException {
        AnonymousValueSpec.Builder embedded = AnonymousValueSpec.anonymousValueSpec();
        for (TypeDeclaration objectProp : declaration.properties()) {
            if(objectProp.type().equals("object")) {
                embedded.addProperty(PropertySpec.property()
                        .name(this.naming.property(objectProp.name()))
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.SINGLE)
                                .typeKind(TypeKind.EMBEDDED)
                                .embeddedValueSpec(this.nestedType((ObjectTypeDeclaration) objectProp))
                        )
                );
            } else {
                embedded.addProperty(PropertySpec.property()
                        .name(this.naming.property(objectProp.name()))
                        .type(this.typeSpecFromDeclaration(objectProp))
                );
            }
        }
        return embedded;
    }
}
