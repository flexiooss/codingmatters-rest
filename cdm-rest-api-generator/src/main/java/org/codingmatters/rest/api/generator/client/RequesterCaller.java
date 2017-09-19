package org.codingmatters.rest.api.generator.client;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.ResponseDelegate;
import org.raml.v2.api.model.v10.methods.Method;

import javax.lang.model.element.Modifier;
import java.io.IOException;

public class RequesterCaller {
    private final ResourceNaming naming;
    private final Method method;

    public RequesterCaller(ResourceNaming naming, Method method) {
        this.naming = naming;
        this.method = method;
    }

    public MethodSpec caller() {
        MethodSpec.Builder caller = MethodSpec.methodBuilder(this.callerName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(this.requestType(), "request")
                .returns(this.responseType())
                .addException(IOException.class);

        caller.addStatement("$T requester = this.requesterFactory\n" +
                        ".forBaseUrl(this.baseUrl)\n" +
                        ".path($S)",
                Requester.class, method.resource().resourcePath());

        caller.addStatement("$T response = requester.get()", ResponseDelegate.class);

        caller.addStatement("$T.Builder resp = $T.builder()",
                responseType(),
                responseType());
        caller.addStatement("return resp.build()");

        return caller.build();
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
