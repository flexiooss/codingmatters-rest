package org.codingmatters.rest.api.generator.client;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.types.File;
import org.codingmatters.rest.io.Content;
import org.raml.v2.api.model.v10.bodies.Response;

import java.io.InputStream;

public class FileRequesterClientResponseBodyReaderStatement implements ClientResponseBodyReaderStatement {
    private final Response response;
    private final String typesPackage;
    private final ResourceNaming naming;

    public FileRequesterClientResponseBodyReaderStatement(Response response, String typesPackage, ResourceNaming naming) {
        this.response = response;
        this.typesPackage = typesPackage;
        this.naming = naming;
    }

    @Override
    public void append(MethodSpec.Builder caller) {
        caller
                .beginControlFlow("try($T bodyStream = response.bodyStream())", InputStream.class)
                .addStatement("responseBuilder.payload($T.builder().contentType(response.contentType()).content($T.from(bodyStream)).build())",File.class, Content.class)
                .endControlFlow()
        ;
    }
}
