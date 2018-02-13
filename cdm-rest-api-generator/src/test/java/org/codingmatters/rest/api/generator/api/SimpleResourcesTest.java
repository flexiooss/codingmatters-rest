package org.codingmatters.rest.api.generator.api;

import org.codingmatters.rest.api.generator.ApiGenerator;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.value.objects.spec.*;
import org.codingmatters.value.objects.values.ObjectValue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/1/17.
 */
public class SimpleResourcesTest {

    @Rule
    public FileHelper fileHelper = new FileHelper();
    private Spec spec;

    @Before
    public void setUp() throws Exception {
        this.spec = new ApiGenerator("org.generated.types").generate(new RamlModelBuilder().buildApi(this.fileHelper.fileResource("types/simple-resources.raml")));
    }

    @Test
    public void rootResource() throws Exception {
        for(String method : Arrays.asList("Get", "Post", "Put", "Delete", "Head", "Patch", "Options")) {
            String request = "RootResource" + method + "Request";
            String response = "RootResource" + method + "Response";
            assertThat(request, spec.valueSpec(request), is(notNullValue()));
            assertThat(response, spec.valueSpec(response), is(notNullValue()));
        }
    }

    @Test
    public void middleResource() throws Exception {
        assertThat(spec.valueSpec("MiddleResourceGetRequest"), is(notNullValue()));
        assertThat(spec.valueSpec("MiddleResourceGetResponse"), is(notNullValue()));
    }

    @Test
    public void leafs() throws Exception {
        assertThat(spec.valueSpec("FirstResourceGetRequest"), is(notNullValue()));
        assertThat(spec.valueSpec("FirstResourceGetResponse"), is(notNullValue()));

        assertThat(spec.valueSpec("SecondResourceGetRequest"), is(notNullValue()));
        assertThat(spec.valueSpec("SecondResourceGetResponse"), is(notNullValue()));
    }

    @Test
    public void obj() throws Exception {
        assertThat(spec.valueSpec("ObjGetRequest").propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef(ObjectValue.class.getName())
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .build())
        );

        assertThat(spec.valueSpec("ObjGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec().propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef(ObjectValue.class.getName())
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .build())
        );
    }

    @Test
    public void objArray() throws Exception {
        assertThat(spec.valueSpec("ObjArrayGetRequest").propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef(ObjectValue.class.getName())
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );

        assertThat(spec.valueSpec("ObjArrayGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec().propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef(ObjectValue.class.getName())
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );
    }

    @Test
    public void objArrayInline() throws Exception {
        assertThat(spec.valueSpec("ObjArrayInlineGetRequest").propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef(ObjectValue.class.getName())
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );

        assertThat(spec.valueSpec("ObjArrayInlineGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec().propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef(ObjectValue.class.getName())
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );
    }



    @Test
    public void simpleType() throws Exception {
        assertThat(spec.valueSpec("SimpleTypeGetRequest").propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.generated.types.SimpleType")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .build())
        );

        assertThat(spec.valueSpec("SimpleTypeGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec().propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.generated.types.SimpleType")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .build())
        );
    }

    @Test
    public void simpleTypeArray() throws Exception {
        assertThat(spec.valueSpec("SimpleTypeArrayGetRequest").propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.generated.types.SimpleType")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );

        assertThat(spec.valueSpec("SimpleTypeArrayGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec().propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.generated.types.SimpleType")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );
    }

    @Test
    public void simpleTypeArrayInline() throws Exception {
        assertThat(spec.valueSpec("SimpleTypeArrayInlineGetRequest").propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.generated.types.SimpleType")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );

        assertThat(spec.valueSpec("SimpleTypeArrayInlineGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec().propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.generated.types.SimpleType")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );
    }

    @Test
    public void alreadyDefinedRequestAndResponseBody() {
        assertThat(spec.valueSpec("AlreadyDefinedRequestAndResponseBodyGetRequest").propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.codingmatters.AnAlreadyDefinedValueObject")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .build())
        );

        assertThat(spec.valueSpec("AlreadyDefinedRequestAndResponseBodyGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec().propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.codingmatters.AnAlreadyDefinedValueObject")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .build())
        );
    }

    @Test
    public void alreadyDefinedArrayRequestAndResponseBody() {
        assertThat(spec.valueSpec("AlreadyDefinedArrayRequestAndResponseBodyGetRequest").propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.codingmatters.AnAlreadyDefinedValueObject")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );

        assertThat(spec.valueSpec("AlreadyDefinedArrayRequestAndResponseBodyGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec().propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.codingmatters.AnAlreadyDefinedValueObject")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );
    }

    @Test
    public void alreadyDefinedInlineArrayRequestAndResponseBody() {
        assertThat(spec.valueSpec("AlreadyDefinedInlineArrayRequestAndResponseBodyGetRequest").propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.codingmatters.AnAlreadyDefinedValueObject")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );

        assertThat(spec.valueSpec("AlreadyDefinedInlineArrayRequestAndResponseBodyGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec().propertySpec("payload"),
                is(PropertySpec.property()
                        .name("payload")
                        .type(PropertyTypeSpec.type()
                                .typeRef("org.codingmatters.AnAlreadyDefinedValueObject")
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .build())
        );
    }
}
