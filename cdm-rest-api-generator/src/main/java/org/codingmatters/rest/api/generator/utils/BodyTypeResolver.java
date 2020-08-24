package org.codingmatters.rest.api.generator.utils;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.api.generator.type.RamlType;
import org.codingmatters.rest.api.types.File;
import org.codingmatters.value.objects.generation.Naming;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

public class BodyTypeResolver {
    private final TypeDeclaration typeDeclaration;
    private final String typesPackage;
    private final Naming naming = new Naming();

    public BodyTypeResolver(TypeDeclaration typeDeclaration, String typesPackage) {
        this.typeDeclaration = typeDeclaration;
        this.typesPackage = typesPackage;
    }

    public PropertyTypeSpec.Builder resolve() throws RamlSpecException {
        if(RamlType.isRamlType(typeDeclaration)) {
            return this.typeSpecFromDeclaration(typeDeclaration);
        } else {
            if (typeDeclaration instanceof ArrayTypeDeclaration) {
                if(this.isAlreadyDefined(((ArrayTypeDeclaration) typeDeclaration).items())) {
                    return PropertyTypeSpec.type()
                            .cardinality(PropertyCardinality.LIST)
                            .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                            .typeRef(this.alreadyDefined(((ArrayTypeDeclaration) typeDeclaration).items()));
                } else if(this.naming.isArbitraryObjectArray(typeDeclaration)) {
                    return PropertyTypeSpec.type()
                            .cardinality(PropertyCardinality.LIST)
                            .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                            .typeRef(this.naming.arbitraryObjectImpl(typeDeclaration));
                } else {
                    String typeRef;
                    if (((ArrayTypeDeclaration) typeDeclaration).items().name().equals("file")) {
                        typeRef = File.class.getName();
                    } else {
                        String typeName = ((ArrayTypeDeclaration) typeDeclaration).items().type().equals("object") ? ((ArrayTypeDeclaration) typeDeclaration).items().name() : ((ArrayTypeDeclaration) typeDeclaration).items().type();
                        typeRef = this.typesPackage + "." + typeName;
                    }
                    return PropertyTypeSpec.type()
                            .cardinality(PropertyCardinality.LIST)
                            .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                            .typeRef(typeRef);
                }
            } else {
                if(this.isAlreadyDefined(typeDeclaration)) {
                    return PropertyTypeSpec.type()
                            .cardinality(PropertyCardinality.SINGLE)
                            .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                            .typeRef(this.alreadyDefined(typeDeclaration));
                } else if(this.naming.isArbitraryObject(typeDeclaration)) {
                    return PropertyTypeSpec.type()
                            .cardinality(PropertyCardinality.SINGLE)
                            .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                            .typeRef(this.naming.arbitraryObjectImpl(typeDeclaration));
                } else {
                    String typeRef;
                    if (typeDeclaration.type().equals("file")) {
                        typeRef = File.class.getName();
                    } else {
                        typeRef = this.typesPackage + "." + typeDeclaration.type();
                    }
                    return PropertyTypeSpec.type()
                            .cardinality(PropertyCardinality.SINGLE)
                            .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                            .typeRef(typeRef);
                }
            }
        }
    }

    private boolean isAlreadyDefined(TypeDeclaration typeDeclaration) {
        if(this.naming.isAlreadyDefined(typeDeclaration)) return true;
        for (TypeDeclaration parentType : typeDeclaration.parentTypes()) {
            if(this.naming.isAlreadyDefined(parentType)) {
                return true;
            }
        }
        return false;
    }

    private PropertyTypeSpec.Builder typeSpecFromDeclaration(TypeDeclaration typeDeclaration) throws RamlSpecException {
        PropertyTypeSpec.Builder typeSpec = PropertyTypeSpec.type();
        if(typeDeclaration.type().equals("array")) {
            typeSpec.cardinality(PropertyCardinality.LIST)
                    .typeKind(TypeKind.JAVA_TYPE)
                    .typeRef(RamlType.from(((ArrayTypeDeclaration)typeDeclaration).items()).javaType());
        } else {
            typeSpec.cardinality(PropertyCardinality.SINGLE)
                    .typeKind(TypeKind.JAVA_TYPE)
                    .typeRef(RamlType.from(typeDeclaration).javaType());
        }
        return typeSpec;
    }

    private String alreadyDefined(TypeDeclaration typeDeclaration) {
        if(this.naming.isAlreadyDefined(typeDeclaration)) {
            return this.naming.alreadyDefined(typeDeclaration);
        }

        for (TypeDeclaration parentType : typeDeclaration.parentTypes()) {
            if (this.naming.isAlreadyDefined(parentType)) {
                return this.naming.alreadyDefined(parentType);
            }
        }

        return null;
    }
}
