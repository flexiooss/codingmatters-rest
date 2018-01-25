package org.codingmatters.rest.api.generator.client;

import com.squareup.javapoet.MethodSpec;
import org.raml.v2.api.model.v10.methods.Method;

public class TextClientRequestBodyWriterStatement implements ClientRequestBodyWriterStatement {
    private final Method method;
    private final String typesPackage;
    private final ResourceNaming naming;

    public TextClientRequestBodyWriterStatement(Method method, String typesPackage, ResourceNaming naming) {
        this.method = method;
        this.typesPackage = typesPackage;
        this.naming = naming;
    }

    @Override
    public void append(MethodSpec.Builder caller) {
        caller.addStatement("out.write(request.payload().getBytes())");
    }

    @Override
    public void appendContentTypeVariableCreate(MethodSpec.Builder caller) {
        caller.addStatement("String contentType = $S", "text/plain");
    }
}
