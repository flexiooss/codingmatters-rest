package org.codingmatters.rest.js.api.client;

import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES;
import org.junit.Before;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ApiTypesJsGeneratorTest {

    private PackagesConfiguration packageConfig;

    @Before
    public void setUp() throws Exception {
        packageConfig = new PackagesConfiguration( "org.generated.client", "org.generated.api", "org.generated.types" );
    }

    @Test
    public void testSimplePropertiesType() throws Exception {
        RamlModelResult ramlModel;
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "basicProperties.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        ApiTypesJsGenerator generator = new ApiTypesJsGenerator( packageConfig );
        List<ParsedValueObject> valueObjects = generator.parseRamlTypes( ramlModel );

        assertThat( valueObjects.size(), is( 1 ) );
        ParsedValueObject object = valueObjects.get( 0 );
        assertThat( object.properties().size(), is( 9 ) );

        ValueObjectProperty property = object.properties().get( 0 );
        assertThat( property.name(), is( "simpleString" ) );
        assertThat( ((ValueObjectTypePrimitiveType) property.type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );

        property = object.properties().get( 1 );
        assertThat( property.name(), is( "stringProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) property.type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );

        property = object.properties().get( 2 );
        assertThat( property.name(), is( "stringArrayProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) (property.type())).type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );
        assertThat( ((ValueObjectTypeList) (property.type())).namespace(), is( "org.generated.types.simplepropertytype" ) );
        assertThat( ((ValueObjectTypeList) (property.type())).name(), is( "SimplePropertyTypeStringArrayPropList" ) );

        property = object.properties().get( 3 );
        assertThat( property.name(), is( "shortStringArray" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) property.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );
        assertThat( ((ValueObjectTypeList) property.type()).namespace(), is( "org.generated.types.simplepropertytype" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "SimplePropertyTypeShortStringArrayList" ) );

        property = object.properties().get( 4 );
        assertThat( property.name(), is( "intProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) property.type()).type(), is( YAML_PRIMITIVE_TYPES.INT ) );

        property = object.properties().get( 5 );
        assertThat( property.name(), is( "intArrayProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) property.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.INT ) );
        assertThat( ((ValueObjectTypeList) property.type()).namespace(), is( "org.generated.types.simplepropertytype" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "SimplePropertyTypeIntArrayPropList" ) );

        property = object.properties().get( 6 );
        assertThat( property.name(), is( "intShortArray" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) property.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.INT ) );
        assertThat( ((ValueObjectTypeList) property.type()).namespace(), is( "org.generated.types.simplepropertytype" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "SimplePropertyTypeIntShortArrayList" ) );

        property = object.properties().get( 7 );
        assertThat( property.name(), is( "enumProp" ) );
        assertThat( ((YamlEnumInSpecEnum) property.type()).values().toArray(), is( new String[]{ "A", "B", "C" } ) );
        assertThat( ((YamlEnumInSpecEnum) property.type()).namespace(), is( "org.generated.types.simplepropertytype" ) );
        assertThat( ((YamlEnumInSpecEnum) property.type()).name(), is( "SimplePropertyTypeEnumProp" ) );

        property = object.properties().get( 8 );
        assertThat( property.name(), is( "enumArrayProp" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) property.type()).type()).values().toArray(), is( new String[]{ "D", "E", "F" } ) );
        assertThat( ((ValueObjectTypeList) property.type()).namespace(), is( "org.generated.types.simplepropertytype" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "SimplePropertyTypeEnumArrayPropList" ) );
    }

    @Test
    public void testNestedType() throws Exception {
        RamlModelResult ramlModel;
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "nestedType.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        ApiTypesJsGenerator generator = new ApiTypesJsGenerator( packageConfig );
        List<ParsedValueObject> valueObjects = generator.parseRamlTypes( ramlModel );
        assertThat( valueObjects.size(), is( 1 ) );
        ParsedValueObject object = valueObjects.get( 0 );

        ValueObjectProperty property = object.properties().get( 0 );
        assertThat( property.name(), is( "nested" ) );
        assertThat( ((ObjectTypeNested) property.type()).namespace(), is( "org.generated.types" ) );
        assertThat( ((ObjectTypeNested) property.type()).nestValueObject().name(), is( "Nested" ) );
        assertThat( ((ObjectTypeNested) property.type()).nestValueObject().properties().size(), is( 3 ) );

        ValueObjectProperty subProperty = ((ObjectTypeNested) property.type()).nestValueObject().properties().get( 0 );
        assertThat( subProperty.name(), is( "stringProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) subProperty.type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );
        subProperty = ((ObjectTypeNested) property.type()).nestValueObject().properties().get( 1 );
        assertThat( subProperty.name(), is( "enumProp" ) );
        assertThat( ((YamlEnumInSpecEnum) subProperty.type()).namespace(), is( "org.generated.types.nestedtype" ) );
        assertThat( ((YamlEnumInSpecEnum) subProperty.type()).name(), is( "NestedTypeEnumProp" ) );
        assertThat( ((YamlEnumInSpecEnum) subProperty.type()).values().toArray(), is( new String[]{ "A", "B", "C" } ) );
        subProperty = ((ObjectTypeNested) property.type()).nestValueObject().properties().get( 2 );
        assertThat( subProperty.name(), is( "enumArrayProp" ) );
        assertThat( ((ValueObjectTypeList) subProperty.type()).namespace(), is( "org.generated.types.nestedtype" ) );
        assertThat( ((ValueObjectTypeList) subProperty.type()).name(), is( "NestedTypeEnumArrayPropList" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) subProperty.type()).type()).name(), is( "NestedTypeEnumArrayProp" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) subProperty.type()).type()).namespace(), is( "org.generated.types.nestedtype" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) subProperty.type()).type()).values().toArray(), is( new String[]{ "D", "E", "F" } ) );
    }

    @Test
    public void testReferencedType() throws Exception {
        RamlModelResult ramlModel;
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "referenceType.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        ApiTypesJsGenerator generator = new ApiTypesJsGenerator( packageConfig );
        List<ParsedValueObject> valueObjects = generator.parseRamlTypes( ramlModel );
        assertThat( valueObjects.size(), is( 2 ) );
        ParsedValueObject object = valueObjects.get( 1 );

        ValueObjectProperty property = object.properties().get( 0 );
        assertThat( property.name(), is( "reference" ) );
        assertThat( ((ObjectTypeInSpecValueObject) property.type()).inSpecValueObjectName(), is( "SimplePropertyType" ) );

        property = object.properties().get( 1 );
        assertThat( property.name(), is( "typeReference" ) );
        assertThat( ((ObjectTypeInSpecValueObject) property.type()).inSpecValueObjectName(), is( "SimplePropertyType" ) );

        property = object.properties().get( 2 );
        assertThat( property.name(), is( "referenceArray" ) );
        assertThat( ((ValueObjectTypeList) property.type()).namespace(), is( "org.generated.types.referencestype" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "ReferencesTypeReferenceArrayList" ) );
        assertThat( ((ObjectTypeInSpecValueObject) ((ValueObjectTypeList) property.type()).type()).inSpecValueObjectName(), is( "SimplePropertyType" ) );
    }

    @Test
    public void testObjectValueType() throws Exception {
        RamlModelResult ramlModel;
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "objectValueType.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        ApiTypesJsGenerator generator = new ApiTypesJsGenerator( packageConfig );
        List<ParsedValueObject> valueObjects = generator.parseRamlTypes( ramlModel );
        assertThat( valueObjects.size(), is( 1 ) );
        ParsedValueObject object = valueObjects.get( 0 );

        ValueObjectProperty property = object.properties().get( 0 );
        assertThat( property.name(), is( "obj" ) );
        assertThat( ((ValueObjectTypePrimitiveType) property.type()).type(), is( YAML_PRIMITIVE_TYPES.OBJECT ) );

        property = object.properties().get( 1 );
        assertThat( property.name(), is( "objs" ) );
        assertThat( ((ValueObjectTypeList) property.type()).namespace(), is( "org.generated.types.typewithobjectproperty" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "TypeWithObjectPropertyObjsList" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) property.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.OBJECT ) );

        property = object.properties().get( 2 );
        assertThat( property.name(), is( "shortObjs" ) );
        assertThat( ((ValueObjectTypeList) property.type()).namespace(), is( "org.generated.types.typewithobjectproperty" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "TypeWithObjectPropertyShortObjsList" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) property.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.OBJECT ) );
    }

    @Test
    public void testAlreadyDefinedType() throws Exception {
        RamlModelResult ramlModel;
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "alreadyDefinedType.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        ApiTypesJsGenerator generator = new ApiTypesJsGenerator( packageConfig );
        List<ParsedValueObject> valueObjects = generator.parseRamlTypes( ramlModel );
        assertThat( valueObjects.size(), is( 2 ) );
        ParsedValueObject object = valueObjects.get( 0 );

        ValueObjectProperty property = object.properties().get( 0 );
        assertThat( property.name(), is( "adt" ) );
        assertThat( ((ObjectTypeExternalValue) property.type()).objectReference(), is( "org.codingmatters.AnExternalValueObject" ) );

        property = object.properties().get( 1 );
        assertThat( property.name(), is( "adtShort" ) );
        assertThat( ((ObjectTypeExternalValue) property.type()).objectReference(), is( "org.codingmatters.AnExternalValueObject" ) );

        property = object.properties().get( 2 );
        assertThat( property.name(), is( "adtShortList" ) );
        assertThat( ((ValueObjectTypeList) property.type()).namespace(), is( "org.generated.types.includeadt" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "IncludeADTAdtShortListList" ) );
        assertThat( ((ObjectTypeExternalValue) ((ValueObjectTypeList) property.type()).type()).objectReference(), is( "org.codingmatters.AnExternalValueObject" ) );

        property = object.properties().get( 3 );
        assertThat( property.name(), is( "adtList" ) );
        assertThat( ((ValueObjectTypeList) property.type()).namespace(), is( "org.generated.types.includeadt" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "IncludeADTAdtListList" ) );
        assertThat( ((ObjectTypeExternalValue) ((ValueObjectTypeList) property.type()).type()).objectReference(), is( "org.codingmatters.AnExternalValueObject" ) );


        object = valueObjects.get( 1 );
        property = object.properties().get( 0 );

        assertThat( property.name(), is( "prop" ) );
        assertThat( ((ObjectTypeExternalValue) property.type()).objectReference(), is( "io.flexio.services.resources.api.types.Schema" ) );

        property = object.properties().get( 1 );

        assertThat( property.name(), is( "propList" ) );
        assertThat( ((ValueObjectTypeList) property.type()).namespace(), is( "org.generated.types.typewithalreadydefinedproperty" ) );
        assertThat( ((ValueObjectTypeList) property.type()).name(), is( "TypeWithAlreadyDefinedPropertyPropListList" ) );
        assertThat( ((ObjectTypeExternalValue) ((ValueObjectTypeList) property.type()).type()).objectReference(), is( "io.flexio.services.resources.api.types.Schema" ) );
    }

}
