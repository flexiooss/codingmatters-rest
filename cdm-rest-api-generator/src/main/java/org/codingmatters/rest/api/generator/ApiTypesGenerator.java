package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.api.generator.type.RamlType;
import org.codingmatters.rest.api.generator.utils.AnnotationProcessor;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.value.objects.spec.*;
import org.codingmatters.value.objects.values.ObjectValue;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nelt on 5/2/17.
 */
public class ApiTypesGenerator {

    private final Naming naming = new Naming();
    private final AnnotationProcessor annotationProcessor = new AnnotationProcessor();

    public Spec generate(RamlModelResult ramlModel) throws RamlSpecException {
        Spec.Builder result = Spec.spec();
        for (TypeDeclaration typeDeclaration : ramlModel.getApiV10().types()) {
            if(typeDeclaration.type().equals("object")) {
                if(!this.naming.isAlreadyDefined(typeDeclaration)) {
                    ValueSpec.Builder valueSpec = ValueSpec.valueSpec().name(this.naming.type(typeDeclaration.name()));
                    this.annotationProcessor.appendConformsToAnnotations(valueSpec, typeDeclaration.annotations());

                    for (TypeDeclaration declaration : ((ObjectTypeDeclaration) typeDeclaration).properties()) {
                        PropertySpec.Builder prop = PropertySpec.property()
                                .name(this.naming.property(declaration.name()))
                                .hints(this.rawNameHint(declaration))
                                .type(this.typeSpecFromDeclaration(declaration));
                        this.annotationProcessor.appendValueObjectHints(prop, declaration.annotations());
                        valueSpec.addProperty(prop);
                    }

                    result.addValue(valueSpec);
                }
            }
        }
        return result.build();
    }

    private Set<String> rawNameHint(TypeDeclaration declaration) {
        return new HashSet<>(Arrays.asList(String.format("property:raw(%s)", declaration.name())));
    }

    private PropertyTypeSpec.Builder typeSpecFromDeclaration(TypeDeclaration declaration) throws RamlSpecException {
        if(declaration instanceof ArrayTypeDeclaration) {
            if(((ArrayTypeDeclaration)declaration).items().type().equals("object")) {
                if(this.naming.isArbitraryObject(((ArrayTypeDeclaration) declaration).items())) {
                    return PropertyTypeSpec.type()
                            .cardinality(PropertyCardinality.LIST)
                            .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                            .typeRef(ObjectValue.class.getName());
                } else if(((ArrayTypeDeclaration)declaration).items().name().equals("object")) {
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
        } else if(this.naming.isArbitraryObject(declaration)) {
            return PropertyTypeSpec.type()
                    .cardinality(PropertyCardinality.SINGLE)
                    .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                    .typeRef(ObjectValue.class.getName());
        } else if(declaration.type().equals("object")) {
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
            PropertySpec.Builder prop;
            if(objectProp.type().equals("object")) {
                prop = PropertySpec.property()
                        .name(this.naming.property(objectProp.name()))
                        .hints(this.rawNameHint(objectProp))
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.SINGLE)
                                .typeKind(TypeKind.EMBEDDED)
                                .embeddedValueSpec(this.nestedType((ObjectTypeDeclaration) objectProp))
                        );
            } else {
                prop = PropertySpec.property()
                        .name(this.naming.property(objectProp.name()))
                        .hints(this.rawNameHint(objectProp))
                        .type(this.typeSpecFromDeclaration(objectProp)
                        );
            }
            this.annotationProcessor.appendValueObjectHints(prop, objectProp.annotations());
            embedded.addProperty(prop);
        }
        return embedded;
    }
}
