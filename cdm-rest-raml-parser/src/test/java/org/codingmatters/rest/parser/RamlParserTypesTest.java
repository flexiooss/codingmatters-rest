package org.codingmatters.rest.parser;

import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RamlParserTypesTest {

    private String typesPackage = "org.generated.types";
    private String apiPackage = "org.generated.api";

    @Test
    public void testBasicProperties() throws Exception {
        ParsedRaml raml = new RamlParser( typesPackage, apiPackage ).parseFile( getRaml( "01_primitiveProperties.raml" ) );
        assertTrue( raml.routes().isEmpty() );
        assertThat( raml.types().size(), is( 1 ) );
        assertThat( raml.types().get( 0 ).name(), is( "SimplePropertyType" ) );
        assertThat( raml.types().get( 0 ).properties().size(), is( 4 ) );

        ValueObjectProperty property;
        property = raml.types().get( 0 ).properties().get( 0 );
        assertThat( property.name(), is( "simpleString" ) );
        assertThat( ((ValueObjectTypePrimitiveType) property.type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );

        property = raml.types().get( 0 ).properties().get( 1 );
        assertThat( property.name(), is( "complexString" ) );
        assertThat( ((ValueObjectTypePrimitiveType) property.type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );

        property = raml.types().get( 0 ).properties().get( 2 );
        assertThat( property.name(), is( "intArraySimple" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "SimplePropertyTypeIntArraySimpleList" ) );
        assertThat( ((ValueObjectTypeList) property.type()).packageName(), is( "org.generated.types.simplepropertytype" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) property.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.LONG ) );

        property = raml.types().get( 0 ).properties().get( 3 );
        assertThat( property.name(), is( "intArrayShortcut" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "SimplePropertyTypeIntArrayShortcutList" ) );
        assertThat( ((ValueObjectTypeList) property.type()).packageName(), is( "org.generated.types.simplepropertytype" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) property.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.LONG ) );
    }

    @Test
    public void testObjectValueType() throws Exception {
        ParsedRaml raml = new RamlParser( typesPackage, apiPackage ).parseFile( getRaml( "02_objectValueType.raml" ) );
        assertTrue( raml.routes().isEmpty() );
        assertThat( raml.types().size(), is( 1 ) );
        assertThat( raml.types().get( 0 ).name(), is( "TypeWithObjectProperty" ) );
        assertThat( raml.types().get( 0 ).properties().size(), is( 4 ) );

        ValueObjectProperty prop;

        prop = raml.types().get( 0 ).properties().get( 0 );
        assertThat( prop.name(), is( "objSimple" ) );
        assertThat( ((ValueObjectTypePrimitiveType) prop.type()).type(), is( YAML_PRIMITIVE_TYPES.OBJECT ) );

        prop = raml.types().get( 0 ).properties().get( 1 );
        assertThat( prop.name(), is( "objComplex" ) );
        assertThat( ((ValueObjectTypePrimitiveType) prop.type()).type(), is( YAML_PRIMITIVE_TYPES.OBJECT ) );

        prop = raml.types().get( 0 ).properties().get( 2 );
        assertThat( prop.name(), is( "objArraySimple" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).name(), is( "TypeWithObjectPropertyObjArraySimpleList" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).packageName(), is( "org.generated.types.typewithobjectproperty" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) prop.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.OBJECT ) );

        prop = raml.types().get( 0 ).properties().get( 3 );
        assertThat( prop.name(), is( "objArrayShortcut" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).name(), is( "TypeWithObjectPropertyObjArrayShortcutList" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).packageName(), is( "org.generated.types.typewithobjectproperty" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) prop.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.OBJECT ) );
    }

    @Test
    public void testInSpecProperties() throws Exception {
        ParsedRaml raml = new RamlParser( typesPackage, apiPackage ).parseFile( getRaml( "03_inSpecProperties.raml" ) );
        assertTrue( raml.routes().isEmpty() );
        assertThat( raml.types().size(), is( 2 ) );
        assertThat( raml.types().get( 0 ).name(), is( "InSpecPropertyType" ) );
        assertThat( raml.types().get( 0 ).properties().size(), is( 4 ) );

        ValueObjectProperty prop;

        prop = raml.types().get( 0 ).properties().get( 0 );
        assertThat( prop.name(), is( "objSimple" ) );
        assertThat( ((ObjectTypeInSpecValueObject) prop.type()).inSpecValueObjectName(), is( "Toto" ) );

        prop = raml.types().get( 0 ).properties().get( 1 );
        assertThat( prop.name(), is( "objComplex" ) );
        assertThat( ((ObjectTypeInSpecValueObject) prop.type()).inSpecValueObjectName(), is( "Toto" ) );

        prop = raml.types().get( 0 ).properties().get( 2 );
        assertThat( prop.name(), is( "objArraySimple" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).name(), is( "InSpecPropertyTypeObjArraySimpleList" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).packageName(), is( "org.generated.types.inspecpropertytype" ) );
        assertThat( ((ObjectTypeInSpecValueObject) ((ValueObjectTypeList) prop.type()).type()).inSpecValueObjectName(), is( "Toto" ) );

        prop = raml.types().get( 0 ).properties().get( 3 );
        assertThat( prop.name(), is( "objArrayShortcut" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).name(), is( "InSpecPropertyTypeObjArrayShortcutList" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).packageName(), is( "org.generated.types.inspecpropertytype" ) );
        assertThat( ((ObjectTypeInSpecValueObject) ((ValueObjectTypeList) prop.type()).type()).inSpecValueObjectName(), is( "Toto" ) );
    }

    @Test
    public void testEnumProperties() throws Exception {
        ParsedRaml raml = new RamlParser( typesPackage, apiPackage ).parseFile( getRaml( "04_enumProperties.raml" ) );
        assertTrue( raml.routes().isEmpty() );
        assertThat( raml.types().size(), is( 1 ) );
        assertThat( raml.types().get( 0 ).name(), is( "EnumPropertyType" ) );
        assertThat( raml.types().get( 0 ).properties().size(), is( 2 ) );

        ValueObjectProperty prop;

        prop = raml.types().get( 0 ).properties().get( 0 );
        assertThat( prop.name(), is( "simpleEnum" ) );
        assertThat( ((YamlEnumInSpecEnum) prop.type()).name(), is( "EnumPropertyTypeSimpleEnum" ) );
        assertThat( ((YamlEnumInSpecEnum) prop.type()).namespace(), is( "enumpropertytype" ) );
        assertThat( ((YamlEnumInSpecEnum) prop.type()).values().toArray( new String[0] ), is( new String[]{ "A", "B", "C" } ) );

        prop = raml.types().get( 0 ).properties().get( 1 );
        assertThat( prop.name(), is( "enumList" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).name(), is( "EnumPropertyTypeEnumListList" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).packageName(), is( "org.generated.types.enumpropertytype" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) prop.type()).type()).name(), is( "EnumPropertyTypeEnumList" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) prop.type()).type()).namespace(), is( "enumpropertytype" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) prop.type()).type()).values().toArray( new String[0] ), is( new String[]{ "D", "E", "F" } ) );
    }

    @Test
    public void testAlreadyDefinedType() throws Exception {
        ParsedRaml raml = new RamlParser( typesPackage, apiPackage ).parseFile( getRaml( "05_alreadyDefinedType.raml" ) );
        assertTrue( raml.routes().isEmpty() );
        assertThat( raml.types().size(), is( 2 ) );
        assertThat( raml.types().get( 0 ).name(), is( "IncludeADT" ) );

        ValueObjectProperty prop;

        prop = raml.types().get( 0 ).properties().get( 0 );
        assertThat( prop.name(), is( "adt" ) );
        assertThat( ((ObjectTypeExternalValue) prop.type()).objectReference(), is( "org.codingmatters.AnExternalValueObject" ) );

        prop = raml.types().get( 0 ).properties().get( 1 );
        assertThat( prop.name(), is( "adtShort" ) );
        assertThat( ((ObjectTypeExternalValue) prop.type()).objectReference(), is( "org.codingmatters.AnExternalValueObject" ) );

        prop = raml.types().get( 0 ).properties().get( 2 );
        assertThat( prop.name(), is( "adtShortList" ) );
        assertThat( ((ObjectTypeExternalValue) ((ValueObjectTypeList) prop.type()).type()).objectReference(), is( "org.codingmatters.AnExternalValueObject" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).name(), is( "IncludeADTAdtShortListList" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).packageName(), is( "org.generated.types.includeadt" ) );

        prop = raml.types().get( 0 ).properties().get( 3 );
        assertThat( prop.name(), is( "adtList" ) );
        assertThat( ((ObjectTypeExternalValue) ((ValueObjectTypeList) prop.type()).type()).objectReference(), is( "org.codingmatters.AnExternalValueObject" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).name(), is( "IncludeADTAdtListList" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).packageName(), is( "org.generated.types.includeadt" ) );


        prop = raml.types().get( 1 ).properties().get( 0 );
        assertThat( prop.name(), is( "prop" ) );
        assertThat( ((ObjectTypeExternalValue) prop.type()).objectReference(), is( "io.flexio.services.resources.api.types.Schema" ) );

        prop = raml.types().get( 1 ).properties().get( 1 );
        assertThat( prop.name(), is( "propList" ) );
        assertThat( ((ObjectTypeExternalValue) ((ValueObjectTypeList) prop.type()).type()).objectReference(), is( "io.flexio.services.resources.api.types.Schema" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).name(), is( "TypeWithAlreadyDefinedPropertyPropListList" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).packageName(), is( "org.generated.types.typewithalreadydefinedproperty" ) );
    }

    @Test
    public void testNestedTypes() throws Exception {
        ParsedRaml raml = new RamlParser( typesPackage, apiPackage ).parseFile( getRaml( "06_nestedType.raml" ) );
        assertTrue( raml.routes().isEmpty() );
        assertThat( raml.types().size(), is( 1 ) );
        assertThat( raml.types().get( 0 ).name(), is( "NestedType" ) );

        ValueObjectProperty prop;

        prop = raml.types().get( 0 ).properties().get( 0 );
        assertThat( prop.name(), is( "nested" ) );
        assertThat( ((ObjectTypeNested) prop.type()).namespace(), is( "nestedtype" ) );
        ParsedValueObject nestValueObject = ((ObjectTypeNested) prop.type()).nestValueObject();
        assertThat( nestValueObject.name(), is( "NestedTypeNested" ) );
        assertThat( nestValueObject.properties().size(), is( 3 ) );

        prop = nestValueObject.properties().get( 0 );
        assertThat( prop.name(), is( "stringProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) prop.type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );

        prop = nestValueObject.properties().get( 1 );
        assertThat( prop.name(), is( "enumProp" ) );
        assertThat( ((YamlEnumInSpecEnum) prop.type()).values().toArray( new String[0] ), is( new String[]{ "A", "B", "C" } ) );
        assertThat( ((YamlEnumInSpecEnum) prop.type()).namespace(), is( "nested" ) );
        assertThat( ((YamlEnumInSpecEnum) prop.type()).name(), is( "NestedTypeNested" ) );

        prop = nestValueObject.properties().get( 2 );
        assertThat( prop.name(), is( "enumArrayProp" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) prop.type()).type()).values().toArray( new String[0] ), is( new String[]{ "D", "E", "F" } ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) prop.type()).type()).name(), is( "NestedTypeNested" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) prop.type()).type()).namespace(), is( "nested" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).name(), is( "NestedEnumArrayPropList" ) );
        assertThat( ((ValueObjectTypeList) prop.type()).packageName(), is( "org.generated.types.nested" ) );
    }

    //    @Test
    public void testBiDim() throws Exception {
        ParsedRaml raml = new RamlParser( typesPackage, apiPackage ).parseFile( getRaml( "toto.raml" ) );
        System.out.println( "end" );
    }

    private String getRaml( String ramlFile ) {
        return Thread.currentThread().getContextClassLoader().getResource( ramlFile ).getPath();
    }
}
