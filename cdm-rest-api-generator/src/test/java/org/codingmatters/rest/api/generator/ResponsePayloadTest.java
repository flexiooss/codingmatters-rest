package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.tests.utils.FileHelper;
import org.codingmatters.value.objects.spec.*;
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
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef("org.generated.types.AType")
                                )
                        )
                        .build())
        );
    }
}
