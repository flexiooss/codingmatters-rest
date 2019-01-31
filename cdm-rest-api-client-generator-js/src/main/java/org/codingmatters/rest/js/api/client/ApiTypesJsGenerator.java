package org.codingmatters.rest.js.api.client;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.api.generator.type.RamlType;
import org.codingmatters.value.objects.generation.Naming;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.codingmatters.rest.api.generator.type.RamlType.isRamlType;

public class ApiTypesJsGenerator {

    private final PackagesConfiguration packagesConfiguration;
    private final Naming naming = new Naming();
    public static Map<String, String> typeMapping;

    static {
        typeMapping = new HashMap<>();
        typeMapping.put( "integer", "int" );
        typeMapping.put( "string", "string" );
        typeMapping.put( "boolean", "bool" );
        typeMapping.put( "number", "int" );
        typeMapping.put( "datetime", "tz-date-time" );
        typeMapping.put( "datetime-only", "date-time" );
        typeMapping.put( "time-only", "time" );
        typeMapping.put( "date-only", "date" );
    }

    public ApiTypesJsGenerator( PackagesConfiguration packagesConfiguration ) {
        this.packagesConfiguration = packagesConfiguration;
    }

    public List<ParsedValueObject> parseRamlTypes( RamlModelResult model ) throws RamlSpecException {
        List<ParsedValueObject> valueObjects = new ArrayList<>();
        for( TypeDeclaration typeDeclaration : model.getApiV10().types() ){
            if( typeDeclaration.type().equals( "object" ) ){
                if( !this.naming.isAlreadyDefined( typeDeclaration ) ){
                    ParsedValueObject valueObject = new ParsedValueObject( typeDeclaration.name() );
                    for( TypeDeclaration property : ((ObjectTypeDeclaration) typeDeclaration).properties() ){
                        ValueObjectType type = parseType( typeDeclaration.name(), property );
                        valueObject.properties().add( new ValueObjectProperty( property.name(), type ) );
                    }
                    valueObjects.add( valueObject );
                }
            }
        }
        return valueObjects;
    }

    public ValueObjectType parseType( String typeDeclarationName, TypeDeclaration declaration ) throws RamlSpecException {
        if( declaration instanceof ArrayTypeDeclaration ){
            String type;
            ArrayTypeDeclaration arrayTypeDeclaration = (ArrayTypeDeclaration) declaration;
            String alreadyDefinedType;
            if( "array".equals( declaration.type() ) ){ /* case type: array items: XXX */
                type = arrayTypeDeclaration.items().type().replace( "[]", "" );
                alreadyDefinedType = isAlreadyDefined( arrayTypeDeclaration.items() );
                if( alreadyDefinedType == null ){
                    alreadyDefinedType = isAlreadyDefined( declaration );
                }
            } else { /* case XXX[] */
                type = declaration.type().replace( "[]", "" );
                alreadyDefinedType = isAlreadyDefined( arrayTypeDeclaration.items() );
            }
            if( type.equals( "object" ) && (((ObjectTypeDeclaration) ((ArrayTypeDeclaration) declaration).items()).properties().isEmpty()) && alreadyDefinedType == null ){ /** IS OBJECT VALUE */
                String typeRef = packagesConfiguration.typesPackage() + "." + typeDeclarationName;
                String name = naming.type( typeDeclarationName, type, "list" );
                return new ValueObjectTypeList(
                        name,
                        new ValueObjectTypePrimitiveType( "object" ),
                        typeRef
                );
            } else {
                return this.simplePropertyArray( typeDeclarationName, ((ArrayTypeDeclaration) declaration) );
            }
        } else if( this.naming.isArbitraryObject( declaration ) ){
            return new ValueObjectTypePrimitiveType( "object" );
        } else if( declaration.type().equals( "object" ) && isAlreadyDefined( declaration ) == null ){
            return new ObjectTypeNested( this.nestedType( typeDeclarationName, (ObjectTypeDeclaration) declaration ), packagesConfiguration.typesPackage() );
        } else {
            return this.simpleProperty( typeDeclarationName, declaration );
        }
    }

    private ValueObjectType simplePropertyArray( String typeDeclarationName, ArrayTypeDeclaration declaration ) {
        String namespace = packagesConfiguration.typesPackage() + "." + typeDeclarationName;
        String name = naming.type( typeDeclarationName, declaration.items().name(), "list" ); // TODO application here
        if( this.isEnum( declaration.items() ) ){
            String[] values = ((StringTypeDeclaration) declaration.items()).enumValues().toArray( new String[0] );
            return new ValueObjectTypeList(
                    name,
                    new YamlEnumInSpecEnum( naming.type( typeDeclarationName, declaration.name() ), namespace, values ),
                    namespace );
            /** RAML PRIMITIVE TYPE */
        } else if( RamlType.isRamlType( declaration.items() ) ){
            String type;
            if( "array".equals( declaration.type() ) ){
                type = declaration.items().type().replace( "[]", "" );
            } else {
                type = declaration.type().replace( "[]", "" );
            }
            return new ValueObjectTypeList(
                    name,
                    new ValueObjectTypePrimitiveType( typeMapping.get( type ) ),
                    namespace
            );
        } else {
            String type;
            String alreadyDefinedType;
            if( "array".equals( declaration.type() ) ){ /* case type: array items: XXX */
                type = declaration.items().type().replace( "[]", "" );
                alreadyDefinedType = isAlreadyDefined( declaration.items() );
                if( alreadyDefinedType == null ){
                    alreadyDefinedType = isAlreadyDefined( declaration );
                }
            } else { /* case XXX[] */
                type = declaration.type().replace( "[]", "" );
                alreadyDefinedType = isAlreadyDefined( declaration.items() );
            }
            if( alreadyDefinedType != null ){
                return new ValueObjectTypeList(
                        name,
                        new ObjectTypeExternalValue( alreadyDefinedType ),
                        namespace
                );
            }
            return new ValueObjectTypeList(
                    name,
                    new ObjectTypeInSpecValueObject( type ),
                    namespace
            );
        }
    }

    private ValueObjectType simpleProperty( String typeDeclarationName, TypeDeclaration declaration ) {
        if( this.isEnum( declaration ) ){
            String[] values = ((StringTypeDeclaration) declaration).enumValues().toArray( new String[0] );
            String name = naming.type( typeDeclarationName, declaration.name() );
            String namespace = packagesConfiguration.typesPackage() + "." + typeDeclarationName.toLowerCase();
            return new YamlEnumInSpecEnum(
                    name,
                    namespace,
                    values
            );
        } else if( isRamlType( declaration ) ){
            return new ValueObjectTypePrimitiveType( typeMapping.get( declaration.type() ) );
        } else {
            String type = isAlreadyDefined( declaration );
            if( type != null ){
                return new ObjectTypeExternalValue( type );
            }
            return new ObjectTypeInSpecValueObject( declaration.type() );
        }
    }

    private String isAlreadyDefined( TypeDeclaration declaration ) {
        for( AnnotationRef annotationRef : declaration.annotations() ){
            if( "already-defined".equals( annotationRef.annotation().name() ) ){
                return annotationRef.structuredValue().value().toString();
            }
        }
        for( TypeDeclaration typeDeclaration : declaration.parentTypes() ){
            String type = isAlreadyDefined( typeDeclaration );
            if( type != null ){
                return type;
            }
        }
        return null;
    }

    private boolean isEnum( TypeDeclaration declaration ) {
        return declaration instanceof StringTypeDeclaration && !((StringTypeDeclaration) declaration).enumValues().isEmpty();
    }

    private ParsedValueObject nestedType( String typeDeclarationName, ObjectTypeDeclaration declaration ) throws RamlSpecException {
        ParsedValueObject valueObject = new ParsedValueObject( NamingUtility.className( declaration.name() ) );
        for( TypeDeclaration property : declaration.properties() ){
            ValueObjectType type = parseType( typeDeclarationName, property );
            valueObject.properties().add( new ValueObjectProperty( property.name(), type ) );
        }
        return valueObject;
        /*
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
                        .type( this.parseType( typeDeclaration, objectProp ) );
            }
            embedded.addProperty( prop );
        }
        return embedded;
        */
    }

}
