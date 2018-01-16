package org.codingmatters.rest.api.generator.processors.responses;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.processors.ProcessorResponseBodyWriterStatement;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

public class TextProcessorResponseStatement implements ProcessorResponseBodyWriterStatement {
    private final Response response;
    private final String typesPackage;
    private final Naming naming;
    private final TypeDeclaration body;

    public TextProcessorResponseStatement(Response response, String typesPackage, Naming naming) {
        this.response = response;
        this.typesPackage = typesPackage;
        this.naming = naming;
        this.body = this.response.body().get(0);
    }

    @Override
    public MethodSpec.Builder appendTo(MethodSpec.Builder method) {
        method.beginControlFlow("if(response.status$L().payload() != null)", response.code().value());
        method.addStatement("responseDelegate.payload(response.status$L().payload(), $S)", response.code().value(), "utf-8");
        method.endControlFlow();

        return method;
    }

    @Override
    public MethodSpec.Builder appendContentType(MethodSpec.Builder method) {
        return method.addStatement("responseDelegate.contenType($S)", this.body.name() + "; charset=utf-8");
    }
}
