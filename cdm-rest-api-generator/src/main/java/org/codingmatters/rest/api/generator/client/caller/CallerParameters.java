package org.codingmatters.rest.api.generator.client.caller;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.client.ResourceNaming;
import org.codingmatters.rest.api.generator.utils.Parameter;
import org.codingmatters.rest.api.generator.utils.Resolver;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;

public class CallerParameters {
    private final Method method;
    private final ResourceNaming naming;

    public CallerParameters(Method method, ResourceNaming naming) {
        this.method = method;
        this.naming = naming;
    }

    public void addStatement(MethodSpec.Builder caller) {
        for (TypeDeclaration param : this.method.queryParameters()) {
            new CallerParam(this.naming, param).addStatement(caller, Parameter.ParameterSource.QUERY);
        }
        for (TypeDeclaration param : this.method.headers()) {
            new CallerParam(this.naming, param).addStatement(caller, Parameter.ParameterSource.HEADERS);
        }

        caller.addStatement("String path = $S", this.method.resource().resourcePath());
        for (TypeDeclaration param : Resolver.resolvedUriParameters(this.method.resource())) {
            new CallerParam(this.naming, param).addStatement(caller, Parameter.ParameterSource.URI);
        }
        caller.addStatement("requester.path(path)");
    }
}
