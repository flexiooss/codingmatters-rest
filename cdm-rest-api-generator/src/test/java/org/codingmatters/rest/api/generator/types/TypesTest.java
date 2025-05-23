package org.codingmatters.rest.api.generator.types;

import org.codingmatters.rest.api.generator.ApiTypesGenerator;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.value.objects.spec.*;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.vals.Val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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
                                .hints(set("property:raw(stringProp)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(String.class.getName())
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("stringArrayProp")
                                .hints(set("property:raw(stringArrayProp)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.LIST)
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(String.class.getName())
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("intProp")
                                .hints(set("property:raw(intProp)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(Long.class.getName())
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("intArrayProp")
                                .hints(set("property:raw(intArrayProp)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.LIST)
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(Long.class.getName())
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("enumProp")
                                .hints(set("property:raw(enumProp)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.ENUM)
                                        .enumValues("A", "B", "C")
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("enumArrayProp")
                                .hints(set("property:raw(enumArrayProp)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.LIST)
                                        .typeKind(TypeKind.ENUM)
                                        .enumValues("A", "B", "C")
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
                                .hints(set("property:raw(nested)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.EMBEDDED)
                                        .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                                .addProperty(PropertySpec.property()
                                                        .name("stringProp")
                                                        .hints(set("property:raw(stringProp)"))
                                                        .type(PropertyTypeSpec.type()
                                                                .cardinality(PropertyCardinality.SINGLE)
                                                                .typeKind(TypeKind.JAVA_TYPE)
                                                                .typeRef(String.class.getName())
                                                        )
                                                )
                                                .addProperty(PropertySpec.property()
                                                        .name("nestedProp")
                                                        .hints(set("property:raw(nested-prop)"))
                                                        .type(PropertyTypeSpec.type()
                                                            .cardinality(PropertyCardinality.SINGLE)
                                                            .typeKind(TypeKind.EMBEDDED)
                                                            .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                                                    .addProperty(PropertySpec.property()
                                                                            .name("stringProp")
                                                                            .hints(set("property:raw(stringProp)"))
                                                                            .type(PropertyTypeSpec.type()
                                                                                    .cardinality(PropertyCardinality.SINGLE)
                                                                                    .typeKind(TypeKind.JAVA_TYPE)
                                                                                    .typeRef(String.class.getName())
                                                                            )
                                                                    )
                                                                    .addProperty(PropertySpec.property()
                                                                            .name("enumProp")
                                                                            .hints(set("property:raw(enumProp)"))
                                                                            .type(PropertyTypeSpec.type()
                                                                                    .cardinality(PropertyCardinality.SINGLE)
                                                                                    .typeKind(TypeKind.ENUM)
                                                                                    .enumValues("A", "B", "C")
                                                                            )
                                                                    )
                                                                    .addProperty(PropertySpec.property()
                                                                            .name("enumArrayProp")
                                                                            .hints(set("property:raw(enumArrayProp)"))
                                                                            .type(PropertyTypeSpec.type()
                                                                                    .cardinality(PropertyCardinality.LIST)
                                                                                    .typeKind(TypeKind.ENUM)
                                                                                    .enumValues("A", "B", "C")
                                                                            )
                                                                    )
                                                                    .addProperty(PropertySpec.property()
                                                                            .name("deeplyNested")
                                                                            .hints(set("property:raw(deeply-nested)"))
                                                                            .type(PropertyTypeSpec.type()
                                                                                    .cardinality(PropertyCardinality.SINGLE)
                                                                                    .typeKind(TypeKind.EMBEDDED)
                                                                                    .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                                                                        .addProperty(PropertySpec.property()
                                                                                                .name("dNS")
                                                                                                .hints(set("property:raw(d-n-s)"))
                                                                                                .type(PropertyTypeSpec.type()
                                                                                                        .cardinality(PropertyCardinality.SINGLE)
                                                                                                        .typeKind(TypeKind.JAVA_TYPE)
                                                                                                        .typeRef(String.class.getName())
                                                                                                ))
                                                                                            .addProperty(PropertySpec.property()
                                                                                                .name("envenMoreDeeplyNester")
                                                                                                .hints(set("property:raw(enven-more-deeply-nester)"))
                                                                                                .type(PropertyTypeSpec.type()
                                                                                                        .cardinality(PropertyCardinality.SINGLE)
                                                                                                        .typeKind(TypeKind.EMBEDDED)
                                                                                                        .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                                                                                                .addProperty(PropertySpec.property()
                                                                                                                        .name("eMDNS")
                                                                                                                        .hints(set("property:raw(e-m-d-n-s)"))
                                                                                                                        .type(PropertyTypeSpec.type()
                                                                                                                                .cardinality(PropertyCardinality.SINGLE)
                                                                                                                                .typeKind(TypeKind.JAVA_TYPE)
                                                                                                                                .typeRef(String.class.getName())
                                                                                                                        ))
                                                                                                        )
                                                                                                ))
                                                                                    )
                                                                            ))
                                                            )
                                                        )
                                                )
                                        )
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("nestedArray")
                                .hints(set("property:raw(nestedArray)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.LIST)
                                        .typeKind(TypeKind.EMBEDDED)
                                        .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                                .addProperty(PropertySpec.property()
                                                        .name("stringProp")
                                                        .hints(set("property:raw(stringProp)"))
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


    @Test
    public void referencesType() throws Exception {
        assertThat(
                this.spec.valueSpec("ReferencesType"),
                is(ValueSpec.valueSpec().name("ReferencesType")
                        .addProperty(PropertySpec.property()
                                .name("reference")
                                .hints(set("property:raw(reference)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                                        .typeRef("SimplePropertyType")
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("typeReference")
                                .hints(set("property:raw(typeReference)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                                        .typeRef("SimplePropertyType")
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("referenceArray")
                                .hints(set("property:raw(referenceArray)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.LIST)
                                        .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                                        .typeRef("SimplePropertyType")
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("typeReferenceArray")
                                .hints(set("property:raw(typeReferenceArray)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.LIST)
                                        .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                                        .typeRef("SimplePropertyType")
                                )
                        )
                        .build())
        );
    }

    @Test
    public void rawPropertyNameTypes() throws Exception {
        assertThat(
                this.spec.valueSpec("RawPropertyNameType"),
                is(ValueSpec.valueSpec().name("RawPropertyNameType")
                        .addProperty(PropertySpec.property()
                                .name("rawPropertyName")
                                .hints(set("property:raw(Raw Property Name)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(String.class.getName())
                                ))
                        .build()
                )
        );
    }

    @Test
    public void typeWithValueObjectHint() throws Exception {
        assertThat(
                this.spec.valueSpec("TypeWithHint"),
                is(ValueSpec.valueSpec().name("TypeWithHint")
                        .addProperty(PropertySpec.property()
                                .name("annotated")
                                .hints(set(
                                        "mongo:object-id",
                                        "mongo:field(_id)",
                                        "property:raw(annotated)"
                                ))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(String.class.getName())
                                ))
                        .addProperty(PropertySpec.property()
                                .name("propertyRawAnnotated")
                                .hints(set(
                                    "property:raw(funky-name)"
                                ))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.JAVA_TYPE)
                                        .typeRef(String.class.getName())
                                )
                                .build())
                        .build()
                )
        );
    }

    @Test
    public void typeWithProtocol() throws Exception {
        assertThat(
                this.spec.valueSpec("TypeWithProtocol"),
                is(ValueSpec.valueSpec().name("TypeWithProtocol")
                        .addConformsTo(Serializable.class.getCanonicalName())
                        .addBuilderConformsTo(Serializable.class.getCanonicalName())
                        .addBuilderConformsToParametrized(ParametrizedInterface.class.getCanonicalName())
                        .build()
                )
        );
    }


    @Test
    public void typeWithObjectProperty() throws Exception {
        assertThat(
                this.spec.valueSpec("TypeWithObjectProperty"),
                is(ValueSpec.valueSpec().name("TypeWithObjectProperty")
                        .addProperty(PropertySpec.property()
                                .name("obj")
                                .hints(set("property:raw(obj)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                        .typeRef(ObjectValue.class.getName())
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("objVal")
                                .hints(set("property:raw(objVal)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.SINGLE)
                                        .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                        .typeRef(Val.class.getName())
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("objs")
                                .hints(set("property:raw(objs)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.LIST)
                                        .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                        .typeRef(ObjectValue.class.getName())
                                )
                        )
                        .addProperty(PropertySpec.property()
                                .name("objVals")
                                .hints(set("property:raw(objVals)"))
                                .type(PropertyTypeSpec.type()
                                        .cardinality(PropertyCardinality.LIST)
                                        .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                        .typeRef(Val.class.getName())
                                )
                        )
                        .build())
        );
    }

    @Test
    public void alreadyDefinedType() {
        assertThat(
                this.spec.valueSpec("AlreadyDefinedType"),
                is(nullValue())
        );
    }

    @Test
    public void typeWithAlreadyDefinedProperty() {
        assertThat(
                this.spec.valueSpec("TypeWithAlreadyDefinedProperty").propertySpec("prop"),
                is(PropertySpec.property()
                        .name("prop")
                        .type(PropertyTypeSpec.type()
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .typeRef("org.codingmatters.AnAlreadyDefinedValueObject")
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .hints(new HashSet<String>(){{add("property:raw(prop)");}})
                        .build())
        );

        assertThat(
                this.spec.valueSpec("TypeWithAlreadyDefinedProperty").propertySpec("deeper").typeSpec().embeddedValueSpec().propertySpec("prop"),
                is(PropertySpec.property()
                        .name("prop")
                        .type(PropertyTypeSpec.type()
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .typeRef("org.codingmatters.AnAlreadyDefinedValueObject")
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .hints(new HashSet<String>(){{add("property:raw(prop)");}})
                        .build())
        );
    }

    @Test
    public void typeWithAlreadyDefinedEnumProperty() {
        assertThat(
                this.spec.valueSpec("TypeWithAlreadyDefinedEnumProperty").propertySpec("prop"),
                is(PropertySpec.property()
                        .name("prop")
                        .type(PropertyTypeSpec.type()
                                .typeKind(TypeKind.ENUM)
                                .typeRef("java.time.DayOfWeek")
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .hints(new HashSet<String>(){{add("property:raw(prop)");}})
                        .build())
        );

        assertThat(
                this.spec.valueSpec("TypeWithAlreadyDefinedEnumProperty").propertySpec("deeper").typeSpec().embeddedValueSpec().propertySpec("prop"),
                is(PropertySpec.property()
                        .name("prop")
                        .type(PropertyTypeSpec.type()
                                .typeKind(TypeKind.ENUM)
                                .typeRef("java.time.DayOfWeek")
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .hints(new HashSet<String>(){{add("property:raw(prop)");}})
                        .build())
        );
    }

    @Test
    public void typeWithDeeplyNestedAlreadyDefinedProperty() {
        assertThat(
                this.spec.valueSpec("TypeWithAlreadyDefinedProperty").propertySpec("deeper").typeSpec().embeddedValueSpec().propertySpec("prop"),
                is(PropertySpec.property()
                        .name("prop")
                        .type(PropertyTypeSpec.type()
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .typeRef("org.codingmatters.AnAlreadyDefinedValueObject")
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .hints(new HashSet<String>(){{add("property:raw(prop)");}})
                        .build())
        );
    }

    @Test
    public void typeWithAlreadyDefinedArrayProperty() {
        assertThat(
                this.spec.valueSpec("TypeWithAlreadyDefinedProperty").propertySpec("props"),
                is(PropertySpec.property()
                        .name("props")
                        .type(PropertyTypeSpec.type()
                                .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT)
                                .typeRef("org.codingmatters.AnAlreadyDefinedValueObject")
                                .cardinality(PropertyCardinality.LIST)
                        )
                        .hints(new HashSet<String>(){{add("property:raw(props)");}})
                        .build())
        );
    }

    @Test
    public void specialChars() {
        assertThat(
                this.spec.valueSpec("SpecialChars").propertySpec("entityContainer"),
                is(PropertySpec.property()
                        .name("entityContainer")
                        .type(PropertyTypeSpec.type()
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(String.class.getName())
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .hints(new HashSet<String>(){{add("property:raw($EntityContainer)");}})
                        .build())
        );
        assertThat(
                this.spec.valueSpec("SpecialChars").propertySpec("odataCount"),
                is(PropertySpec.property()
                        .name("odataCount")
                        .type(PropertyTypeSpec.type()
                                .typeKind(TypeKind.JAVA_TYPE)
                                .typeRef(Long.class.getName())
                                .cardinality(PropertyCardinality.SINGLE)
                        )
                        .hints(new HashSet<String>(){{add("property:raw(@odata.count)");}})
                        .build())
        );
    }



    private Set<String> set(String ... values) {
        return values != null ? new HashSet<String>(Arrays.asList(values)) : new HashSet<>();
    }
}
