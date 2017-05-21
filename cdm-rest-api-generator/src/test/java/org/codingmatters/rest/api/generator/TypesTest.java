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
 * Created by nelt on 5/2/17.
 */
public class TypesTest {

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private Spec spec;

    @Before
    public void setUp() throws Exception {
        this.spec = new ApiTypesGenerator().generate(new RamlModelBuilder().buildApi(this.fileHelper.fileResource("types/types.raml")));
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

    @Test
    public void nestedType() throws Exception {
        assertThat(
                this.spec.valueSpec("NestedType"),
                is(ValueSpec.valueSpec().name("NestedType")
                        .addProperty(PropertySpec.property()
                                .name("nested")
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.EMBEDDED)
                                        .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                                .addProperty(PropertySpec.property()
                                                        .name("stringProp")
                                                        .type(PropertyTypeSpec.type()
                                                                .cardinality(PropertyCardinality.SINGLE)
                                                                .typeKind(TypeKind.JAVA_TYPE)
                                                                .typeRef(String.class.getName())
                                                        )
                                                )
                                                .addProperty(PropertySpec.property()
                                                        .name("nested")
                                                        .type(PropertyTypeSpec.type()
                                                            .cardinality(PropertyCardinality.SINGLE)
                                                            .typeKind(TypeKind.EMBEDDED)
                                                            .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                                                    .addProperty(PropertySpec.property()
                                                                            .name("stringProp")
                                                                            .type(PropertyTypeSpec.type()
                                                                                    .cardinality(PropertyCardinality.SINGLE)
                                                                                    .typeKind(TypeKind.JAVA_TYPE)
                                                                                    .typeRef(String.class.getName())
                                                                            )
                                                                    )
                                                            )
                                                        )
                                                )
                                        )
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("nestedArray")
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.LIST)
                                        .typeKind(TypeKind.EMBEDDED)
                                        .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                                .addProperty(PropertySpec.property()
                                                        .name("stringProp")
                                                        .type(PropertyTypeSpec.type()
                                                                .cardinality(PropertyCardinality.SINGLE)
                                                                .typeKind(TypeKind.JAVA_TYPE)
                                                                .typeRef(String.class.getName())
                                                        )
                                                )
                                        )
                                )
                        )
                        .build())
        );
    }
}
