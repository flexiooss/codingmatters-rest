package org.codingmatters.rest.api.generator.processors;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.generator.exception.UnsupportedMediaTypeException;
import org.codingmatters.rest.api.generator.processors.responses.ResponseParameter;
import org.codingmatters.value.objects.generation.Naming;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

            ResponseParameter parameter = new ResponseParameter(this.naming, typeDeclaration);
            parameter.appendStatements(response, method);
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
