package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.ResponseDelegate;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;

import javax.lang.model.element.Modifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RequesterCaller {
    private final String typesPackage;
    private final ResourceNaming naming;
    private final Method method;

    public RequesterCaller(String typesPackage, ResourceNaming naming, Method method) {
        this.typesPackage = typesPackage;
        this.naming = naming;
        this.method = method;
    }

    public MethodSpec caller() {
        MethodSpec.Builder caller = MethodSpec.methodBuilder(this.callerName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(this.requestType(), "request")
                .returns(this.responseType())
                .addException(IOException.class);

        caller.addStatement(
                "$T requester = this.requesterFactory\n" +
                        ".forBaseUrl(this.baseUrl)\n" +
                        ".path($S)",
                Requester.class, method.resource().resourcePath());

        this.prepareParameters(caller);
        this.makeRequest(caller);

        caller.addStatement("$T.Builder resp = $T.builder()",
                responseType(),
                responseType());
        caller.addStatement("return resp.build()");

        return caller.build();
    }

    private void prepareParameters(MethodSpec.Builder caller) {
        for (TypeDeclaration param : this.method.queryParameters()) {
            caller
                    .beginControlFlow("if(request.$L() != null)", this.naming.property(param.name()))
                        .addStatement("requester.parameter($S, request.$L())", param.name(), this.naming.property(param.name()))
                    .endControlFlow()
            ;
        }

    }

    private void makeRequest(MethodSpec.Builder caller) {
        if(this.method.method().equals("get") || this.method.method().equals("delete")) {
            caller.addStatement("$T response = requester.$L()", ResponseDelegate.class, this.method.method());
        } else {
            TypeDeclaration body = this.method.body().get(0);

            caller.addStatement("byte[] requestBody = new byte[0]");
            caller.beginControlFlow("if(request.payload() != null)");
            caller.beginControlFlow("try($T out = new $T())", ByteArrayOutputStream.class, ByteArrayOutputStream.class);
            caller.beginControlFlow("try($T generator = this.jsonFactory.createGenerator(out))", JsonGenerator.class);

            if(body instanceof ArrayTypeDeclaration) {
                // TODO replace with list writer
                String elementType = ((ArrayTypeDeclaration) body).items().name();
                caller.addStatement("generator.writeStartArray()");
                caller.beginControlFlow("for ($T element : request.payload())", ClassName.get(this.typesPackage, this.naming.type(elementType)))
                        .beginControlFlow("if(element != null)")
                        .addStatement("new $T().write(generator, element)", ClassName.get(this.typesPackage + ".json", this.naming.type(elementType, "Writer")))
                        .nextControlFlow("else")
                        .addStatement("generator.writeNull()")
                        .endControlFlow()
                        .endControlFlow();
                caller.addStatement("generator.writeEndArray()");
            } else {
                caller.addStatement(
                        "new $T().write(generator, request.payload())",
                        ClassName.get(this.typesPackage + ".json", this.naming.type(body.type(), "Writer"))
                );
            }
            caller.endControlFlow();
            caller.addStatement("requestBody = out.toByteArray()");
            caller.endControlFlow();
            caller.endControlFlow();
            caller.addStatement("$T response = requester.$L($S, requestBody)", ResponseDelegate.class, this.method.method(), "application/json");
        }
    }

    private String callerName() {
        return this.naming.property(this.method.method());
    }

    private ClassName requestType() {
        return this.naming.methodRequestType(this.method);
    }

    private ClassName responseType() {
        return this.naming.methodResponseType(this.method);
    }
}
