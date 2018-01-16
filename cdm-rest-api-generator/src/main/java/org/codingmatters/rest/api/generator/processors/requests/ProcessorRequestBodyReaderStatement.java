package org.codingmatters.rest.api.generator.processors.requests;

import com.squareup.javapoet.MethodSpec;

public interface ProcessorRequestBodyReaderStatement {
    void append(MethodSpec.Builder caller);
}
