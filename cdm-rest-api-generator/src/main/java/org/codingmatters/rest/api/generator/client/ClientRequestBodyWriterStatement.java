package org.codingmatters.rest.api.generator.client;

import com.squareup.javapoet.MethodSpec;

public interface ClientRequestBodyWriterStatement {
    void append(MethodSpec.Builder caller);

    void appendContentTypeVariableCreate(MethodSpec.Builder caller);
}
