package org.codingmatters.rest.api.generator.processors.responses;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.processors.ProcessorResponse;
import org.codingmatters.rest.api.generator.utils.Parameter;
import org.codingmatters.value.objects.generation.Naming;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseParameter extends Parameter {
    static private final Logger log = LoggerFactory.getLogger(ProcessorResponse.class);

    public ResponseParameter(Naming naming, TypeDeclaration typeDeclaration) {
        super(naming, typeDeclaration);
    }

    public void appendStatements(Response response, MethodSpec.Builder method) {
        String property = this.property();
        method.beginControlFlow(
                "if (response.status$L().$L() != null)",
                response.code().value(),
                property
        );
        if (this.isArray()) {
            method.beginControlFlow(
                    "for ($T ___element: response.status$L().$L())",
                    this.javaType(),
                    response.code().value(),
                    property
            );
            this.addToStringStatement(method, "___value", "___element");
            method.addStatement("responseDelegate.addHeader($S, this.substituted(requestDelegate, ___value))", this.name());
            method.endControlFlow();
        } else {
            method.addStatement("$T $L = response.status$L().$L()",
                    this.javaType(), this.property() + "___RawValue", response.code().value(), property
            );
            this.addToStringStatement(method, this.property() + "___StringValue", this.property() + "___RawValue");
            method.addStatement(
                    "responseDelegate.addHeader($S, this.substituted(requestDelegate, $L))",
                    this.name(),
                    this.property() + "___StringValue"
            );
        }
        method.endControlFlow();
    }

    private void addToStringStatement(MethodSpec.Builder method, String valueVariable, String elementVariable) {
        if (this.isOfType("string")) {
            method.addStatement("$T $L = $L != null ? $L : null", String.class, valueVariable, elementVariable, elementVariable);
        } else {
            method.addStatement("$T $L = $L != null ? $L.toString() : null", String.class, valueVariable, elementVariable, elementVariable);
        }
    }
}
