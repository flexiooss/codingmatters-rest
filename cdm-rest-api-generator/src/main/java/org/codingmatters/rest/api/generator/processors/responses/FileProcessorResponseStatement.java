package org.codingmatters.rest.api.generator.processors.responses;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.processors.ProcessorResponseBodyWriterStatement;
import org.codingmatters.value.objects.generation.Naming;
import org.codingmatters.rest.io.Content;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

public class FileProcessorResponseStatement implements ProcessorResponseBodyWriterStatement {
    private final Response response;
    private final String typesPackage;
    private final Naming naming;
    private final TypeDeclaration body;

    public FileProcessorResponseStatement(Response response, String typesPackage, Naming naming) {
        this.response = response;
        this.typesPackage = typesPackage;
        this.naming = naming;
        this.body = this.response.body().get(0);
    }

    @Override
    public MethodSpec.Builder appendContentType(MethodSpec.Builder method) {
        return method.addStatement(
                "responseDelegate.contenType(response.status$L().opt().payload().contentType().orElse($S))",
                response.code().value(),
                body.name());
    }

    @Override
    public MethodSpec.Builder appendTo(MethodSpec.Builder method) {
        return method.addStatement(
                "responseDelegate.payload(response.status$L().opt().payload().content().orElse($T.from(new byte[0])).asStream())",
                response.code().value(), Content.class
        );
    }
}
