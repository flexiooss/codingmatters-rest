package org.codingmatters.rest.api.generator.processors.requests;

import com.fasterxml.jackson.core.JsonParser;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.model.v10.methods.Method;

public class JsonProcessorRequestBodyReaderStatement implements ProcessorRequestBodyReaderStatement {
    private final Method method;
    private final String typesPackage;
    private final Naming naming;

    public JsonProcessorRequestBodyReaderStatement(Method method, String typesPackage, Naming naming) {
        this.method = method;
        this.typesPackage = typesPackage;
        this.naming = naming;
    }

    @Override
    public void append(MethodSpec.Builder caller) {
        caller.addStatement("$T parser = this.factory.createParser(payload)", JsonParser.class);
        caller.addStatement("requestBuilder.payload(new $T().read(parser))",
                ClassName.get(this.typesPackage + ".json", this.naming.type(this.method.body().get(0).type(), "Reader"))
        );
        caller.nextControlFlow("catch(IOException e)");
        caller
                .addStatement("responseDelegate.status($L).payload($S, $S)", 400, "bad request body, see logs", "utf-8")
                .addStatement("log.info($S)", "malformed request")
                .addStatement("return");
    }
}
