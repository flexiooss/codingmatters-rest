package org.codingmatters.rest.api.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.rest.api.generator.client.ClientNamingHelper;
import org.codingmatters.rest.api.generator.client.ResourceNaming;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
        ClassName clientInterface = ClassName.get(this.clientPackage, ClientNamingHelper.interfaceName(this.naming, model));
        TypeSpec clientClass = this.clientClass(clientInterface, model);

        writeJavaFile(
                packageDir(this.dir, this.clientPackage),
                this.clientPackage,
                clientClass);
    }

    private TypeSpec clientClass(ClassName clientInterface, RamlModelResult model) {
        TypeSpec.Builder clientClass = TypeSpec.classBuilder(ClientNamingHelper.handlersClassName(this.naming, model))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(clientInterface)
                ;

        this.addFieldsAndConstructor(model, clientClass);
        clientClass.addMethods(this.resourceMethods(model.getApiV10().resources(), clientInterface));

        clientClass.addTypes(this.resourceTypes(model.getApiV10().resources(), clientInterface));


        return clientClass.build();
    }

    private void addFieldsAndConstructor(RamlModelResult model, TypeSpec.Builder clientClass) {
        ClassName handlersType = ClassName.get(this.apiPackage, this.naming.type(model.getApiV10().title().value(), "Handlers"));
        clientClass.addField(handlersType, "handlers", Modifier.FINAL, Modifier.PRIVATE);

        clientClass.addMethod(MethodSpec.constructorBuilder()
                .addAnnotation(Deprecated.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(handlersType, "handlers")
                .addParameter(Object.class, "executor")
                .addStatement("this.handlers = handlers")
                .build());

        clientClass.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(handlersType, "handlers")
                .addStatement("this.handlers = handlers")
                .build());
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
                .addStatement("return handlers.$L().apply(request)", this.naming.property(method.resource().displayName().value(), method.method(), "Handler"))
                .build());

        results.add(MethodSpec.methodBuilder(this.naming.property(method.method()))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Consumer.class), requestTypeName.nestedClass("Builder")), "request")
                .returns(responseTypeName)
                .addException(IOException.class)
                .addStatement("$T.Builder builder = $T.builder()", requestTypeName, requestTypeName)
                .addStatement("request.accept(builder)")
                .addStatement("return handlers.$L().apply(builder.build())", this.naming.property(method.resource().displayName().value(), method.method(), "Handler"))
                .build());

        return results;
    }
}
