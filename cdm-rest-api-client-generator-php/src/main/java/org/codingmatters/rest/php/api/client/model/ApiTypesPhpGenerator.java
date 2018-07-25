package org.codingmatters.rest.php.api.client.model;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.api.generator.type.RamlType;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.value.objects.spec.*;
import org.codingmatters.value.objects.values.ObjectValue;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.codingmatters.rest.api.generator.type.RamlType.isRamlType;

public class ApiTypesPhpGenerator {

    private final Naming naming = new Naming();
    private final String typesPackage;
    private static Map<String, String> typeMapping;

    static {
        typeMapping = new HashMap<>();
        typeMapping.put( "integer", "int" );
        typeMapping.put( "string", "string" );
        typeMapping.put( "boolean", "bool" );
        typeMapping.put( "number", "int" ); // TODO: ???
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
            if( ((ArrayTypeDeclaration) declaration).items().type() == null && declaration.type().endsWith( "[]" ) ) {
                type = declaration.name().replace( "[]", "" );
            } else {
                type = ((ArrayTypeDeclaration) declaration).items().type();
            }
            if( type.equals( "object" ) ) {
                if( this.naming.isArbitraryObject( ((ArrayTypeDeclaration) declaration).items() ) ) {
                    return PropertyTypeSpec.type()
                            .cardinality( PropertyCardinality.LIST )
                            .typeKind( TypeKind.JAVA_TYPE )
                            .typeRef( "array" );
                } else if( ((ArrayTypeDeclaration) declaration).items().name().equals( "object" ) ) {
                    return PropertyTypeSpec.type()
                            .cardinality( PropertyCardinality.LIST )
                            .typeKind( TypeKind.EMBEDDED )
                            .embeddedValueSpec( this.nestedType( typeDeclaration, (ObjectTypeDeclaration) ((ArrayTypeDeclaration) declaration).items() ) );
                } else {
                    return PropertyTypeSpec.type()
                            .cardinality( PropertyCardinality.LIST )
                            .typeKind( TypeKind.IN_SPEC_VALUE_OBJECT )
                            .typeRef( ((ArrayTypeDeclaration) declaration).items().name() );
                }
            } else {
                return this.simplePropertyArray( typeDeclaration, ((ArrayTypeDeclaration) declaration) );
            }
        } else if( this.naming.isArbitraryObject( declaration ) ) {
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.SINGLE )
                    .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                    .typeRef( ObjectValue.class.getName() );
        } else if( declaration.type().equals( "object" ) ) {
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.SINGLE )
                    .typeKind( TypeKind.EMBEDDED )
                    .embeddedValueSpec( this.nestedType( typeDeclaration, (ObjectTypeDeclaration) declaration )
                    );
        } else {
            return this.simpleProperty( declaration, RamlType.isArrayType( declaration ) ? PropertyCardinality.LIST : PropertyCardinality.SINGLE );
        }
    }

    private PropertyTypeSpec.Builder simplePropertyArray( TypeDeclaration typeDeclaration, ArrayTypeDeclaration declaration ) {
        String typeRef = typesPackage + "." + naming.type( typeDeclaration.name(), declaration.name(), "list" );
        if( this.isEnum( declaration.items() ) ) {
            String[] values = ((StringTypeDeclaration) declaration.items()).enumValues().toArray( new String[0] );
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.LIST )
                    .typeKind( TypeKind.EMBEDDED )
                    .embeddedValueSpec( AnonymousValueSpec.anonymousValueSpec()
                            .addProperty( PropertySpec.property()
                                    .type( PropertyTypeSpec.type()
                                            .typeKind( TypeKind.ENUM )
                                            .typeRef( typeRef )
                                            .cardinality( PropertyCardinality.SINGLE )
                                            .enumValues( values )
                                    )
                                    .build() )
                            .build() );
        } else if( RamlType.isRamlType( declaration.items() ) ) {
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.LIST )
                    .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                    .typeRef( typeRef )
                    .embeddedValueSpec( AnonymousValueSpec.anonymousValueSpec().addProperty(
                            PropertySpec.property().type( PropertyTypeSpec.type()
                                    .typeRef( typeMapping.get( declaration.items().name().replace( "[]", "" ) ) )
                                    .typeKind( TypeKind.JAVA_TYPE )
                            ).build() ).build() );
        } else {
            return PropertyTypeSpec.type()
                    .cardinality( PropertyCardinality.LIST )
                    .typeKind( TypeKind.IN_SPEC_VALUE_OBJECT )
                    .typeRef( declaration.type() );
        }
    }

    private PropertyTypeSpec.Builder simpleProperty( TypeDeclaration declaration, PropertyCardinality withCardinality ) throws RamlSpecException {
        if( this.isEnum( declaration ) ) {
            List<String> values = ((StringTypeDeclaration) declaration).enumValues();
            return PropertyTypeSpec.type()
                    .cardinality( withCardinality )
                    .typeKind( TypeKind.ENUM )
                    .enumValues( values.toArray( new String[values.size()] ) );
        } else if( isRamlType( declaration ) ) {
            return PropertyTypeSpec.type()
                    .cardinality( withCardinality )
                    .typeKind( TypeKind.JAVA_TYPE )
                    .typeRef( typeMapping.get( declaration.type() ) );
        } else {
            return PropertyTypeSpec.type()
                    .cardinality( withCardinality )
                    .typeKind( TypeKind.IN_SPEC_VALUE_OBJECT )
                    .typeRef( declaration.type() );
        }
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
