package org.codingmatters.rest.api.generator;

import com.fasterxml.jackson.core.JsonFactory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.codingmatters.value.objects.generation.GenerationUtils.packageDir;
import static org.codingmatters.value.objects.generation.GenerationUtils.writeJavaFile;

public class ClientRequesterImplementation {
    private final String clientPackage;
    private final String apiPackage;
    private final File dir;

    private final Naming naming = new Naming();

    public ClientRequesterImplementation(String clientPackage, String apiPackage, File dir) {
        this.clientPackage = clientPackage;
        this.apiPackage = apiPackage;
        this.dir = dir;
    }

    public void generate(RamlModelResult model) throws IOException {
        ClassName clientInterface = ClassName.get(this.clientPackage, this.naming.type(model.getApiV10().title().value(), "Client"));
        TypeSpec clientClass = this.clientClass(clientInterface, model);
        writeJavaFile(
                packageDir(this.dir, this.clientPackage),
                this.clientPackage,
                clientClass);

        List<TypeSpec> resources = this.resourceClasses(clientInterface, model.getApiV10().resources());
        for (TypeSpec resource : resources) {
            writeJavaFile(
                    packageDir(this.dir, this.clientPackage),
                    this.resourcePackage(),
                    resource);
        }

    }

    private String resourcePackage() {
        return this.clientPackage + ".resources";
    }

    private TypeSpec clientClass(ClassName clientInterface, RamlModelResult model) {
        TypeSpec.Builder result = TypeSpec.classBuilder(this.naming.type(model.getApiV10().title().value() , "RequesterClient"))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(clientInterface)
                ;

        this.addResourceConstructor(result);
        this.addChildResourcesMethods(clientInterface, model.getApiV10().resources(), result);


        return result.build();
    }


    private List<TypeSpec> resourceClasses(ClassName parentInterface, List<Resource> resources) {
        List<TypeSpec> results = new LinkedList<>();
        for (Resource resource : resources) {
            ClassName clientInterface = parentInterface.nestedClass(this.naming.type(resource.displayName().value()));
            TypeSpec resourceClass = this.resourceClass(clientInterface, resource);
            results.add(resourceClass);
            results.addAll(this.resourceClasses(clientInterface, resource.resources()));
        }

        return results;
    }

    private TypeSpec resourceClass(ClassName clientInterface, Resource resource) {
        TypeSpec.Builder result = TypeSpec.classBuilder(this.naming.type(resource.displayName().value(), "Client"))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(clientInterface)
                ;
        this.addResourceConstructor(result);

        String resourceTypeName = this.naming.type(resource.displayName().value());
        for (Method method : resource.methods()) {
            ClassName requestTypeName = ClassName.get(this.apiPackage, this.naming.type(resourceTypeName, method.method(), "Request"));
            ClassName responseTypeName = ClassName.get(this.apiPackage, this.naming.type(resourceTypeName, method.method(), "Response"));
            result.addMethod(MethodSpec.methodBuilder(this.naming.property(method.method()))
                    .addModifiers( Modifier.PUBLIC)
                    .addParameter(requestTypeName, "request")
                    .returns(responseTypeName)
                    .addStatement("return null")
                    .build());
        }


        this.addChildResourcesMethods(clientInterface, resource.resources(), result);

        return result.build();
    }

    private void addChildResourcesMethods(ClassName clientInterface, List<Resource> childResources, TypeSpec.Builder result) {
        for (Resource childResource : childResources) {
            result.addMethod(MethodSpec.methodBuilder(this.naming.property(childResource.displayName().value()))
                    .addModifiers( Modifier.PUBLIC)
                    .returns(clientInterface.nestedClass(this.naming.type(childResource.displayName().value())))
                    .addStatement("return new $T(this.requesterFactory, this.jsonFactory, this.baseUrl)",
                            ClassName.get(this.resourcePackage(), this.naming.type(childResource.displayName().value(), "Client"))
                            )
                    .build());
        }
    }

    private void addResourceConstructor(TypeSpec.Builder result) {
        result
                .addField(ClassName.get(RequesterFactory.class), "requesterFactory")
                .addField(ClassName.get(JsonFactory.class), "jsonFactory")
                .addField(ClassName.get(String.class), "baseUrl");

        result.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(RequesterFactory.class), "requesterFactory")
                .addParameter(ClassName.get(JsonFactory.class), "jsonFactory")
                .addParameter(ClassName.get(String.class), "baseUrl")
                .addStatement("this.requesterFactory = requesterFactory")
                .addStatement("this.jsonFactory = jsonFactory")
                .addStatement("this.baseUrl = baseUrl")
                .build());
    }
}
