package org.codingmatters.rest.php.api.client.model;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.api.generator.type.RamlType;
import org.codingmatters.rest.api.generator.utils.AnnotationProcessor;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.rest.api.generator.utils.Resolver;
import org.codingmatters.value.objects.php.generator.TypeTokenPhp;
import org.codingmatters.value.objects.spec.*;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

public class ApiGeneratorPhp {
    /**
     * Created by nelt on 5/2/17.
     */

    private final String typesPackage;
    private final Naming naming = new Naming();
    private final AnnotationProcessor annotationProcessor = new AnnotationProcessor();
    ApiTypesPhpGenerator apiTypesPhpGenerator;

    public ApiGeneratorPhp( String typesPackage ) {
        this.typesPackage = typesPackage;
        this.apiTypesPhpGenerator = new ApiTypesPhpGenerator( typesPackage );
    }

    public Spec generate( RamlModelResult ramlModel ) throws RamlSpecException {
        Spec.Builder result = Spec.spec();
        for( Resource resource : ramlModel.getApiV10().resources() ) {
            this.generateResourceValues( result, resource );
        }

        return result.build();
    }

    private void generateResourceValues( Spec.Builder result, Resource resource ) throws RamlSpecException {
        for( Method method : resource.methods() ) {
            result.addValue( this.generateMethodRequestValue( resource, method ) );
            result.addValue( this.generateMethodResponseValue( resource, method ) );
        }
        for( Resource subResource : resource.resources() ) {
            this.generateResourceValues( result, subResource );
        }
    }

    private ValueSpec generateMethodRequestValue( Resource resource, Method method ) throws RamlSpecException {
        String resourceName = this.naming.type( resource.displayName().value(), method.method(), "Request" );
        ValueSpec.Builder result = ValueSpec.valueSpec()
                .name( resourceName );

        this.annotationProcessor.appendConformsToAnnotations( result, method.annotations() );
        for( Resource res = method.resource(); res != null; res = res.parentResource() ) {
            this.annotationProcessor.appendConformsToAnnotations( result, method.resource().annotations() );
        }

        for( TypeDeclaration typeDeclaration : method.queryParameters() ) {
            result.addProperty( this.getPropertyFromTypeDeclaration( typeDeclaration, resourceName ) );
        }
        for( TypeDeclaration typeDeclaration : method.headers() ) {
            result.addProperty( this.getPropertyFromTypeDeclaration( typeDeclaration, resourceName ) );
        }
        if( method.body() != null && !method.body().isEmpty() ) {
            PropertyTypeSpec.Builder type = this.payloadType( method.body().get( 0 ), resourceName );
            result.addProperty( PropertySpec.property()
                    .name( "payload" )
                    .type( type ) );
            if( type.build().typeRef().equals( "string" ) ) {
                result.addProperty( PropertySpec.property()
                        .name( "contentType" )
                        .type( PropertyTypeSpec.type().typeRef( "string" ).typeKind( TypeKind.JAVA_TYPE ).cardinality( PropertyCardinality.SINGLE ) )
                        .build() );
            }
        }
        for( TypeDeclaration typeDeclaration : Resolver.resolvedUriParameters( resource ) ) {
            result.addProperty( this.getPropertyFromTypeDeclaration( typeDeclaration, resourceName ) );
        }

        return result.build();
    }

    public PropertyTypeSpec.Builder payloadType( TypeDeclaration typeDeclaration, String resourceName ) throws RamlSpecException {
        if( RamlType.isRamlType( typeDeclaration ) ) {
            return this.typeSpecFromDeclaration( typeDeclaration );
        } else {
            if( typeDeclaration instanceof ArrayTypeDeclaration ) {
                if( this.isAlreadyDefined( ((ArrayTypeDeclaration) typeDeclaration).items() ) ) {
                    return PropertyTypeSpec.type()
                            .cardinality( PropertyCardinality.LIST )
                            .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                            .typeRef( this.alreadyDefined( ((ArrayTypeDeclaration) typeDeclaration).items() ) );
                } else if( this.naming.isArbitraryObjectArray( typeDeclaration ) ) {
                    String typeRef = typesPackage + "." + resourceName.toLowerCase() + "." + naming.type( resourceName, "array", "list" );
                    return PropertyTypeSpec.type()
                            .cardinality( PropertyCardinality.LIST )
                            .typeKind( TypeKind.EMBEDDED )
                            .typeRef( typeRef )
                            .embeddedValueSpec( AnonymousValueSpec.anonymousValueSpec().addProperty( PropertySpec.property().type( PropertyTypeSpec.type()
                                    .typeKind( TypeKind.JAVA_TYPE )
                                    .typeRef( "array" )
                            ).build() ).build() );
                } else {
                    String listTypeRef;
                    TypeKind typeKind;

                    if( ((ArrayTypeDeclaration) typeDeclaration).items().name().equals( "file" ) ) {
                        listTypeRef = "string";
                        typeKind = TypeKind.JAVA_TYPE;
                    } else {
                        String typeName = ((ArrayTypeDeclaration) typeDeclaration).items().type().equals( "object" ) ? ((ArrayTypeDeclaration) typeDeclaration).items().name() : ((ArrayTypeDeclaration) typeDeclaration).items().type();
                        listTypeRef = this.typesPackage + "." + typeName;
                        typeKind = TypeKind.EXTERNAL_VALUE_OBJECT;
                    }
                    String typeRef = typesPackage + "." + resourceName.toLowerCase() + "." + naming.type( resourceName, listTypeRef.substring( listTypeRef.lastIndexOf( "." ) + 1 ), "list" );
                    return PropertyTypeSpec.type()
                            .cardinality( PropertyCardinality.LIST )
                            .typeKind( TypeKind.EMBEDDED )
                            .typeRef( typeRef )
                            .embeddedValueSpec( AnonymousValueSpec.anonymousValueSpec()
                                    .addProperty( PropertySpec.property().type( PropertyTypeSpec.type()
                                            .typeRef( listTypeRef )
                                            .typeKind( typeKind )
                                    ).build() )
                                    .build() );
                }
            } else {
                if( this.isAlreadyDefined( typeDeclaration ) ) {
                    return PropertyTypeSpec.type()
                            .cardinality( PropertyCardinality.SINGLE )
                            .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                            .typeRef( this.alreadyDefined( typeDeclaration ) );
                } else if( this.naming.isArbitraryObject( typeDeclaration ) ) {
                    return PropertyTypeSpec.type()
                            .cardinality( PropertyCardinality.SINGLE )
                            .typeKind( TypeKind.JAVA_TYPE )
                            .typeRef( "array" );
                } else {
                    String typeRef;
                    TypeKind typeKind;
                    if( typeDeclaration.type().equals( "file" ) ) {
                        typeRef = "string";
                        typeKind = TypeKind.JAVA_TYPE;
                    } else {
                        typeKind = TypeKind.EXTERNAL_VALUE_OBJECT;
                        typeRef = this.typesPackage + "." + typeDeclaration.type();
                    }
                    return PropertyTypeSpec.type()
                            .cardinality( PropertyCardinality.SINGLE )
                            .typeKind( typeKind )
                            .typeRef( typeRef );
                }
            }
        }
    }

    private String alreadyDefined( TypeDeclaration typeDeclaration ) {
        if( this.naming.isAlreadyDefined( typeDeclaration ) ) {
            return this.naming.alreadyDefined( typeDeclaration );
        }

        for( TypeDeclaration parentType : typeDeclaration.parentTypes() ) {
            if( this.naming.isAlreadyDefined( parentType ) ) {
                return this.naming.alreadyDefined( parentType );
            }
        }

        return null;
    }

    private boolean isAlreadyDefined( TypeDeclaration typeDeclaration ) {
        if( this.naming.isAlreadyDefined( typeDeclaration ) ) { return true; }
        for( TypeDeclaration parentType : typeDeclaration.parentTypes() ) {
            if( this.naming.isAlreadyDefined( parentType ) ) {
                return true;
            }
        }
        return false;
    }

    private ValueSpec generateMethodResponseValue( Resource resource, Method method ) throws RamlSpecException {
        String resourceName = this.naming.type( resource.displayName().value(), method.method(), "Response" );
        ValueSpec.Builder result = ValueSpec.valueSpec()
                .name( resourceName );

        for( Response response : method.responses() ) {
            AnonymousValueSpec.Builder responseSpec = AnonymousValueSpec.anonymousValueSpec();
            for( TypeDeclaration typeDeclaration : response.headers() ) {
                responseSpec.addProperty( this.getPropertyFromTypeDeclaration( typeDeclaration, resourceName ) );
            }
            if( response.body() != null && !response.body().isEmpty() ) {
                PropertyTypeSpec.Builder type = this.payloadType( response.body().get( 0 ), resourceName );
                responseSpec.addProperty( PropertySpec.property()
                        .name( "payload" )
                        .type( type )
                );
                if( type.build().typeRef().equals( "string" ) ) {
                    responseSpec.addProperty( PropertySpec.property()
                            .name( "contentType" )
                            .type( PropertyTypeSpec.type().typeRef( "string" ).typeKind( TypeKind.JAVA_TYPE ).cardinality( PropertyCardinality.SINGLE ) )
                            .build() );
                }
            }
            PropertySpec.Builder responseProp = PropertySpec.property()
                    .name( this.naming.property( "status", response.code().value() ) )
                    .type( PropertyTypeSpec.type()
                            .typeKind( TypeKind.EMBEDDED )
                            .cardinality( PropertyCardinality.SINGLE )
                            .embeddedValueSpec( responseSpec )
                    );

            result.addProperty( responseProp );
        }


        return result.build();
    }

    private PropertySpec getPropertyFromTypeDeclaration( TypeDeclaration typeDeclaration, String resourceName ) {
        if( typeDeclaration instanceof ArrayTypeDeclaration ) {
            String typeRef = typesPackage + "." + typeDeclaration.name().toLowerCase() + "." + naming.type( typeDeclaration.name(), resourceName, "list" );
            ArrayTypeDeclaration arrayTypeDeclaration = (ArrayTypeDeclaration) typeDeclaration;
            return PropertySpec.property()
                    .name( this.naming.property( typeDeclaration.name() ) )
                    .type( PropertyTypeSpec.type()
                            .cardinality( PropertyCardinality.LIST )
                            .typeRef( typeRef )
                            .typeKind( TypeKind.EMBEDDED )
                            .embeddedValueSpec(
                                    AnonymousValueSpec.anonymousValueSpec()
                                            .addProperty(
                                                    PropertySpec.property().type(
                                                            PropertyTypeSpec.type()
                                                                    .typeRef( ApiTypesPhpGenerator.typeMapping.get( arrayTypeDeclaration.items().type() ) )
                                                                    .typeKind( TypeKind.JAVA_TYPE ) ) ) )
                    ).build();
        } else {
            return PropertySpec.property()
                    .name( this.naming.property( typeDeclaration.name() ) )
                    .type( PropertyTypeSpec.type()
                            .cardinality( PropertyCardinality.SINGLE )
                            .typeRef( ApiTypesPhpGenerator.typeMapping.get( typeDeclaration.type() ) )
                            .typeKind( TypeKind.JAVA_TYPE )
                    ).build();
        }
    }

    private PropertyTypeSpec.Builder typeSpecFromDeclaration( TypeDeclaration typeDeclaration ) throws RamlSpecException {
        PropertyTypeSpec.Builder typeSpec = PropertyTypeSpec.type();
        if( typeDeclaration.type().equals( "array" ) ) {
            typeSpec.cardinality( PropertyCardinality.LIST )
                    .typeKind( TypeKind.JAVA_TYPE )
                    .typeRef( RamlType.from( ((ArrayTypeDeclaration) typeDeclaration).items() ).javaType() );
        } else {
            typeSpec.cardinality( PropertyCardinality.SINGLE )
                    .typeKind( TypeKind.JAVA_TYPE )
                    .typeRef( TypeTokenPhp.parse( typeDeclaration.type() ).getTypeName() );
        }
        return typeSpec;
    }
}

