package org.codingmatters.rest.api.generator;

import com.squareup.javapoet.*;
import org.codingmatters.rest.api.generator.client.ResourceNaming;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static org.codingmatters.value.objects.generation.GenerationUtils.packageDir;
import static org.codingmatters.value.objects.generation.GenerationUtils.writeJavaFile;

public class ClientHandlerImplementation {
    private final String clientPackage;
    private final String apiPackage;
    private String typesPackage;
    private final File dir;

    private final ResourceNaming naming;

    public ClientHandlerImplementation(String clientPackage, String apiPackage, String typesPackage, File dir) {
        this.clientPackage = clientPackage;
        this.apiPackage = apiPackage;
        this.typesPackage = typesPackage;
        this.dir = dir;
        this.naming = new ResourceNaming(this.apiPackage, this.clientPackage);
    }

    public void generate(RamlModelResult model) throws IOException {
        ClassName clientInterface = ClassName.get(this.clientPackage, this.naming.type(model.getApiV10().title().value(), "Client"));
        TypeSpec clientClass = this.clientClass(clientInterface, model);

        writeJavaFile(
                packageDir(this.dir, this.clientPackage),
                this.clientPackage,
                clientClass);
    }

    private TypeSpec clientClass(ClassName clientInterface, RamlModelResult model) {
        TypeSpec.Builder clientClass = TypeSpec.classBuilder(this.naming.type(model.getApiV10().title().value(), "HandlersClient"))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(clientInterface)
                ;

        this.addFieldsAndConstructor(model, clientClass);
        clientClass.addMethod(this.addCallUtilMethod());
        clientClass.addMethods(this.resourceMethods(model.getApiV10().resources(), clientInterface));

        clientClass.addTypes(this.resourceTypes(model.getApiV10().resources(), clientInterface));


        return clientClass.build();
    }

    private void addFieldsAndConstructor(RamlModelResult model, TypeSpec.Builder clientClass) {
        ClassName handlersType = ClassName.get(this.apiPackage, this.naming.type(model.getApiV10().title().value(), "Handlers"));
        clientClass.addField(handlersType, "handlers", Modifier.FINAL, Modifier.PRIVATE);
        clientClass.addField(ExecutorService.class, "executor", Modifier.FINAL, Modifier.PRIVATE);

        clientClass.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(handlersType, "handlers")
                .addParameter(ExecutorService.class, "executor")
                .addStatement("this.handlers = handlers")
                .addStatement("this.executor = executor")
                .build());
    }

    private MethodSpec addCallUtilMethod() {
        return MethodSpec.methodBuilder("call")
                .addModifiers(Modifier.PRIVATE)
                .addTypeVariable(TypeVariableName.get("T"))
                .returns(TypeVariableName.get("T"))
                .addParameter(ParameterizedTypeName.get(ClassName.get(Callable.class), TypeVariableName.get("T")), "callable")
                .addParameter(String.class, "action")
                .addException(IOException.class)
                .beginControlFlow("try")
                    .addStatement("return this.executor.submit(callable).get()")
                .nextControlFlow("catch($T | $T e)", InterruptedException.class, ExecutionException.class)
                    .addStatement("throw new $T($S + action, e)", IOException.class, "error invoking ")
                .endControlFlow()
                .build()
                ;

    }

    private Iterable<MethodSpec> resourceMethods(List<Resource> resources, ClassName parentInterface) {
        List<MethodSpec> results = new LinkedList<>();

        for (Resource resource : resources) {
            results.add(MethodSpec.methodBuilder(this.naming.property(resource.displayName().value()))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(parentInterface.nestedClass(this.naming.type(resource.displayName().value())))
                    .addStatement("return new $T()", ClassName.bestGuess(this.naming.type(resource.displayName().value(), "Impl")))
                    .build());
        }

        return results;
    }

    private Iterable<TypeSpec> resourceTypes(List<Resource> resources, ClassName parentInterface) {
        List<TypeSpec> results = new LinkedList<>();

        for (Resource resource : resources) {
            TypeSpec.Builder resourceType = TypeSpec.classBuilder(this.naming.type(resource.displayName().value(), "Impl"))
                    .addModifiers(Modifier.PRIVATE)
                    .addSuperinterface(parentInterface.nestedClass(this.naming.type(resource.displayName().value())))
                    .addMethods(this.resourceMethods(resource.resources(), parentInterface.nestedClass(this.naming.type(resource.displayName().value()))));

            for (Method method : resource.methods()) {
                resourceType.addMethods(this.methodMethods(method));
            }

            resourceType.addTypes(this.resourceTypes(resource.resources(), parentInterface.nestedClass(this.naming.type(resource.displayName().value()))));

            results.add(resourceType.build());
        }


        return results;
    }

    private Iterable<MethodSpec> methodMethods(Method method) {
        List<MethodSpec> results = new LinkedList<>();

        ClassName requestTypeName = this.naming.methodRequestType(method);
        ClassName responseTypeName = this.naming.methodResponseType(method);
        results.add(MethodSpec.methodBuilder(this.naming.property(method.method()))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(requestTypeName, "request")
                .returns(responseTypeName)
                .addException(IOException.class)
                .addStatement("return call(() -> handlers.$L().apply(request), $S)",
                        this.naming.property(method.resource().displayName().value(), method.method(), "Handler"),
                        method.resource().displayName().value() + " " + method.method()
                        )
                .build());

        results.add(MethodSpec.methodBuilder(this.naming.property(method.method()))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Consumer.class), requestTypeName.nestedClass("Builder")), "request")
                .returns(responseTypeName)
                .addException(IOException.class)
                .addStatement("return call(() -> {\n" +
                        "\t$T.Builder builder = $T.builder();\n" +
                        "\trequest.accept(builder);\n" +
                        "\treturn handlers.$L().apply(builder.build());\n" +
                        "}, $S)",
                        requestTypeName, requestTypeName,
                        this.naming.property(method.resource().displayName().value(), method.method(), "Handler"),
                        method.resource().displayName().value() + " " + method.method()
                )
                .build());

        return results;
    }
}
