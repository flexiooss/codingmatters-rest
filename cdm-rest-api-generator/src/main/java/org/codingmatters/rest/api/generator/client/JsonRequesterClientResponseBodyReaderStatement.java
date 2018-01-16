package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonParser;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

public class JsonRequesterClientResponseBodyReaderStatement implements ClientResponseBodyReaderStatement {

    private final Response response;
    private final String typesPackage;
    private final ResourceNaming naming;

    public JsonRequesterClientResponseBodyReaderStatement(Response response, String typesPackage, ResourceNaming naming) {
        this.response = response;
        this.typesPackage = typesPackage;
        this.naming = naming;
    }

    @Override
    public void append(MethodSpec.Builder caller) {
        TypeDeclaration bodyType = ! response.body().isEmpty() ? response.body().get(0) : null;

        caller.beginControlFlow("try($T parser = this.jsonFactory.createParser(response.body()))",
                JsonParser.class
        );
        if(bodyType instanceof ArrayTypeDeclaration) {
            caller.addStatement("responseBuilder.payload(new $T().readArray(parser))",
                    ClassName.get(
                            this.typesPackage + ".json",
                            this.naming.type(((ArrayTypeDeclaration)bodyType).items().name(), "Reader")
                    )
            );
        } else {
            caller.addStatement("responseBuilder.payload(new $T().read(parser))",
                    ClassName.get(this.typesPackage + ".json", this.naming.type(bodyType.type(), "Reader"))
            );
        }
        caller.endControlFlow();
    }

}
