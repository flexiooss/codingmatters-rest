package org.codingmatters.rest.api.generator;

import org.codingmatters.value.objects.spec.*;
import org.junit.Before;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;

import static org.codingmatters.rest.api.generator.util.Helper.fileResource;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/5/17.
 */
public class RequestURIParameterTest {

    private Spec spec;

    @Before
    public void setUp() throws Exception {
        this.spec = new ApiGenerator("org.generated.types").generate(new RamlModelBuilder().buildApi(fileResource("types/request-uri-parameters.raml")));
    }

    @Test
    public void propertyCount() throws Exception {
        assertThat(this.spec.valueSpec("RootResourceGetRequest").propertySpecs(), hasSize(1));
    }

    @Test
    public void completePart() throws Exception {
        assertThat(
                spec.valueSpec("RootResourceGetRequest").propertySpec("param"),
                is(PropertySpec.property().name("param")
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.SINGLE)
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(String.class.getName())
                        )
                        .build())
        );
    }
}
