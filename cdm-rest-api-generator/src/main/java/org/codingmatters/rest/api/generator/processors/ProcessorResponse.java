package org.codingmatters.rest.api.generator.processors;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.exception.UnsupportedMediaTypeException;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorResponse {
    static private final Logger log = LoggerFactory.getLogger(ProcessorResponse.class);

    private final Naming naming;
    private final String typesPackage;

    private boolean needsSubstitutedMethod = false;

    public ProcessorResponse(String typesPackage, Naming naming) {
        this.typesPackage = typesPackage;
        this.naming = naming;
    }

    public boolean needsSubstitutedMethod() {
        return this.needsSubstitutedMethod;
    }

    public void append(Method resourceMethod, MethodSpec.Builder method) {

        //TODO handle response content type
        //method.addStatement("responseDelegate.contenType($S)", "application/json; charset=utf-8");

        method.beginControlFlow("if(response != null)");
        if(! resourceMethod.responses().isEmpty()) {
            for (int i = 0; i < resourceMethod.responses().size(); i++) {
                Response response = resourceMethod.responses().get(i);
                if (i == 0) {
                    method.beginControlFlow("if (response.status$L() != null)", response.code().value());
                } else {
                    method.nextControlFlow("else if (response.status$L() != null)", response.code().value());
                }
                method.addStatement("responseDelegate.status($L)", response.code().value());

                if (!response.headers().isEmpty()) {
                    this.addResponseHeadersProcessingStatements(response, method);
                }
                if (!response.body().isEmpty()) {
                    this.addResponsePayloadProcessingStatements(response, method);
                }
            }
            method.endControlFlow();
        }
        method.endControlFlow();
    }

    private void addResponseHeadersProcessingStatements(Response response, MethodSpec.Builder method) {
        for (TypeDeclaration typeDeclaration : response.headers()) {
            this.needsSubstitutedMethod = true;
            String property = this.naming.property(typeDeclaration.name());
            method.beginControlFlow(
                    "if(response.status$L().$L() != null)",
                    response.code().value(),
                    property
            );
            if(typeDeclaration.type().equalsIgnoreCase("string")) {
                method.addStatement(
                        "responseDelegate.addHeader($S, this.substituted(requestDelegate, response.status$L().$L()))",
                        typeDeclaration.name(),
                        response.code().value(),
                        property
                );
            } else if(typeDeclaration.type().equalsIgnoreCase("array")
                    && ((ArrayTypeDeclaration)typeDeclaration).items().type().equalsIgnoreCase("string")) {
                method.beginControlFlow(
                        "for($T element: response.status$L().$L())",
                        String.class,
                        response.code().value(),
                        property
                );
                method.addStatement("responseDelegate.addHeader($S, this.substituted(requestDelegate, element))", typeDeclaration.name());
                method.endControlFlow();
            } else {
                log.warn("not yet implemented : {} response header type", typeDeclaration);
            }
            method.endControlFlow();
        }
    }

    private void addResponsePayloadProcessingStatements(Response response, MethodSpec.Builder method) {
        try {
            ProcessorResponseBodyWriterStatement statement = ProcessorResponseBodyWriterStatement.from(response, this.typesPackage, this.naming);
            statement.appendContentType(method);
            statement.appendTo(method);
        } catch (UnsupportedMediaTypeException e) {
            log.error("error while processing response", e);
        }
    }

}
