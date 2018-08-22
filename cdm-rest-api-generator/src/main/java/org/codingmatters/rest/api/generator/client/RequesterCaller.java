package org.codingmatters.rest.api.generator.client;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.ResponseDelegate;
import org.codingmatters.rest.api.generator.client.caller.CallerParameters;
import org.codingmatters.rest.api.generator.exception.UnsupportedMediaTypeException;
import org.codingmatters.rest.api.generator.type.SupportedMediaType;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class RequesterCaller {
    static private Logger log = LoggerFactory.getLogger(RequesterCaller.class);

    private final String typesPackage;
    private final ResourceNaming naming;
    private final Method method;

    public RequesterCaller(String typesPackage, ResourceNaming naming, Method method) {
        this.typesPackage = typesPackage;
        this.naming = naming;
        this.method = method;
    }

    public List<MethodSpec> callers() {
        return Arrays.asList(
                this.baseCaller().build(),
                this.consumerCaller().build()
        );
    }

    private MethodSpec.Builder baseCaller() {
        MethodSpec.Builder caller = MethodSpec.methodBuilder(this.callerName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(this.requestType(), "request")
                .returns(this.responseType())
                .addException(IOException.class);

        caller.addStatement(
                "$T requester = this.requesterFactory.forBaseUrl(this.urlProvider.baseUrl())",
                Requester.class);

        new CallerParameters(this.method, naming).addStatement(caller);

        caller.addStatement("$T response = null", ResponseDelegate.class);
        caller.beginControlFlow("try");
        this.makeRequest(caller);

        caller.addStatement("$T.Builder resp = $T.builder()",
                responseType(),
                responseType());

        this.parseResponse(caller);

        caller.addStatement("return resp.build()");

        caller
                .nextControlFlow("finally")
                    .beginControlFlow("try")
                        .beginControlFlow("if(response != null)")
                            .addStatement("response.close()")
                        .endControlFlow()
                    .nextControlFlow( "catch($T e)", Exception.class)
                        .addStatement("throw new $T($S, e)", IOException.class, "error closing response")
                    .endControlFlow()
                .endControlFlow();

        return caller;
    }

    private MethodSpec.Builder consumerCaller() {

        return MethodSpec.methodBuilder(this.naming.property(method.method()))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Consumer.class), this.requestType().nestedClass("Builder")), "request")
                .returns(this.responseType())
                .addException(IOException.class)
                .addStatement("$T builder = $T.builder()", this.requestType().nestedClass("Builder"), this.requestType())
                .beginControlFlow("if(request != null)")
                    .addStatement("request.accept(builder)")
                    .endControlFlow()
                .addStatement("return this.$L(builder.build())", this.callerName())
                ;
    }

    private void makeRequest(MethodSpec.Builder caller) {
        if(this.method.method().equals("get") || this.method.method().equals("delete")|| this.method.method().equals("head")) {
            caller.addStatement("response = requester.$L()", this.method.method());
        } else if(this.method.body().isEmpty()) {
            caller.addStatement("response = requester.$L($S, new byte[0])", this.method.method(), "application/json");
        } else {
            TypeDeclaration body = this.method.body().get(0);

            caller.addStatement("byte[] requestBody = new byte[0]");
            caller.beginControlFlow("if(request.payload() != null)");
            caller.beginControlFlow("try($T out = new $T())", ByteArrayOutputStream.class, ByteArrayOutputStream.class);

            SupportedMediaType mediaType;
            try {
                mediaType = SupportedMediaType.from(this.method);
            } catch (UnsupportedMediaTypeException e) {
                log.error("unsupported request media type : " + this.method.body().get(0).type());
                return ;
            }

            ClientRequestBodyWriterStatement writerStatement = mediaType.clientBodyWriterStatement(this.method, this.typesPackage, this.naming);
            writerStatement.append(caller);

            caller.addStatement("requestBody = out.toByteArray()");
            caller.endControlFlow();
            caller.endControlFlow();

            writerStatement.appendContentTypeVariableCreate(caller);
            caller.addStatement("response = requester.$L(contentType, requestBody)",
                    this.method.method()
            );
        }
    }

    private void parseResponse(MethodSpec.Builder caller) {
        for (Response response : this.method.responses()) {
            caller.beginControlFlow("if(response.code() == $L)", response.code().value());

            caller.addStatement("$T.Builder responseBuilder = $T.builder()",
                    this.naming.methodResponseStatusType(this.method, response.code()),
                    this.naming.methodResponseStatusType(this.method, response.code())
            );

            this.parseHeaders(caller, response);
            this.parseBody(caller, response);

            caller.addStatement("resp.$L(responseBuilder.build())", "status" + response.code().value());
            caller.endControlFlow();
        }

    }

    private void parseHeaders(MethodSpec.Builder caller, Response response) {
        for (TypeDeclaration headerType : response.headers()) {
            if(headerType instanceof ArrayTypeDeclaration) {
                caller.addStatement("responseBuilder.$L(response.header($S))",
                        this.naming.property(headerType.name()),
                        headerType.name());
            } else {
                caller.beginControlFlow("if(response.header($S) != null)", headerType.name())
                        .addStatement("responseBuilder.$L(response.header($S)[0])",
                                this.naming.property(headerType.name()),
                                headerType.name())
                        .endControlFlow();
            }
        }
    }

    private void parseBody(MethodSpec.Builder caller, Response response) {
        TypeDeclaration bodyType = ! response.body().isEmpty() ? response.body().get(0) : null;
        if(bodyType  != null) {
            try {
                SupportedMediaType.from(response).clientBodyReaderStatement(response, this.typesPackage, this.naming).append(caller);
            } catch (UnsupportedMediaTypeException e) {
                log.error("error while processing response", e);
                return ;
            }
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
