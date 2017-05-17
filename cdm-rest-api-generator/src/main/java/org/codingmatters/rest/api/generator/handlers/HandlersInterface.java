package org.codingmatters.rest.api.generator.handlers;

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
public class HandlersInterface {
    private final String typesPackage;
    private final String apiPackage;
    private final Naming naming;
    private final HandlersHelper helper;

    public HandlersInterface(String typesPackage, String apiPackage, Naming naming, HandlersHelper helper) {
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
        this.naming = naming;
        this.helper = helper;
    }

    public TypeSpec type(RamlModelResult ramlModel) {
        return this.createHandlersInterface(ramlModel).build();
    }

    private TypeSpec.Builder createHandlersInterface(RamlModelResult ramlModel) {
        TypeSpec.Builder result = TypeSpec.interfaceBuilder(this.helper.handlersInterfaceName(ramlModel))
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
                    .returns(this.helper.handlerFunctionType(resourceName, methodName))
                    .build());
        }
        for (Resource subResource : resource.resources()) {
            this.processResourceForInterface(result, subResource);
        }

    }

    private TypeSpec createHandlersBuilder(RamlModelResult ramlModel) {
        return new Builder(this.typesPackage, this.apiPackage, this.naming, helper).type(ramlModel);
    }
}
