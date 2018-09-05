package org.codingmatters.rest.api.generator.processors.requests;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.rest.api.types.File;
import org.codingmatters.rest.io.Content;
import org.raml.v2.api.model.v10.methods.Method;

public class FileProcessorRequestBodyReaderStatement implements ProcessorRequestBodyReaderStatement {
    private final Method method;
    private final String typesPackage;
    private final Naming naming;

    public FileProcessorRequestBodyReaderStatement(Method method, String typesPackage, Naming naming) {
        this.method = method;
        this.typesPackage = typesPackage;
        this.naming = naming;
    }

    @Override
    public void append(MethodSpec.Builder caller) {
        caller.addStatement("requestBuilder.payload($T.builder()" +
                                ".content($T.from(payload))" +
                                ".contentType(requestDelegate.contentType()).build())",
                        File.class, Content.class
                );
    }
}
