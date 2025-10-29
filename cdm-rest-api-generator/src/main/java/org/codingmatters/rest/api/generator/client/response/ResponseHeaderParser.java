package org.codingmatters.rest.api.generator.client.response;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.generator.client.ResourceNaming;
import org.codingmatters.rest.api.generator.utils.Parameter;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

public class ResponseHeaderParser {
    private final ResourceNaming naming;
    private final List<TypeDeclaration> headers;

    public ResponseHeaderParser(ResourceNaming naming, List<TypeDeclaration> headers) {
        this.naming = naming;
        this.headers = headers;
    }

    public void addStatements(MethodSpec.Builder caller) {
        for (TypeDeclaration headerType : this.headers) {
            if (headerType instanceof ArrayTypeDeclaration) {
                this.addArrayParamsStatement(caller, headerType);
            } else {
                this.addSingleParamStatement(caller, headerType);
            }
        }
    }

    private void addArrayParamsStatement(MethodSpec.Builder caller, TypeDeclaration headerType) {
        Parameter headerParam = new Parameter(this.naming, headerType);
        if (!headerParam.isSupportedType()) {
            throw new AssertionError("unsupported type for response header : " + headerParam);
        }

        caller.beginControlFlow("if (response.header($S) != null)", headerType.name());
        caller.addStatement("$T<$T> values = new $T<>()", List.class, headerParam.javaType(), LinkedList.class);
        caller.beginControlFlow("for (int i = 0; i < response.header($S).length; i++)", headerType.name());
        caller.addStatement("$T rawValue = response.header($S)[i]", String.class, headerType.name());
        caller.beginControlFlow("if (rawValue != null)");
        this.addValueAssignerStatement(caller, headerParam);
        caller.addStatement("values.add(value)");
        caller.endControlFlow();
        caller.addStatement("responseBuilder.$L(values)", this.naming.property(headerType.name()));
        caller.endControlFlow();
        caller.endControlFlow();
    }

    public void addSingleParamStatement(MethodSpec.Builder caller, TypeDeclaration headerType) {
        Parameter headerParam = new Parameter(this.naming, headerType);
        if (!headerParam.isSupportedType())
            throw new AssertionError("unsupported type for response header : " + headerParam);

        caller.beginControlFlow("if (response.header($S) != null)", headerType.name());
        caller.addStatement("$T rawValue = response.header($S)[0]", String.class, headerType.name());
        caller.beginControlFlow("if (rawValue != null)");
        this.addValueAssignerStatement(caller, headerParam);
        caller.addStatement("responseBuilder.$L(value)", this.naming.property(headerType.name()));
        caller.endControlFlow();
        caller.endControlFlow();
    }

    private void addValueAssignerStatement(MethodSpec.Builder caller, Parameter headerParam) {
        if (String.class.equals(headerParam.javaType())) {
            caller.addStatement("$T value = rawValue", headerParam.javaType());
        } else if (Long.class.equals(headerParam.javaType())) {
            caller.addStatement("$T value = $T.valueOf(rawValue)", headerParam.javaType(), headerParam.javaType());
        } else if (Double.class.equals(headerParam.javaType())) {
            caller.addStatement("$T value = $T.valueOf(rawValue)", headerParam.javaType(), headerParam.javaType());
        } else if (LocalDateTime.class.equals(headerParam.javaType())) {
            caller.addStatement("$T value = $T.parse(rawValue, $T.Formatters.DATETIMEONLY.formatter)", headerParam.javaType(), headerParam.javaType(), Requester.class);
        } else if (LocalDate.class.equals(headerParam.javaType())) {
            caller.addStatement("$T value = $T.parse(rawValue, $T.Formatters.DATEONLY.formatter)", headerParam.javaType(), headerParam.javaType(), Requester.class);
        } else if (LocalTime.class.equals(headerParam.javaType())) {
            caller.addStatement("$T value = $T.parse(rawValue, $T.Formatters.TIMEONLY.formatter)", headerParam.javaType(), headerParam.javaType(), Requester.class);
        } else if (Boolean.class.equals(headerParam.javaType())) {
            caller.addStatement("$T value = $T.valueOf(rawValue)", headerParam.javaType(), headerParam.javaType());
        }
    }

}
