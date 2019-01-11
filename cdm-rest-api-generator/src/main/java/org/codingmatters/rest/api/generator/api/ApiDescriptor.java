package org.codingmatters.rest.api.generator.api;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.rest.api.generator.handlers.HandlersHelper;
import org.codingmatters.value.objects.generation.Naming;
import org.raml.v2.api.RamlModelResult;

import javax.lang.model.element.Modifier;

public class ApiDescriptor {
    private final String typesPackage;
    private final String apiPackage;
    private final Naming naming;
    private final HandlersHelper helper;

    public ApiDescriptor(String typesPackage, String apiPackage, Naming naming, HandlersHelper helper) {
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
        this.naming = naming;
        this.helper = helper;
    }

    public TypeSpec type(RamlModelResult ramlModel) {
        return this.createHandlersInterface(ramlModel).build();
    }

    private TypeSpec.Builder createHandlersInterface(RamlModelResult ramlModel) {
        TypeSpec.Builder result = TypeSpec.interfaceBuilder(this.naming.type(ramlModel.getApiV10().title().value(), "Descriptor"))
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC);

        result.addField(
                FieldSpec.builder(
                        String.class,
                        "NAME",
                        Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
                        .initializer("$S", ramlModel.getApiV10().title().value().toLowerCase().replaceAll("\\s+", "-"))
                        .build()

        );

        return result;
    }
}
