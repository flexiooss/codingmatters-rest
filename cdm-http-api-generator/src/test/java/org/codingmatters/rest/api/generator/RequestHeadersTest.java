package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.generator.util.Helper;
import org.codingmatters.value.objects.spec.*;
import org.junit.Before;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/2/17.
 */
public class RequestHeadersTest {

    private Spec spec;

    @Before
    public void setUp() throws Exception {
        this.spec = new ApiGenerator("org.generated.types").generate(new RamlModelBuilder().buildApi(Helper.fileResource("request-headers.raml")));
    }

    @Test
    public void propertyCount() throws Exception {
        assertThat(this.spec.valueSpec("RootResourceGetRequest").propertySpecs(), hasSize(4));
    }

    @Test
    public void singleParameters() throws Exception {
        assertThat(
                this.spec.valueSpec("RootResourceGetRequest").propertySpec("stringParam"),
                is(PropertySpec.property().name("stringParam")
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.SINGLE)
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(String.class.getName())
                        )
                        .build())
        );
        assertThat(
                this.spec.valueSpec("RootResourceGetRequest").propertySpec("intParam"),
                is(PropertySpec.property().name("intParam")
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
                this.spec.valueSpec("RootResourceGetRequest").propertySpec("stringArrayParam"),
                is(PropertySpec.property().name("stringArrayParam")
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.LIST)
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(String.class.getName())
                        )
                        .build())
        );
        assertThat(
                this.spec.valueSpec("RootResourceGetRequest").propertySpec("intArrayParam"),
                is(PropertySpec.property().name("intArrayParam")
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.LIST)
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(Long.class.getName())
                        )
                        .build())
        );
    }
}
