package org.codingmatters.rest.php.api.client;

import org.codingmatters.rest.php.api.client.model.ApiTypesPhpGenerator;
import org.codingmatters.value.objects.spec.*;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ApiTypesGeneratorTest {

    @Test
    public void testSimplePropertiesType() throws Exception {
        RamlModelResult ramlModel;

        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "basicProperties.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiTypesPhpGenerator( "org.generated" ).generate( ramlModel );

        assertThat( spec.valueSpecs().size(), is( 1 ) );
        ValueSpec valueSpec = spec.valueSpecs().get( 0 );
        assertThat( valueSpec.propertySpecs().size(), is( 9 ) );

        assertThat( valueSpec.propertySpec( "simpleString" ).typeSpec().typeRef(), is( "string" ) );
        assertThat( valueSpec.propertySpec( "simpleString" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( valueSpec.propertySpec( "simpleString" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );

        assertThat( valueSpec.propertySpec( "stringProp" ).typeSpec().typeRef(), is( "string" ) );
        assertThat( valueSpec.propertySpec( "stringProp" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( valueSpec.propertySpec( "stringProp" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );

        assertThat( valueSpec.propertySpec( "stringArrayProp" ).typeSpec().typeRef(), is( "org.generated.simplepropertytype.SimplePropertyTypeStringArrayPropList" ) );
        assertThat( valueSpec.propertySpec( "stringArrayProp" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "stringArrayProp" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "stringArrayProp" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "string" ) );
        assertThat( valueSpec.propertySpec( "stringArrayProp" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( valueSpec.propertySpec( "shortStringArray" ).typeSpec().typeRef(), is( "org.generated.simplepropertytype.SimplePropertyTypeShortStringArrayList" ) );
        assertThat( valueSpec.propertySpec( "shortStringArray" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "shortStringArray" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "shortStringArray" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "string" ) );
        assertThat( valueSpec.propertySpec( "shortStringArray" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( valueSpec.propertySpec( "intProp" ).typeSpec().typeRef(), is( "int" ) );
        assertThat( valueSpec.propertySpec( "intProp" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( valueSpec.propertySpec( "intProp" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );

        assertThat( valueSpec.propertySpec( "intArrayProp" ).typeSpec().typeRef(), is( "org.generated.simplepropertytype.SimplePropertyTypeIntArrayPropList" ) );
        assertThat( valueSpec.propertySpec( "intArrayProp" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "intArrayProp" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "intArrayProp" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "int" ) );
        assertThat( valueSpec.propertySpec( "intArrayProp" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( valueSpec.propertySpec( "intShortArray" ).typeSpec().typeRef(), is( "org.generated.simplepropertytype.SimplePropertyTypeIntShortArrayList" ) );
        assertThat( valueSpec.propertySpec( "intShortArray" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "intShortArray" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "intShortArray" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "int" ) );
        assertThat( valueSpec.propertySpec( "intShortArray" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( valueSpec.propertySpec( "enumProp" ).typeSpec().typeRef(), is( "org.generated.simplepropertytype.SimplePropertyTypeEnumProp" ) );
        assertThat( valueSpec.propertySpec( "enumProp" ).typeSpec().typeKind(), is( TypeKind.ENUM ) );
        assertThat( valueSpec.propertySpec( "enumProp" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( valueSpec.propertySpec( "enumProp" ).typeSpec().enumValues(), is( new String[]{ "A", "B", "C" } ) );

        assertThat( valueSpec.propertySpec( "enumArrayProp" ).typeSpec().typeRef(), is( "org.generated.simplepropertytype.SimplePropertyTypeEnumArrayPropList" ) );
        assertThat( valueSpec.propertySpec( "enumArrayProp" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "enumArrayProp" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "enumArrayProp" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.ENUM ) );
        assertThat( valueSpec.propertySpec( "enumArrayProp" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().enumValues(), is( new String[]{ "D", "E", "F" } ) );
        assertThat( valueSpec.propertySpec( "enumArrayProp" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "org.generated.simplepropertytype.SimplePropertyTypeEnumArrayProp" ) );
    }

    @Test
    public void testNestedType() throws Exception {
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "nestedType.raml" ).getPath();

        RamlModelResult ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiTypesPhpGenerator( "org.generated" ).generate( ramlModel );

        assertThat( spec.valueSpecs().size(), is( 1 ) );

        ValueSpec valueSpec = spec.valueSpecs().get( 0 );
        assertThat( valueSpec.propertySpecs().size(), is( 1 ) );

        assertThat( valueSpec.propertySpec( "nested" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "nested" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        AnonymousValueSpec nested = valueSpec.propertySpec( "nested" ).typeSpec().embeddedValueSpec();
        assertThat( nested.propertySpecs().size(), is( 3 ) );

        assertThat( nested.propertySpec( "stringProp" ).typeSpec().typeRef(), is( "string" ) );
        assertThat( nested.propertySpec( "stringProp" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( nested.propertySpec( "stringProp" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );

        assertThat( nested.propertySpec( "enumProp" ).typeSpec().typeRef(), is( "org.generated.nestedtype.NestedTypeEnumProp" ) );
        assertThat( nested.propertySpec( "enumProp" ).typeSpec().typeKind(), is( TypeKind.ENUM ) );
        assertThat( nested.propertySpec( "enumProp" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( nested.propertySpec( "enumProp" ).typeSpec().enumValues(), is( new String[]{ "A", "B", "C" } ) );

        assertThat( nested.propertySpec( "enumArrayProp" ).typeSpec().typeRef(), is( "org.generated.nestedtype.NestedTypeEnumArrayPropList" ) );
        assertThat( nested.propertySpec( "enumArrayProp" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( nested.propertySpec( "enumArrayProp" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( nested.propertySpec( "enumArrayProp" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.ENUM ) );
        assertThat( nested.propertySpec( "enumArrayProp" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().enumValues(), is( new String[]{ "D", "E", "F" } ) );
        assertThat( nested.propertySpec( "enumArrayProp" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "org.generated.nestedtype.NestedTypeEnumArrayProp" ) );
    }

    @Test
    public void testReferencedType() throws Exception {
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "referenceType.raml" ).getPath();

        RamlModelResult ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiTypesPhpGenerator( "org.generated" ).generate( ramlModel );
        assertThat( spec.valueSpecs().size(), is( 2 ) );

        ValueSpec valueSpec = spec.valueSpecs().get( 1 );

        assertThat( valueSpec.propertySpecs().size(), is( 4 ) );

        assertThat( valueSpec.propertySpec( "reference" ).typeSpec().typeRef(), is( "SimplePropertyType" ) );
        assertThat( valueSpec.propertySpec( "reference" ).typeSpec().typeKind(), is( TypeKind.IN_SPEC_VALUE_OBJECT ) );
        assertThat( valueSpec.propertySpec( "reference" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );

        assertThat( valueSpec.propertySpec( "typeReference" ).typeSpec().typeRef(), is( "SimplePropertyType" ) );
        assertThat( valueSpec.propertySpec( "typeReference" ).typeSpec().typeKind(), is( TypeKind.IN_SPEC_VALUE_OBJECT ) );
        assertThat( valueSpec.propertySpec( "typeReference" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );

        assertThat( valueSpec.propertySpec( "referenceArray" ).typeSpec().typeRef(), is( "org.generated.referencestype.ReferencesTypeReferenceArrayList" ) );
        assertThat( valueSpec.propertySpec( "referenceArray" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "referenceArray" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "referenceArray" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "SimplePropertyType" ) );
        assertThat( valueSpec.propertySpec( "referenceArray" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.IN_SPEC_VALUE_OBJECT ) );
        assertThat( valueSpec.propertySpec( "referenceArray" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );

        assertThat( valueSpec.propertySpec( "typeReferenceArray" ).typeSpec().typeRef(), is( "org.generated.referencestype.ReferencesTypeTypeReferenceArrayList" ) );
        assertThat( valueSpec.propertySpec( "typeReferenceArray" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "typeReferenceArray" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "typeReferenceArray" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "SimplePropertyType" ) );
        assertThat( valueSpec.propertySpec( "typeReferenceArray" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.IN_SPEC_VALUE_OBJECT ) );
        assertThat( valueSpec.propertySpec( "typeReferenceArray" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
    }

    @Test
    public void testObjectValueType() throws Exception {
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "objectValueType.raml" ).getPath();

        RamlModelResult ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiTypesPhpGenerator( "org.generated" ).generate( ramlModel );
        assertThat( spec.valueSpecs().size(), is( 1 ) );

        ValueSpec valueSpec = spec.valueSpecs().get( 0 );

        assertThat( valueSpec.propertySpecs().size(), is( 3 ) );
        assertThat( valueSpec.propertySpec( "obj" ).typeSpec().typeRef(), is( "array" ) );
        assertThat( valueSpec.propertySpec( "obj" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( valueSpec.propertySpec( "obj" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( valueSpec.propertySpec( "objs" ).typeSpec().typeRef(), is( "org.generated.typewithobjectproperty.TypeWithObjectPropertyObjsList" ) );
        assertThat( valueSpec.propertySpec( "objs" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "objs" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "objs" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "array" ) );
        assertThat( valueSpec.propertySpec( "objs" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( valueSpec.propertySpec( "shortObjs" ).typeSpec().typeRef(), is( "org.generated.typewithobjectproperty.TypeWithObjectPropertyShortObjsList" ) );
        assertThat( valueSpec.propertySpec( "shortObjs" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "shortObjs" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "shortObjs" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "array" ) );
        assertThat( valueSpec.propertySpec( "shortObjs" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
    }

    @Test
    public void testAlreadyDefinedType() throws Exception {
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "alreadyDefinedType.raml" ).getPath();

        RamlModelResult ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiTypesPhpGenerator( "org.generated" ).generate( ramlModel );
        assertThat( spec.valueSpecs().size(), is( 1 ) );

        ValueSpec valueSpec = spec.valueSpecs().get( 0 );

//        assertThat( valueSpec.propertySpecs().size(), is( 4 ) );

        assertThat( valueSpec.propertySpec( "adtShort" ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );
        assertThat( valueSpec.propertySpec( "adtShort" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( valueSpec.propertySpec( "adtShort" ).typeSpec().typeRef(), is( "org.codingmatters.AnExternalValueObject" ) );

        assertThat( valueSpec.propertySpec( "adt" ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );
        assertThat( valueSpec.propertySpec( "adt" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( valueSpec.propertySpec( "adt" ).typeSpec().typeRef(), is( "org.codingmatters.AnExternalValueObject" ) );

        assertThat( valueSpec.propertySpec( "adtShortList" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "adtShortList" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "adtShortList" ).typeSpec().typeRef(), is( "org.generated.includeadt.IncludeADTAdtShortListList" ) );
        assertThat( valueSpec.propertySpec( "adtShortList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );
        assertThat( valueSpec.propertySpec( "adtShortList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "org.codingmatters.AnExternalValueObject" ) );

        assertThat( valueSpec.propertySpec( "adtList" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "adtList" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "adtList" ).typeSpec().typeRef(), is( "org.generated.includeadt.IncludeADTAdtListList" ) );
        assertThat( valueSpec.propertySpec( "adtList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );
        assertThat( valueSpec.propertySpec( "adtList" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "org.codingmatters.AnExternalValueObject" ) );


    }
}
