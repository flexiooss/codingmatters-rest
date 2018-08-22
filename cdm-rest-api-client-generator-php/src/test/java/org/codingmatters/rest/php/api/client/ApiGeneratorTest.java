package org.codingmatters.rest.php.api.client;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.php.api.client.model.ApiGeneratorPhp;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.codingmatters.value.objects.spec.ValueSpec;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ApiGeneratorTest {

    @Test
    public void testTypePayload() throws RamlSpecException {
        RamlModelResult ramlModel;

        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "requestBody.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiGeneratorPhp( "org.generated" ).generate( ramlModel );
        ValueSpec valueSpec = this.getValueSpec( spec, "TypePostRequest" );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeRef(), is( "org.generated.LittleObject" ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );

        valueSpec = this.getValueSpec( spec, "TypePostResponse" );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeRef(), is( "org.generated.LittleObject" ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
    }

    @Test
    public void testTypeArrayShortSyntaxPayload() throws RamlSpecException {
        RamlModelResult ramlModel;

        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "requestBody.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiGeneratorPhp( "org.generated" ).generate( ramlModel );
        ValueSpec valueSpec = this.getValueSpec( spec, "TypeArrayShortPostRequest" );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeRef(), is( "org.generated.typearrayshortpostrequest.TypeArrayShortPostRequestLittleObjectList" ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "org.generated.LittleObject" ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );

        valueSpec = this.getValueSpec( spec, "TypeArrayShortPostResponse" );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeRef(), is( "org.generated.typearrayshortpostresponse.TypeArrayShortPostResponseLittleObjectList" ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "org.generated.LittleObject" ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );
    }

    @Test
    public void testTypeArrayPayload() throws RamlSpecException {
        RamlModelResult ramlModel;

        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "requestBody.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiGeneratorPhp( "org.generated" ).generate( ramlModel );
        ValueSpec valueSpec = this.getValueSpec( spec, "TypeArrayPostRequest" );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeRef(), is( "org.generated.typearraypostrequest.TypeArrayPostRequestLittleObjectList" ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "org.generated.LittleObject" ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );

        valueSpec = this.getValueSpec( spec, "TypeArrayPostResponse" );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeRef(), is( "org.generated.typearraypostresponse.TypeArrayPostResponseLittleObjectList" ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "org.generated.LittleObject" ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.EXTERNAL_VALUE_OBJECT ) );
    }

    @Test
    public void testObjectPayload() throws RamlSpecException {
        RamlModelResult ramlModel;

        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "requestBody.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiGeneratorPhp( "org.generated" ).generate( ramlModel );
        ValueSpec valueSpec = this.getValueSpec( spec, "ObjectPostRequest" );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeRef(), is( "array" ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );

        valueSpec = this.getValueSpec( spec, "ObjectPostResponse" );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeRef(), is( "array" ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
    }

    @Test
    public void testObjectArrayShortSyntaxPayload() throws RamlSpecException {
        RamlModelResult ramlModel;

        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "requestBody.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiGeneratorPhp( "org.generated" ).generate( ramlModel );
        ValueSpec valueSpec = this.getValueSpec( spec, "ObjectArrayShortPostRequest" );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeRef(), is( "org.generated.objectarrayshortpostrequest.ObjectArrayShortPostRequestArrayList" ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "array" ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        valueSpec = this.getValueSpec( spec, "ObjectArrayShortPostResponse" );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeRef(), is( "org.generated.objectarrayshortpostresponse.ObjectArrayShortPostResponseArrayList" ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "array" ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
    }

    @Test
    public void testObjectArrayPayload() throws RamlSpecException {
        RamlModelResult ramlModel;

        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "requestBody.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiGeneratorPhp( "org.generated" ).generate( ramlModel );
        ValueSpec valueSpec = this.getValueSpec( spec, "ObjectArrayPostRequest" );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeRef(), is( "org.generated.objectarraypostrequest.ObjectArrayPostRequestArrayList" ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "array" ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        valueSpec = this.getValueSpec( spec, "ObjectArrayPostResponse" );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeRef(), is( "org.generated.objectarraypostresponse.ObjectArrayPostResponseArrayList" ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "array" ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
    }

    @Test
    public void testFilePayload() throws RamlSpecException {
        RamlModelResult ramlModel;

        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "requestBody.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        Spec spec = new ApiGeneratorPhp( "org.generated" ).generate( ramlModel );
        ValueSpec valueSpec = this.getValueSpec( spec, "FilePostRequest" );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeRef(), is( "string" ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( valueSpec.propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );

        valueSpec = this.getValueSpec( spec, "FilePostResponse" );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeRef(), is( "string" ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( valueSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "payload" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
    }

    @Test
    public void testParameters() throws RamlSpecException {
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "parameters.raml" ).getPath();
        RamlModelResult ramlModel = new RamlModelBuilder().buildApi( ramlLocation );
        Spec spec = new ApiGeneratorPhp( "org.generated" ).generate( ramlModel );

        assertThat( spec.valueSpecs().size(), is( 2 ) );

        ValueSpec requestSpec = spec.valueSpecs().get( 0 );

        assertThat( requestSpec.propertySpecs().size(), is( 14 ) );

        assertThat( requestSpec.propertySpec( "stringParam" ).typeSpec().typeRef(), is( "string" ) );
        assertThat( requestSpec.propertySpec( "stringParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( requestSpec.propertySpec( "stringParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( requestSpec.propertySpec( "stringArrayParam" ).typeSpec().typeRef(), is( "org.generated.stringarrayparam.StringArrayParamHeaderParamsGetRequestList" ) );
        assertThat( requestSpec.propertySpec( "stringArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( requestSpec.propertySpec( "stringArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( requestSpec.propertySpec( "stringArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( requestSpec.propertySpec( "stringArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "string" ) );

        assertThat( requestSpec.propertySpec( "intParam" ).typeSpec().typeRef(), is( "int" ) );
        assertThat( requestSpec.propertySpec( "intParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( requestSpec.propertySpec( "intParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( requestSpec.propertySpec( "intArrayParam" ).typeSpec().typeRef(), is( "org.generated.intarrayparam.IntArrayParamHeaderParamsGetRequestList" ) );
        assertThat( requestSpec.propertySpec( "intArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( requestSpec.propertySpec( "intArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( requestSpec.propertySpec( "intArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( requestSpec.propertySpec( "intArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "int" ) );

        assertThat( requestSpec.propertySpec( "floatParam" ).typeSpec().typeRef(), is( "int" ) );
        assertThat( requestSpec.propertySpec( "floatParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( requestSpec.propertySpec( "floatParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( requestSpec.propertySpec( "floatArrayParam" ).typeSpec().typeRef(), is( "org.generated.floatarrayparam.FloatArrayParamHeaderParamsGetRequestList" ) );
        assertThat( requestSpec.propertySpec( "floatArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( requestSpec.propertySpec( "floatArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( requestSpec.propertySpec( "floatArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( requestSpec.propertySpec( "floatArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "int" ) );

        assertThat( requestSpec.propertySpec( "dateParam" ).typeSpec().typeRef(), is( "date" ) );
        assertThat( requestSpec.propertySpec( "dateParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( requestSpec.propertySpec( "dateParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( requestSpec.propertySpec( "dateArrayParam" ).typeSpec().typeRef(), is( "org.generated.datearrayparam.DateArrayParamHeaderParamsGetRequestList" ) );
        assertThat( requestSpec.propertySpec( "dateArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( requestSpec.propertySpec( "dateArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( requestSpec.propertySpec( "dateArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( requestSpec.propertySpec( "dateArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "date" ) );

        assertThat( requestSpec.propertySpec( "datetimeParam" ).typeSpec().typeRef(), is( "datetime" ) );
        assertThat( requestSpec.propertySpec( "datetimeParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( requestSpec.propertySpec( "datetimeParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( requestSpec.propertySpec( "datetimeArrayParam" ).typeSpec().typeRef(), is( "org.generated.datetimearrayparam.DatetimeArrayParamHeaderParamsGetRequestList" ) );
        assertThat( requestSpec.propertySpec( "datetimeArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( requestSpec.propertySpec( "datetimeArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( requestSpec.propertySpec( "datetimeArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( requestSpec.propertySpec( "datetimeArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "datetime" ) );

        assertThat( requestSpec.propertySpec( "timeParam" ).typeSpec().typeRef(), is( "time" ) );
        assertThat( requestSpec.propertySpec( "timeParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( requestSpec.propertySpec( "timeParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( requestSpec.propertySpec( "timeArrayParam" ).typeSpec().typeRef(), is( "org.generated.timearrayparam.TimeArrayParamHeaderParamsGetRequestList" ) );
        assertThat( requestSpec.propertySpec( "timeArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( requestSpec.propertySpec( "timeArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( requestSpec.propertySpec( "timeArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( requestSpec.propertySpec( "timeArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "time" ) );

        assertThat( requestSpec.propertySpec( "boolParam" ).typeSpec().typeRef(), is( "bool" ) );
        assertThat( requestSpec.propertySpec( "boolParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( requestSpec.propertySpec( "boolParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( requestSpec.propertySpec( "boolArrayParam" ).typeSpec().typeRef(), is( "org.generated.boolarrayparam.BoolArrayParamHeaderParamsGetRequestList" ) );
        assertThat( requestSpec.propertySpec( "boolArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( requestSpec.propertySpec( "boolArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( requestSpec.propertySpec( "boolArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( requestSpec.propertySpec( "boolArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "bool" ) );

        ValueSpec responseSpec = spec.valueSpecs().get( 1 );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpecs().size(), is( 14 ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "stringParam" ).typeSpec().typeRef(), is( "string" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "stringParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "stringParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "stringArrayParam" ).typeSpec().typeRef(), is( "org.generated.stringarrayparam.StringArrayParamHeaderParamsGetResponseList" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "stringArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "stringArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "stringArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "stringArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "string" ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "intParam" ).typeSpec().typeRef(), is( "int" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "intParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "intParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "intArrayParam" ).typeSpec().typeRef(), is( "org.generated.intarrayparam.IntArrayParamHeaderParamsGetResponseList" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "intArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "intArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "intArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "intArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "int" ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "floatParam" ).typeSpec().typeRef(), is( "int" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "floatParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "floatParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "floatArrayParam" ).typeSpec().typeRef(), is( "org.generated.floatarrayparam.FloatArrayParamHeaderParamsGetResponseList" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "floatArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "floatArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "floatArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "floatArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "int" ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "dateParam" ).typeSpec().typeRef(), is( "date" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "dateParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "dateParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "dateArrayParam" ).typeSpec().typeRef(), is( "org.generated.datearrayparam.DateArrayParamHeaderParamsGetResponseList" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "dateArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "dateArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "dateArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "dateArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "date" ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "datetimeParam" ).typeSpec().typeRef(), is( "datetime" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "datetimeParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "datetimeParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "datetimeArrayParam" ).typeSpec().typeRef(), is( "org.generated.datetimearrayparam.DatetimeArrayParamHeaderParamsGetResponseList" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "datetimeArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "datetimeArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "datetimeArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "datetimeArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "datetime" ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "timeParam" ).typeSpec().typeRef(), is( "time" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "timeParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "timeParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "timeArrayParam" ).typeSpec().typeRef(), is( "org.generated.timearrayparam.TimeArrayParamHeaderParamsGetResponseList" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "timeArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "timeArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "timeArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "timeArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "time" ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "boolParam" ).typeSpec().typeRef(), is( "bool" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "boolParam" ).typeSpec().cardinality(), is( PropertyCardinality.SINGLE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "boolParam" ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );

        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "boolArrayParam" ).typeSpec().typeRef(), is( "org.generated.boolarrayparam.BoolArrayParamHeaderParamsGetResponseList" ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "boolArrayParam" ).typeSpec().cardinality(), is( PropertyCardinality.LIST ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "boolArrayParam" ).typeSpec().typeKind(), is( TypeKind.EMBEDDED ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "boolArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind(), is( TypeKind.JAVA_TYPE ) );
        assertThat( responseSpec.propertySpec( "status200" ).typeSpec().embeddedValueSpec().propertySpec( "boolArrayParam" ).typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef(), is( "bool" ) );

    }

    private ValueSpec getValueSpec( Spec spec, String name ) {
        return spec.valueSpecs().stream().filter( valueSpec->valueSpec.name().equals( name ) ).findFirst().get();
    }
}
