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
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
                .addMethod(this.buildProcessMethod(ramlModel))
                .addMethods(this.buidMethodProcessingMethods(ramlModel))
                .addField(ClassName.get(String.class), "apiPath", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ClassName.get(JsonFactory.class), "factory", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ClassName.bestGuess(this.naming.type(ramlModel.getApiV10().title().value(), "Handlers")), "handlers", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.get(String.class), "apiPath")
                        .addParameter(ClassName.get(JsonFactory.class), "factory")
                        .addParameter(ClassName.bestGuess(this.naming.type(ramlModel.getApiV10().title().value(), "Handlers")), "handlers")
                        .addStatement("this.$L = $L", "apiPath", "apiPath")
                        .addStatement("this.$L = $L", "factory", "factory")
                        .addStatement("this.$L = $L", "handlers", "handlers")
                        .build())
                .build();
    }

    private MethodSpec buildProcessMethod(RamlModelResult ramlModel) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("process")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(RequestDelegate.class), "requestDelegate")
                .addParameter(ClassName.get(ResponseDelegate.class), "responseDelegate")
                .addException(ClassName.get(IOException.class));

        method.addStatement("responseDelegate.contenType($S)", "application/json; charset=utf-8");
        this.addResourcesProcessing( method, ramlModel.getApiV10().resources());

        return method.build();
    }

    private void addResourcesProcessing(MethodSpec.Builder method, List<Resource> resources) {
        for (Resource resource : resources) {
            this.addResourceProcessing(method, resource);
            this.addResourcesProcessing(method, resource.resources());
        }
    }

    private void addResourceProcessing(MethodSpec.Builder method, Resource resource) {
        if(resource.methods().isEmpty()) return;
        //if(requestDelegate.pathMatcher("/" + this.apiPath + "/jobs/?").matches()) {
        method.beginControlFlow(
                "if(requestDelegate.pathMatcher(this.apiPath + \"$L/?\").matches())",
                resource.resourcePath()
        );
        for (Method resourceMethod : resource.methods()) {
            method.beginControlFlow(
                    "if(requestDelegate.method().equals(RequestDelegate.Method.$L))",
                    resourceMethod.method().toUpperCase()
            );
            method.addStatement("this.$L(requestDelegate, responseDelegate)", this.methodProcessingMethodName(resourceMethod));
            method.endControlFlow();
        }

        method.endControlFlow();

    }



    private Iterable<MethodSpec> buidMethodProcessingMethods(RamlModelResult ramlModel) {
        LinkedList<MethodSpec> methodSpecs = new LinkedList<>();
        this.addMethodProcessingMethods(methodSpecs, ramlModel.getApiV10().resources());
        return methodSpecs;
    }

    private void addMethodProcessingMethods(Collection<MethodSpec> methodSpecs, List<Resource> resources) {
        for (Resource resource : resources) {
            for (Method method : resource.methods()) {
                methodSpecs.add(this.buildMethodProcessingMethod(method));
            }
            this.addMethodProcessingMethods(methodSpecs, resource.resources());
        }
    }

    private MethodSpec buildMethodProcessingMethod(Method resourceMethod) {
        MethodSpec.Builder method = MethodSpec.methodBuilder(this.methodProcessingMethodName(resourceMethod))
                .addModifiers(Modifier.PRIVATE)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(RequestDelegate.class), "requestDelegate")
                .addParameter(ClassName.get(ResponseDelegate.class), "responseDelegate")
                .addException(IOException.class)
                .addStatement(
                        "$T response = this.handlers.$L().apply($T.Builder.builder().build())",
                        this.resourceMethodResponseClass(resourceMethod),
                        this.resourceMethodHandlerMethod(resourceMethod),
                        this.resourceMethodRequestClass(resourceMethod)
                );


        return method.build();
    }

    private String resourceMethodHandlerMethod(Method resourceMethod) {
        return this.naming.property(resourceMethod.resource().displayName().value(), resourceMethod.method(), "Handler");
    }

    private ClassName resourceMethodRequestClass(Method resourceMethod) {
        return ClassName.get(this.apiPackage, this.naming.type(resourceMethod.resource().displayName().value(), resourceMethod.method(), "Request"));
    }

    private ClassName resourceMethodResponseClass(Method resourceMethod) {
        return ClassName.get(this.apiPackage, this.naming.type(resourceMethod.resource().displayName().value(), resourceMethod.method(), "Response"));
    }

    private String methodProcessingMethodName(Method method) {
        return this.naming.property("process", method.resource().displayName().value(), method.method(), "Request");
    }
}
