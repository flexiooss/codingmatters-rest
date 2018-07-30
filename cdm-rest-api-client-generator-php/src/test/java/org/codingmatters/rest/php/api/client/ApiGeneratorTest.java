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

    private ValueSpec getValueSpec( Spec spec, String name ) {
        return spec.valueSpecs().stream().filter( valueSpec->valueSpec.name().equals( name ) ).findFirst().get();
    }
}
