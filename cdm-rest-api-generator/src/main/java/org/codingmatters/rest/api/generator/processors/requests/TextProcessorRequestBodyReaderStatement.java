package org.codingmatters.rest.api.generator.processors.requests;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.model.v10.methods.Method;

import java.io.ByteArrayOutputStream;

public class TextProcessorRequestBodyReaderStatement implements ProcessorRequestBodyReaderStatement {
    private final Method method;
    private final String typesPackage;
    private final Naming naming;

    public TextProcessorRequestBodyReaderStatement(Method method, String typesPackage, Naming naming) {
        this.method = method;
        this.typesPackage = typesPackage;
        this.naming = naming;
    }

    @Override
    public void append(MethodSpec.Builder caller) {
        caller.beginControlFlow("try($T out = new $T())", ByteArrayOutputStream.class, ByteArrayOutputStream.class)
                    .addStatement("byte[] buffer = new byte[1024]")
                    .beginControlFlow("for(int read = payload.read(buffer) ; read != -1 ; read = payload.read(buffer))")
                        .addStatement("out.write(buffer, 0, read)")
                    .endControlFlow()
                    .addStatement("requestBuilder.payload(out.toString())")
                .nextControlFlow("catch(IOException e)")
                    .addStatement("responseDelegate.status($L).payload($S, $S)", 400, "bad request body, see logs", "utf-8")
                    .addStatement("log.warn($S, e)", "malformed request")
                    .addStatement("return")
                .endControlFlow();
    }
}
