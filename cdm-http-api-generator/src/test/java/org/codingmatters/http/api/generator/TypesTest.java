package org.codingmatters.http.api.generator;

import org.codingmatters.value.objects.spec.*;
import org.junit.Before;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;

import static org.codingmatters.http.api.generator.util.Helper.fileResource;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/2/17.
 */
public class TypesTest {

    private Spec spec;

    @Before
    public void setUp() throws Exception {
        this.spec = new ApiTypesGenerator().generate(new RamlModelBuilder().buildApi(fileResource("types.raml")));
    }

    @Test
    public void simplePropertyType() throws Exception {
        assertThat(
                this.spec.valueSpec("SimplePropertyType"),
                is(ValueSpec.valueSpec().name("SimplePropertyType")
                        .addProperty(PropertySpec.property()
                                .name("stringProp")
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(String.class.getName())
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("stringArrayProp")
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.LIST)
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(String.class.getName())
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("intProp")
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(Long.class.getName())
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("intArrayProp")
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.LIST)
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(Long.class.getName())
                                )
                        )
                        .build())
        );
    }
}
