package org.codingmatters.http.api.generator;

import org.codingmatters.value.objects.spec.*;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;

import static org.codingmatters.http.api.generator.util.Helper.fileResource;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/2/17.
 */
public class QueryParametersTest {

    @Test
    public void parameters() throws Exception {
        Spec spec = new ApiSpecGenerator().generate(new RamlModelBuilder().buildApi(fileResource("query-parameters.raml")));

        assertThat(spec.valueSpec("RootResourceGetRequest").propertySpecs(), hasSize(2));

        assertThat(
                spec.valueSpec("RootResourceGetRequest").propertySpec("stringParam"),
                is(PropertySpec.property().name("stringParam")
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.SINGLE)
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(String.class.getName())
                        )
                        .build())
        );
        assertThat(
                spec.valueSpec("RootResourceGetRequest").propertySpec("intParam"),
                is(PropertySpec.property().name("intParam")
                        .type(PropertyTypeSpec.type()
                                .cardinality(PropertyCardinality.SINGLE)
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(Long.class.getName())
                        )
                        .build())
        );
    }
}
