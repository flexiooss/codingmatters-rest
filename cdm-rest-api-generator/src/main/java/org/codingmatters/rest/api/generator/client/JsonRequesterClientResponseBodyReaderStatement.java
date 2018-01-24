package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonParser;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.value.objects.values.json.ObjectValueReader;
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
            String elementType = ((ArrayTypeDeclaration) bodyType).items().name();
            caller.addStatement("responseBuilder.payload(new $T().readArray(parser))", this.readerClassName(elementType));
        } else {
            String elementType = bodyType.type();
            caller.addStatement("responseBuilder.payload(new $T().read(parser))", this.readerClassName(elementType));
        }
        caller.endControlFlow();
    }

    private ClassName readerClassName(String elementType) {
        if("object".equals(elementType) || "object[]".equals(elementType)) {
            return ClassName.get(ObjectValueReader.class);
        } else {
            return ClassName.get(
                    this.typesPackage + ".json",
                    this.naming.type(elementType, "Reader")
            );
        }
    }

}
