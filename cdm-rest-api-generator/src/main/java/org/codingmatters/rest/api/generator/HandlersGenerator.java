package org.codingmatters.rest.api.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static org.codingmatters.value.objects.generation.GenerationUtils.packageDir;
import static org.codingmatters.value.objects.generation.GenerationUtils.writeJavaFile;

/**
 * Created by nelt on 5/15/17.
 */
public class HandlersGenerator {

    private final String serverPackage;
    private final String typesPackage;
    private final String apiPackage;
    private final Naming naming = new Naming();
    private final File rootDirectory;

    public HandlersGenerator(String serverPackage, String typesPackage, String apiPackage, File toDirectory) {
        this.serverPackage = serverPackage;
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
        this.rootDirectory = toDirectory;
    }

    public void generate(RamlModelResult ramlModel) throws IOException {
        TypeSpec handlersInterface = this.createHandlersInterface(ramlModel)
                .build();

        writeJavaFile(
                packageDir(this.rootDirectory, this.serverPackage),
                this.serverPackage,
                handlersInterface);
    }

    private TypeSpec.Builder createHandlersInterface(RamlModelResult ramlModel) {
        TypeSpec.Builder result = TypeSpec.interfaceBuilder(this.handlersInterfaceName(ramlModel))
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC);

        for (Resource resource : ramlModel.getApiV10().resources()) {
            this.processResourceForInterface(result, resource);
        }

        result.addType(this.createHandlersBuilder(ramlModel));
        return result;
    }

    private void processResourceForInterface(TypeSpec.Builder result, Resource resource) {
        String resourceName = resource.displayName().value();
        for (Method method : resource.methods()) {
            String methodName = method.method();
            result.addMethod(MethodSpec.methodBuilder(this.naming.property(resourceName, methodName, "Handler"))
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .returns(this.handlerFunctionType(resourceName, methodName))
                    .build());
        }
        for (Resource subResource : resource.resources()) {
            this.processResourceForInterface(result, subResource);
        }

    }

    private ParameterizedTypeName handlerFunctionType(String resourceName, String methodName) {
        return ParameterizedTypeName.get(
                ClassName.get(Function.class),
                ClassName.get(this.apiPackage, this.naming.type(resourceName, methodName, "Request")),
                ClassName.get(this.apiPackage, this.naming.type(resourceName, methodName, "Response"))
                );
    }

    private TypeSpec createHandlersBuilder(RamlModelResult ramlModel) {
        TypeSpec.Builder result = TypeSpec.classBuilder("Builder")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                ;

        for (Resource resource : ramlModel.getApiV10().resources()) {
            this.processResourceForBuilder(result, resource);
        }

        Object[] constructorParameters = this.creatConstructorParameters(ramlModel.getApiV10().resources());
        String constructorCallFormat = this.createConstructorCallFormat(constructorParameters);
        result.addMethod(MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.bestGuess(this.handlersInterfaceName(ramlModel)))
                .addStatement(constructorCallFormat, constructorParameters)
                .build());

        result.addType(this.createDefaultImplementation(ramlModel));

        return result.build();
    }

    private String createConstructorCallFormat(Object [] parameters) {
        StringBuilder builder = new StringBuilder("return new DefaultImpl(");
        for (int i = 0; i < parameters.length; i++) {
            if(i != 0) {
                builder.append(", ");
            }
            builder.append("this.$L");
        }
        return builder.append(")").toString();
    }

    private Object[] creatConstructorParameters(List<Resource> resources) {
        LinkedList<String> result = new LinkedList<>();
        for (Resource resource : resources) {
            this.appendConstructorCallParameter(result, resource);
        }
        return result.toArray();
    }

    private void appendConstructorCallParameter(LinkedList<String> result, Resource resource) {
        for (Method method : resource.methods()) {
            result.add(this.naming.property(resource.displayName().value(), method.method(), "Handler"));
        }
        for (Resource subResource : resource.resources()) {
            this.appendConstructorCallParameter(result, subResource);
        }
    }

    private void processResourceForBuilder(TypeSpec.Builder result, Resource resource) {
        String resourceName = resource.displayName().value();
        for (Method method : resource.methods()) {
            String methodName = method.method();

            result.addField(this.handlerFunctionType(resourceName, methodName), this.naming.property(resourceName, methodName, "Handler"));

            result.addMethod(MethodSpec.methodBuilder(this.naming.property(resourceName, methodName, "Handler"))
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(this.handlerFunctionType(resourceName, methodName), "handler")
                    .returns(ClassName.bestGuess("Builder"))
                    .addStatement("this.$L = $L", this.naming.property(resourceName, methodName, "Handler"), "handler")
                    .addStatement("return this")
                    .build());
        }

        for (Resource subResource : resource.resources()) {
            this.processResourceForBuilder(result, subResource);
        }

    }


    private TypeSpec createDefaultImplementation(RamlModelResult ramlModel) {
        TypeSpec.Builder result = TypeSpec.classBuilder("DefaultImpl")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addSuperinterface(ClassName.bestGuess(this.handlersInterfaceName(ramlModel)));

        result.addMethod(this.createDefaultImplementationConctructor(ramlModel).build());

        for (Resource resource : ramlModel.getApiV10().resources()) {
            this.processResourceForDefaultImplementation(result, resource);
        }

        return result.build();
    }

    private void processResourceForDefaultImplementation(TypeSpec.Builder result, Resource resource) {
        String resourceName = resource.displayName().value();
        for (Method method : resource.methods()) {
            String methodName = method.method();

            result.addField(
                    this.handlerFunctionType(resourceName, methodName),
                    this.naming.property(resourceName, methodName, "Handler"),
                    Modifier.PRIVATE, Modifier.FINAL
            );

            result.addMethod(
                    MethodSpec.methodBuilder(this.naming.property(resourceName, methodName, "Handler"))
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class)
                            .returns(this.handlerFunctionType(resourceName, methodName))
                            .addStatement("return this.$L", this.naming.property(resourceName, methodName, "Handler"))
                            .build()
            );
        }

        for (Resource subResource : resource.resources()) {
            this.processResourceForDefaultImplementation(result, subResource);
        }
    }

    private MethodSpec.Builder createDefaultImplementationConctructor(RamlModelResult ramlModel) {
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);
        for (Resource resource : ramlModel.getApiV10().resources()) {
            this.addResourceConstructorParameterAndInitializer(constructor, resource);
        }
        return constructor;
    }

    private void addResourceConstructorParameterAndInitializer(MethodSpec.Builder constructor, Resource resource) {
        String resourceName = resource.displayName().value();
        for (Method method : resource.methods()) {
            String methodName = method.method();
            constructor.addParameter(this.handlerFunctionType(resourceName, methodName), this.naming.property(resourceName, methodName, "Handler"));
            constructor.addStatement("this.$L = $L",
                    this.naming.property(resourceName, methodName, "Handler"),
                    this.naming.property(resourceName, methodName, "Handler")
            );
        }
        for (Resource subResource : resource.resources()) {
            this.addResourceConstructorParameterAndInitializer(constructor, subResource);
        }

    }

    private String handlersInterfaceName(RamlModelResult ramlModel) {
        return this.naming.type(ramlModel.getApiV10().title().value(), "Handlers");
    }

}
