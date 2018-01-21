package org.codingmatters.rest.api.generator.response;

import org.codingmatters.rest.api.generator.ApiGenerator;
import org.codingmatters.rest.api.types.File;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.value.objects.spec.*;
import org.codingmatters.value.objects.values.ObjectValue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/3/17.
 */
public class ResponsePayloadTest {

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private Spec spec;

    @Before
    public void setUp() throws Exception {
        this.spec = new ApiGenerator("org.generated.types").generate(new RamlModelBuilder().buildApi(this.fileHelper.fileResource("types/response-payload.raml")));
    }

    @Test
    public void typePayload() throws Exception {
        assertThat(
                this.spec.valueSpec("RootResourceGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec(),
                is(AnonymousValueSpec.anonymousValueSpec()
                        .addProperty(PropertySpec.property()
                                .name("payload")
                                .type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                        .typeRef("org.generated.types.AType")
                                )
                        )
                        .build())
        );
    }

    @Test
    public void stringPayload() throws Exception {
        assertThat(
                this.spec.valueSpec("StringPayloadGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec(),
                is(AnonymousValueSpec.anonymousValueSpec()
                        .addProperty(PropertySpec.property()
                                .name("payload")
                                .type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(String.class.getName())
                                )
                        )
                        .build())
        );
    }

    @Test
    public void filePayload() throws Exception {
        assertThat(
                this.spec.valueSpec("FilePayloadGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec(),
                is(AnonymousValueSpec.anonymousValueSpec()
                        .addProperty(PropertySpec.property()
                                .name("payload")
                                .type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                        .typeRef(File.class.getName())
                                )
                        )
                        .build())
        );
    }

    @Test
    public void objectPayload() throws Exception {
        assertThat(
                this.spec.valueSpec("ObjectPayloadGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec(),
                is(AnonymousValueSpec.anonymousValueSpec()
                        .addProperty(PropertySpec.property()
                                .name("payload")
                                .type(PropertyTypeSpec.type()
                                        .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                        .typeRef(ObjectValue.class.getName())
                                )
                        )
                        .build())
        );
    }


}
