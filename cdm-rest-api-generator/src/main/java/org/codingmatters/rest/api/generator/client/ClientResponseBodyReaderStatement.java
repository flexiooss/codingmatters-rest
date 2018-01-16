package org.codingmatters.rest.api.generator.client;

import com.squareup.javapoet.MethodSpec;

public interface ClientResponseBodyReaderStatement {
    void append(MethodSpec.Builder caller);
}
