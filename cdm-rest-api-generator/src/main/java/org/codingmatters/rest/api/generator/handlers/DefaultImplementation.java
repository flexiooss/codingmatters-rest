package org.codingmatters.rest.api.generator.handlers;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.lang.model.element.Modifier;

/**
 * Created by nelt on 5/17/17.
 */
public class DefaultImplementation {

    private final String typesPackage;
    private final String apiPackage;
    private final Naming naming;
    private final HandlersHelper helper;

    public DefaultImplementation(String typesPackage, String apiPackage, Naming naming, HandlersHelper helper) {
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
        this.naming = naming;
        this.helper = helper;
    }

    public TypeSpec type(RamlModelResult ramlModel) {
        TypeSpec.Builder result = TypeSpec.classBuilder("DefaultImpl")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addSuperinterface(ClassName.bestGuess(this.helper.handlersInterfaceName(ramlModel)));

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
                    this.helper.handlerFunctionType(resourceName, methodName),
                    this.naming.property(resourceName, methodName, "Handler"),
                    Modifier.PRIVATE, Modifier.FINAL
            );

            result.addMethod(
                    MethodSpec.methodBuilder(this.naming.property(resourceName, methodName, "Handler"))
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class)
                            .returns(this.helper.handlerFunctionType(resourceName, methodName))
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
            constructor.addParameter(this.helper.handlerFunctionType(resourceName, methodName), this.naming.property(resourceName, methodName, "Handler"));
            constructor.addStatement("this.$L = $L",
                    this.naming.property(resourceName, methodName, "Handler"),
                    this.naming.property(resourceName, methodName, "Handler")
            );
        }
        for (Resource subResource : resource.resources()) {
            this.addResourceConstructorParameterAndInitializer(constructor, subResource);
        }

    }
}
