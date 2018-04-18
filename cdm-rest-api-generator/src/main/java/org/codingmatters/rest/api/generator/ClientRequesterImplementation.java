package org.codingmatters.rest.api.generator;

import com.fasterxml.jackson.core.JsonFactory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.client.UrlProvider;
import org.codingmatters.rest.api.generator.client.RequesterCaller;
import org.codingmatters.rest.api.generator.client.ResourceNaming;
import org.codingmatters.rest.api.generator.utils.DeclaredTypeRegistry;
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
    private String typesPackage;
    private final File dir;

    private final ResourceNaming naming;

    public ClientRequesterImplementation(String clientPackage, String apiPackage, String typesPackage, File dir) {
        this.clientPackage = clientPackage;
        this.apiPackage = apiPackage;
        this.typesPackage = typesPackage;
        this.dir = dir;
        this.naming = new ResourceNaming(this.apiPackage, this.clientPackage);
    }

    public void generate(RamlModelResult model) throws IOException {
        DeclaredTypeRegistry.initialize(model);

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
                    this.naming.resourcePackage(),
                    resource);
        }

    }

    private TypeSpec clientClass(ClassName clientInterface, RamlModelResult model) {
        TypeSpec.Builder result = TypeSpec.classBuilder(this.naming.type(model.getApiV10().title().value() , "RequesterClient"))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(clientInterface)
                ;

        this.addResourceConstructors(result, model.getApiV10().resources());
        this.addChildResourcesMethods(clientInterface, model.getApiV10().resources(), result);


        return result.build();
    }


    private List<TypeSpec> resourceClasses(ClassName parentInterface, List<Resource> resources) {
        List<TypeSpec> results = new LinkedList<>();
        for (Resource resource : resources) {
            ClassName clientInterface = parentInterface.nestedClass(this.naming.resourceType(resource));
            TypeSpec resourceClass = this.resourceClass(clientInterface, resource);
            results.add(resourceClass);
            results.addAll(this.resourceClasses(clientInterface, resource.resources()));
        }

        return results;
    }

    private TypeSpec resourceClass(ClassName clientInterface, Resource resource) {
        TypeSpec.Builder result = TypeSpec.classBuilder(this.naming.resourceClientType(resource))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(clientInterface)
                ;
        this.addResourceConstructors(result, resource.resources());

        for (Method method : resource.methods()) {
            result.addMethods(new RequesterCaller(this.typesPackage, this.naming, method).callers());
        }

        this.addChildResourcesMethods(clientInterface, resource.resources(), result);

        return result.build();
    }

    private void addResourceConstructors(TypeSpec.Builder result, List<Resource> childResources) {
        result
                .addField(ClassName.get(RequesterFactory.class), "requesterFactory", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ClassName.get(JsonFactory.class), "jsonFactory", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ClassName.get(UrlProvider.class), "urlProvider", Modifier.PRIVATE, Modifier.FINAL);

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(RequesterFactory.class), "requesterFactory")
                .addParameter(ClassName.get(JsonFactory.class), "jsonFactory")
                .addParameter(ClassName.get(UrlProvider.class), "urlProvider")
                .addStatement("this.requesterFactory = requesterFactory")
                .addStatement("this.jsonFactory = jsonFactory")
                .addStatement("this.urlProvider = urlProvider");

        for (Resource childResource : childResources) {
            ClassName childResourceType = this.naming.resourceClientType(childResource);
            String childResourceDelegate = this.naming.resourceDelegateName(childResource);

            result.addField(FieldSpec.builder(childResourceType, childResourceDelegate)
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build());
            constructorBuilder.addStatement("this.$N = new $T(this.requesterFactory, this.jsonFactory, this.urlProvider)", childResourceDelegate, childResourceType);
        }

        result.addMethod(constructorBuilder.build());

        result.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(RequesterFactory.class), "requesterFactory")
                .addParameter(ClassName.get(JsonFactory.class), "jsonFactory")
                .addParameter(ClassName.get(String.class), "baseUrl")
                .addStatement("this(requesterFactory, jsonFactory, () -> baseUrl)")
                .build()
        );
    }


    private void addChildResourcesMethods(ClassName clientInterface, List<Resource> childResources, TypeSpec.Builder result) {
        for (Resource childResource : childResources) {
            result.addMethod(MethodSpec.methodBuilder(this.naming.property(childResource.displayName().value()))
                    .addModifiers( Modifier.PUBLIC)
                    .returns(clientInterface.nestedClass(this.naming.resourceType(childResource)))
                    .addStatement("return this.$N", this.naming.resourceDelegateName(childResource))
                    .build());
        }
    }

}
