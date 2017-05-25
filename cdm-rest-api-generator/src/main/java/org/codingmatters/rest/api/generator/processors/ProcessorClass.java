package org.codingmatters.rest.api.generator.processors;

import com.fasterxml.jackson.core.JsonFactory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;
import org.codingmatters.rest.api.generator.handlers.HandlersHelper;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.RamlModelResult;

import javax.lang.model.element.Modifier;
import java.io.IOException;

/**
 * Created by nelt on 5/23/17.
 */
public class ProcessorClass {
    private final String typesPackage;
    private final String apiPackage;
    private final Naming naming;
    private final HandlersHelper helper;

    public ProcessorClass(String typesPackage, String apiPackage, Naming naming, HandlersHelper helper) {
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
        this.naming = naming;
        this.helper = helper;
    }

    public TypeSpec type(RamlModelResult ramlModel) {
        return TypeSpec.classBuilder(this.naming.type(ramlModel.getApiV10().title().value(), "Processor"))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(Processor.class))
                .addMethod(MethodSpec.methodBuilder("process")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.VOID)
                        .addParameter(ClassName.get(RequestDelegate.class), "requestDelegate")
                        .addParameter(ClassName.get(ResponseDelegate.class), "responseDelegate")
                        .addException(ClassName.get(IOException.class))
                        .build())
                .addField(ClassName.get(String.class), "apiRelativePath", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ClassName.get(JsonFactory.class), "factory", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ClassName.bestGuess(this.naming.type(ramlModel.getApiV10().title().value(), "Handlers")), "handlers", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.get(String.class), "apiRelativePath")
                        .addParameter(ClassName.get(JsonFactory.class), "factory")
                        .addParameter(ClassName.bestGuess(this.naming.type(ramlModel.getApiV10().title().value(), "Handlers")), "handlers")
                        .addStatement("this.$L = $L", "apiRelativePath", "apiRelativePath")
                        .addStatement("this.$L = $L", "factory", "factory")
                        .addStatement("this.$L = $L", "handlers", "handlers")
                        .build())
                .build();
    }
}
