package org.codingmatters.rest.api.generator.response;

import org.codingmatters.rest.api.generator.ApiGenerator;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.value.objects.spec.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/3/17.
 */
public class ResponseHeadersTest {

    private Spec spec;

    @Rule
    public FileHelper fileHelper = new FileHelper();

    @Before
    public void setUp() throws Exception {
        this.spec = new ApiGenerator("org.generated.types").generate(new RamlModelBuilder().buildApi(this.fileHelper.fileResource("types/response-headers.raml")));
    }

    @Test
    public void propertyCount() throws Exception {
        assertThat(this.spec.valueSpec("RootResourceGetResponse").propertySpecs(), hasSize(1));
        assertThat(
                this.spec.valueSpec("RootResourceGetResponse").propertySpec("status200").typeSpec().embeddedValueSpec().propertySpecs(),
                hasSize(4)
        );
    }

    @Test
    public void singleParameters() throws Exception {
        assertThat(
                this.spec.valueSpec("RootResourceGetResponse")
                        .propertySpec("status200")
                        .typeSpec().embeddedValueSpec()
                        .propertySpec("stringParam"),
                is(PropertySpec.property().name("stringParam")
                        .hints(new HashSet<>(Arrays.asList(String.format("property:raw(%s)", "stringParam"))))
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.SINGLE)
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(String.class.getName())
                        )
                        .build())
        );
        assertThat(
                this.spec.valueSpec("RootResourceGetResponse")
                        .propertySpec("status200")
                        .typeSpec().embeddedValueSpec()
                        .propertySpec("intParam"),
                is(PropertySpec.property().name("intParam")
                        .hints(new HashSet<>(Arrays.asList(String.format("property:raw(%s)", "intParam"))))
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.SINGLE)
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(Long.class.getName())
                        )
                        .build())
        );
    }

    @Test
    public void arrayParameters() throws Exception {
        assertThat(
                this.spec.valueSpec("RootResourceGetResponse")
                        .propertySpec("status200")
                        .typeSpec().embeddedValueSpec()
                        .propertySpec("stringArrayParam"),
                is(PropertySpec.property().name("stringArrayParam")
                        .hints(new HashSet<>(Arrays.asList(String.format("property:raw(%s)", "stringArrayParam"))))
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.LIST)
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(String.class.getName())
                        )
                        .build())
        );
        assertThat(
                this.spec.valueSpec("RootResourceGetResponse")
                        .propertySpec("status200")
                        .typeSpec().embeddedValueSpec()
                        .propertySpec("intArrayParam"),
                is(PropertySpec.property().name("intArrayParam")
                        .hints(new HashSet<>(Arrays.asList(String.format("property:raw(%s)", "intArrayParam"))))
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.LIST)
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(Long.class.getName())
                        )
                        .build())
        );
    }

    @Test
    public void dollarHeaders() throws Exception {
        assertThat(
                this.spec.valueSpec("WithDollarsGetResponse")
                        .propertySpec("status200")
                        .typeSpec().embeddedValueSpec()
                        .propertySpec("headerWithDollar"),
                is(PropertySpec.property().name("headerWithDollar")
                        .hints(new HashSet<>(Arrays.asList(String.format("property:raw(%s)", "$headerWithDollar"))))
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.SINGLE)
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(String.class.getName())
                        )
                        .build())
        );
    }
}
