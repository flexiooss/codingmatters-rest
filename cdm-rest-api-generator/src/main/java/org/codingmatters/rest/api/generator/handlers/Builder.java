package org.codingmatters.rest.api.generator.handlers;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nelt on 5/17/17.
 */
public class Builder {

    private final String typesPackage;
    private final String apiPackage;
    private final Naming naming;
    private final HandlersHelper helper;

    public Builder(String typesPackage, String apiPackage, Naming naming, HandlersHelper helper) {
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
        this.naming = naming;
        this.helper = helper;
    }

    public TypeSpec type(RamlModelResult ramlModel) {
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
                .returns(ClassName.bestGuess(this.helper.handlersInterfaceName(ramlModel)))
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

            result.addField(this.helper.handlerFunctionType(resourceName, methodName), this.naming.property(resourceName, methodName, "Handler"));

            result.addMethod(MethodSpec.methodBuilder(this.naming.property(resourceName, methodName, "Handler"))
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(this.helper.handlerFunctionType(resourceName, methodName), "handler")
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
        return new DefaultImplementation(this.typesPackage, this.apiPackage, this.naming, helper).type(ramlModel);
    }
}
