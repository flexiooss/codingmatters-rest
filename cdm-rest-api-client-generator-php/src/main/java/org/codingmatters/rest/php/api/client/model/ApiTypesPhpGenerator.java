package org.codingmatters.rest.php.api.client.model;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.api.generator.type.RamlType;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.value.objects.spec.*;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import java.util.HashMap;
import java.util.Map;

import static org.codingmatters.rest.api.generator.type.RamlType.isRamlType;

public class ApiTypesPhpGenerator {

    private final Naming naming = new Naming();
    private final String typesPackage;
    public static Map<String, String> typeMapping;

    static {
        typeMapping = new HashMap<>();
        typeMapping.put( "integer", "int" );
        typeMapping.put( "string", "string" );
        typeMapping.put( "boolean", "bool" );
        typeMapping.put( "number", "int" );
        typeMapping.put( "datetime", "datetime" );
        typeMapping.put( "datetime-only", "datetime" );
        typeMapping.put( "time-only", "time" );
        typeMapping.put( "date-only", "date" );
    }

    public ApiTypesPhpGenerator( String typesPackage ) {
        this.typesPackage = typesPackage;
    }

    public Spec generate( RamlModelResult model ) throws RamlSpecException {
        Spec.Builder spec = Spec.spec();
        for( TypeDeclaration typeDeclaration : model.getApiV10().types() ) {
            if( typeDeclaration.type().equals( "object" ) ) {
                if( !this.naming.isAlreadyDefined( typeDeclaration ) ) {
                    ValueSpec.Builder valueSpec = ValueSpec.valueSpec().name( this.naming.type( typeDeclaration.name() ) );
                    for( TypeDeclaration declaration : ((ObjectTypeDeclaration) typeDeclaration).properties() ) {
                        String propertyName = this.naming.property( declaration.name() );
                        PropertySpec.Builder prop = PropertySpec.property()
                                .name( propertyName )
                                .type( this.typeSpecFromDeclaration( typeDeclaration, declaration ) );
                        // TODO hints ? conforms to ?
                        valueSpec.addProperty( prop );
                    }
                    spec.addValue( valueSpec );
                }
            }
        }
        return spec.build();
    }

    private PropertyTypeSpec.Builder typeSpecFromDeclaration( TypeDeclaration typeDeclaration, TypeDeclaration declaration ) throws RamlSpecException {
        if( declaration instanceof ArrayTypeDeclaration ) {
            String type;
            if( declaration.type().endsWith( "[]" ) ) {
                type = declaration.type().replace( "[]", "" );
            } else {
                type = ((ArrayTypeDeclaration) declaration).items().type();
            }
            if( type.equals( "object" ) && (((ObjectTypeDeclaration) ((ArrayTypeDeclaration) declaration).items()).properties().isEmpty()) ) { /** IS OBJECT VALUE */
                String typeRef = typesPackage + "." + typeDeclaration.name().toLowerCase() + "." + naming.type( typeDeclaration.name(), declaration.name(), "list" );
                return PropertyTypeSpec.type()
                        .cardinality( PropertyCardinality.LIST )
                        .typeKind( TypeKind.EMBEDDED )
                        .typeRef( typeRef )
                        .embeddedValueSpec( AnonymousValueSpec.anonymousValueSpec().addProperty(
                                PropertySpec.property()
                                        .type( PropertyTypeSpec.type().typeKind( TypeKind.JAVA_TYPE ).typeRef( "array" ) )
                                        .build()
                        ).build() );
            } else {
                return this.simplePropertyArray( typeDeclaration, ((ArrayTypeDeclaration) declaration) );
            }
        } else if( this.naming.isArbitraryObject( declaration ) ) {
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.SINGLE )
                    .typeKind( TypeKind.JAVA_TYPE )
                    .typeRef( "array" );
        } else if( declaration.type().equals( "object" ) ) {
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.SINGLE )
                    .typeKind( TypeKind.EMBEDDED )
                    .embeddedValueSpec( this.nestedType( typeDeclaration, (ObjectTypeDeclaration) declaration ) );
        } else {
            return this.simpleProperty( typeDeclaration, declaration );
        }
    }

    private PropertyTypeSpec.Builder simplePropertyArray( TypeDeclaration typeDeclaration, ArrayTypeDeclaration declaration ) {
        String typeRef = typesPackage + "." + typeDeclaration.name().toLowerCase() + "." + naming.type( typeDeclaration.name(), declaration.name(), "list" );
        if( this.isEnum( declaration.items() ) ) {
            String[] values = ((StringTypeDeclaration) declaration.items()).enumValues().toArray( new String[0] );
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.LIST )
                    .typeKind( TypeKind.EMBEDDED )
                    .typeRef( typeRef )
                    .embeddedValueSpec( AnonymousValueSpec.anonymousValueSpec()
                            .addProperty( PropertySpec.property()
                                    .type( PropertyTypeSpec.type()
                                            .typeKind( TypeKind.ENUM )
                                            .typeRef( typesPackage + "." + typeDeclaration.name().toLowerCase() + "." + naming.type( typeDeclaration.name(), declaration.name() ) )
                                            .cardinality( PropertyCardinality.SINGLE )
                                            .enumValues( values )
                                    )
                                    .build() )
                            .build() );
            /** RAML PRIMITIVE TYPE */
        } else if( RamlType.isRamlType( declaration.items() ) ) {
            String type;
            if( "array".equals( declaration.type() ) ) {
                type = declaration.items().type().replace( "[]", "" );
            } else {
                type = declaration.type().replace( "[]", "" );
            }
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.LIST )
                    .typeKind( TypeKind.EMBEDDED )
                    .typeRef( typeRef )
                    .embeddedValueSpec( AnonymousValueSpec.anonymousValueSpec().addProperty(
                            PropertySpec.property().type( PropertyTypeSpec.type()
                                    .typeRef( typeMapping.get( type ) )
                                    .typeKind( TypeKind.JAVA_TYPE )
                            ).build() ).build() );
        } else {
            String type;
            String alreadyDefinedType;
            if( "array".equals( declaration.type() ) ) { /* case type: array items: XXX */
                type = declaration.items().type().replace( "[]", "" );
                alreadyDefinedType = isAlreadyDefined( declaration.items() );
            } else { /* case XXX[] */
                type = declaration.type().replace( "[]", "" );
                alreadyDefinedType = isAlreadyDefined( declaration.items() );
            }
            if( alreadyDefinedType != null ) {
                return PropertyTypeSpec.type()
                        .cardinality( PropertyCardinality.LIST )
                        .typeKind( TypeKind.EMBEDDED )
                        .typeRef( typeRef )
                        .embeddedValueSpec( AnonymousValueSpec.anonymousValueSpec().addProperty(
                                PropertySpec.property().type(
                                        PropertyTypeSpec.type()
                                                .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                                                .typeRef( alreadyDefinedType )
                                ).build() ).build() );
            }
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.LIST )
                    .typeKind( TypeKind.EMBEDDED )
                    .typeRef( typeRef )
                    .embeddedValueSpec( AnonymousValueSpec.anonymousValueSpec().addProperty(
                            PropertySpec.property().type(
                                    PropertyTypeSpec.type()
                                            .typeKind( TypeKind.IN_SPEC_VALUE_OBJECT )
                                            .typeRef( type )
                            ).build() ).build() );
        }
    }

    private PropertyTypeSpec.Builder simpleProperty( TypeDeclaration typeDeclaration, TypeDeclaration declaration ) throws RamlSpecException {
        if( this.isEnum( declaration ) ) {
            String[] values = ((StringTypeDeclaration) declaration).enumValues().toArray( new String[0] );
            String type = naming.type( typeDeclaration.name(), declaration.name() );
            String typeRef = typesPackage + "." + typeDeclaration.name().toLowerCase() + "." + type;
            return PropertyTypeSpec.type()
                    .typeRef( typeRef )
                    .cardinality( PropertyCardinality.SINGLE )
                    .typeKind( TypeKind.ENUM )
                    .enumValues( values );
        } else if( isRamlType( declaration ) ) {
            // if( date )
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.SINGLE )
                    .typeKind( TypeKind.JAVA_TYPE )
                    .typeRef( typeMapping.get( declaration.type() ) );
        } else {
            String type = isAlreadyDefined( declaration );
            if( type != null ) {
                return PropertyTypeSpec.type()
                        .cardinality( PropertyCardinality.SINGLE )
                        .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                        .typeRef( type );
            }
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.SINGLE )
                    .typeKind( TypeKind.IN_SPEC_VALUE_OBJECT )
                    .typeRef( declaration.type() );
        }
    }

    private String isAlreadyDefined( TypeDeclaration declaration ) {
        for( AnnotationRef annotationRef : declaration.annotations() ) {
            if( "already-defined".equals( annotationRef.annotation().name() ) ) {
                return annotationRef.structuredValue().value().toString();
            }
        }
        for( TypeDeclaration typeDeclaration : declaration.parentTypes() ) {
            String type = isAlreadyDefined( typeDeclaration );
            if( type != null ) {
                return type;
            }
        }
        return null;
    }

    private AnonymousValueSpec.Builder nestedType( TypeDeclaration typeDeclaration, ObjectTypeDeclaration declaration ) throws RamlSpecException {
        AnonymousValueSpec.Builder embedded = AnonymousValueSpec.anonymousValueSpec();
        for( TypeDeclaration objectProp : declaration.properties() ) {
            PropertySpec.Builder prop;
            if( objectProp.type().equals( "object" ) ) {
                prop = PropertySpec.property()
                        .name( this.naming.property( objectProp.name() ) )
                        .type( PropertyTypeSpec.type()
                                .cardinality( PropertyCardinality.SINGLE )
                                .typeKind( TypeKind.EMBEDDED )
                                .embeddedValueSpec( this.nestedType( typeDeclaration, (ObjectTypeDeclaration) objectProp ) ) );
            } else {
                prop = PropertySpec.property()
                        .name( this.naming.property( objectProp.name() ) )
                        .type( this.typeSpecFromDeclaration( typeDeclaration, objectProp ) );
            }
            embedded.addProperty( prop );
        }
        return embedded;
    }

    private boolean isEnum( TypeDeclaration declaration ) {
        return declaration instanceof StringTypeDeclaration && !((StringTypeDeclaration) declaration).enumValues().isEmpty();
    }


}
