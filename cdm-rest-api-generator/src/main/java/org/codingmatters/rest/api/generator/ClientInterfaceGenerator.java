package org.codingmatters.rest.api.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.rest.api.generator.client.ResourceNaming;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static org.codingmatters.value.objects.generation.GenerationUtils.packageDir;
import static org.codingmatters.value.objects.generation.GenerationUtils.writeJavaFile;

public class ClientInterfaceGenerator {
    private final String clientPackage;
    private final String apiPackage;
    private final File dir;

    private final ResourceNaming naming;

    public ClientInterfaceGenerator(String clientPackage, String apiPackage, File dir) {
        this.clientPackage = clientPackage;
        this.apiPackage = apiPackage;
        this.dir = dir;
        this.naming = new ResourceNaming(this.apiPackage, this.clientPackage);
    }

    public void generate(RamlModelResult model) throws IOException {
        TypeSpec clientInterface = this.clientInterface(model);
        writeJavaFile(
                packageDir(this.dir, this.clientPackage),
                this.clientPackage,
                clientInterface);
    }

    private TypeSpec clientInterface(RamlModelResult model) {
        TypeSpec.Builder parent = TypeSpec.interfaceBuilder(this.naming.type(model.getApiV10().title().value() , "Client"))
                .addModifiers(Modifier.PUBLIC);

        for (Resource resource : model.getApiV10().resources()) {
            this.resourceInterface(parent, resource);
        }


        return parent.build();
    }

    private void resourceInterface(TypeSpec.Builder parent, Resource resource) {
        String resourceTypeName = this.naming.type(resource.displayName().value());
        TypeSpec.Builder resourceType = TypeSpec.interfaceBuilder(resourceTypeName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        for (Method method : resource.methods()) {
            ClassName requestTypeName = this.naming.methodRequestType(method);
            ClassName responseTypeName = this.naming.methodResponseType(method);
            resourceType.addMethod(MethodSpec.methodBuilder(this.naming.property(method.method()))
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .addParameter(requestTypeName, "request")
                    .returns(responseTypeName)
                    .addException(IOException.class)
                    .build());

            resourceType.addMethod(MethodSpec.methodBuilder(this.naming.property(method.method()))
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .addParameter(ParameterizedTypeName.get(ClassName.get(Consumer.class), requestTypeName.nestedClass("Builder")), "request")
                    .returns(responseTypeName)
                    .addException(IOException.class)
                    .build());
        }


        for (Resource childRessource : resource.resources()) {
            this.resourceInterface(resourceType, childRessource);
        }

        parent.addType(resourceType.build());
        parent.addMethod(MethodSpec.methodBuilder(this.naming.property(resource.displayName().value()))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ClassName.bestGuess(resourceTypeName))
                .build());
    }
}
