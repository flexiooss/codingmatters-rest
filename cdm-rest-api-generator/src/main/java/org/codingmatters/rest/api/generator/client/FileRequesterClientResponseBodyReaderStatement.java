package org.codingmatters.rest.api.generator.client;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.types.File;
import org.raml.v2.api.model.v10.bodies.Response;

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
        caller.addStatement("responseBuilder.payload($T.builder().contentType(response.contentType()).content(response.body()).build())",
                File.class
        );
    }
}
