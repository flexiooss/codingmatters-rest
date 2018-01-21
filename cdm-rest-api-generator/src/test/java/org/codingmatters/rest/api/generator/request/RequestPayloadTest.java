package org.codingmatters.rest.api.generator.request;

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
public class RequestPayloadTest {

    private Spec spec;

    @Rule
    public FileHelper fileHelper = new FileHelper();

    @Before
    public void setUp() throws Exception {
        this.spec = new ApiGenerator("org.generated.types").generate(new RamlModelBuilder().buildApi(this.fileHelper.fileResource("types/request-payload.raml")));
    }

    @Test
    public void typePayload() throws Exception {
        assertThat(
                this.spec.valueSpec("RootResourceGetRequest"),
                is(ValueSpec.valueSpec().name("RootResourceGetRequest")
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
    public void filePayload() throws Exception {
        for (ValueSpec valueSpec : this.spec.valueSpecs()) {
            System.out.println(valueSpec);
        }

        assertThat(
                this.spec.valueSpec("FilePayloadPostRequest"),
                is(ValueSpec.valueSpec().name("FilePayloadPostRequest")
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
        for (ValueSpec valueSpec : this.spec.valueSpecs()) {
            System.out.println(valueSpec);
        }

        assertThat(
                this.spec.valueSpec("ArbitraryObjectPostRequest"),
                is(ValueSpec.valueSpec().name("ArbitraryObjectPostRequest")
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
