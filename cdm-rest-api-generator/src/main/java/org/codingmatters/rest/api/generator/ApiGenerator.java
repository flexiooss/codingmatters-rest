package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.api.generator.type.RamlType;
import org.codingmatters.rest.api.generator.utils.AnnotationProcessor;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.rest.api.generator.utils.Resolver;
import org.codingmatters.rest.api.types.File;
import org.codingmatters.value.objects.spec.*;
import org.codingmatters.value.objects.values.ObjectValue;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by nelt on 5/2/17.
 */
public class ApiGenerator {

    private final String typesPackage;
    private final Naming naming = new Naming();
    private final AnnotationProcessor annotationProcessor = new AnnotationProcessor();

    public ApiGenerator(String typesPackage) {
        this.typesPackage = typesPackage;
    }

    public Spec generate(RamlModelResult ramlModel) throws RamlSpecException {
        Spec.Builder result = Spec.spec();
        for (Resource resource : ramlModel.getApiV10().resources()) {
            this.generateResourceValues(result, resource);
        }

        return result.build();
    }

    private void generateResourceValues(Spec.Builder result, Resource resource) throws RamlSpecException {
        for (Method method : resource.methods()) {
            result.addValue(this.generateMethodRequestValue(resource, method));
            result.addValue(this.generateMethodResponseValue(resource, method));
        }
        for (Resource subResource : resource.resources()) {
            this.generateResourceValues(result, subResource);
        }
    }

    private ValueSpec generateMethodRequestValue(Resource resource, Method method) throws RamlSpecException {
        ValueSpec.Builder result = ValueSpec.valueSpec()
                .name(this.naming.type(resource.displayName().value(), method.method(), "Request"));

        this.annotationProcessor.appendConformsToAnnotations(result, method.annotations());
        for(Resource res = method.resource() ; res != null ; res = res.parentResource()) {
            this.annotationProcessor.appendConformsToAnnotations(result, method.resource().annotations());
        }

        for (TypeDeclaration typeDeclaration : method.queryParameters()) {
            this.addPropertyFromTypeDeclaration(result, typeDeclaration);
        }
        for (TypeDeclaration typeDeclaration : method.headers()) {
            this.addPropertyFromTypeDeclaration(result, typeDeclaration);
        }
        if(method.body() != null && ! method.body().isEmpty()) {
            result.addProperty(PropertySpec.property()
                    .name("payload")
                    .type(this.payloadType(method.body().get(0))
                    ));
        }
        for (TypeDeclaration typeDeclaration : Resolver.resolvedUriParameters(resource)) {
            this.addPropertyFromTypeDeclaration(result, typeDeclaration);
        }


        return result.build();
    }

    private PropertyTypeSpec.Builder payloadType(TypeDeclaration typeDeclaration) throws RamlSpecException {
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
                            .typeRef(ObjectValue.class.getName());
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
                            .typeRef(ObjectValue.class.getName());
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

    private boolean isAlreadyDefined(TypeDeclaration typeDeclaration) {
        if(this.naming.isAlreadyDefined(typeDeclaration)) return true;
        for (TypeDeclaration parentType : typeDeclaration.parentTypes()) {
            if(this.naming.isAlreadyDefined(parentType)) {
                return true;
            }
        }
        return false;
    }

    private ValueSpec generateMethodResponseValue(Resource resource, Method method) throws RamlSpecException {
        ValueSpec.Builder result = ValueSpec.valueSpec()
                .name(this.naming.type(resource.displayName().value(), method.method(), "Response"));

        for (Response response : method.responses()) {
            AnonymousValueSpec.Builder responseSpec = AnonymousValueSpec.anonymousValueSpec();
            for (TypeDeclaration typeDeclaration : response.headers()) {
                responseSpec.addProperty(PropertySpec.property()
                        .name(this.naming.property(typeDeclaration.name()))
                        .type(this.typeSpecFromDeclaration(typeDeclaration))
                        .build());
            }
            if(response.body() != null && ! response.body().isEmpty()) {
                responseSpec.addProperty(PropertySpec.property()
                        .name("payload")
                        .type(this.payloadType(response.body().get(0)))
                );
            }
            PropertySpec.Builder responseProp = PropertySpec.property()
                    .name(this.naming.property("status", response.code().value()))
                    .type(PropertyTypeSpec.type()
                            .typeKind(TypeKind.EMBEDDED)
                            .cardinality(PropertyCardinality.SINGLE)
                            .embeddedValueSpec(responseSpec)
                    )
                    ;

            result.addProperty(responseProp);
        }


        return result.build();
    }

    private void addPropertyFromTypeDeclaration(ValueSpec.Builder result, TypeDeclaration typeDeclaration) throws RamlSpecException {
        result.addProperty(PropertySpec.property()
                .name(this.naming.property(typeDeclaration.name()))
                .type(this.typeSpecFromDeclaration(typeDeclaration))
                .build());
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
}
