package org.codingmatters.rest.api.generator.handlers;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.RamlModelResult;

import java.util.function.Function;

/**
 * Created by nelt on 5/17/17.
 */
public class HandlersHelper {
    private final String apiPackage;
    private final Naming naming;

    public HandlersHelper(String apiPackage, Naming naming) {
        this.apiPackage = apiPackage;
        this.naming = naming;
    }

    public String handlersInterfaceName(RamlModelResult ramlModel) {
        return this.naming.type(ramlModel.getApiV10().title().value(), "Handlers");
    }


    public ParameterizedTypeName handlerFunctionType(String resourceName, String methodName) {
        return ParameterizedTypeName.get(
                ClassName.get(Function.class),
                ClassName.get(this.apiPackage, this.naming.type(resourceName, methodName, "Request")),
                ClassName.get(this.apiPackage, this.naming.type(resourceName, methodName, "Response"))
        );
    }
}
