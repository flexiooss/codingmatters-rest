package org.codingmatters.rest.api.generator.client;

import com.squareup.javapoet.MethodSpec;
import org.raml.v2.api.model.v10.bodies.Response;

public class TextRequesterBodyReaderStatement implements BodyReaderStatement {

    private final Response response;
    private final String typesPackage;
    private final ResourceNaming naming;

    public TextRequesterBodyReaderStatement(Response response, String typesPackage, ResourceNaming naming) {
        this.response = response;
        this.typesPackage = typesPackage;
        this.naming = naming;
    }

    @Override
    public void append(MethodSpec.Builder caller) {
        caller.addStatement("responseBuilder.payload(new $T(response.body()))", String.class);
    }

}
