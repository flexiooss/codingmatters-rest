package org.codingmatters.http.api.generator;

import org.codingmatters.value.objects.spec.*;
import org.junit.Before;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;

import static org.codingmatters.http.api.generator.util.Helper.fileResource;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/3/17.
 */
public class ResponsePayloadTest {

    private Spec spec;

    @Before
    public void setUp() throws Exception {
        this.spec = new ApiGenerator("org.generated.types").generate(new RamlModelBuilder().buildApi(fileResource("response-payload.raml")));
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
