package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonParser;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.utils.DeclaredTypeRegistry;
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
        TypeDeclaration body = this.response.body().get(0);
        caller.beginControlFlow("try($T parser = this.jsonFactory.createParser(response.body()))",
                JsonParser.class
        );

        if(body.type().endsWith("[]")) {
            String itemsTypeName =  body.type().substring(0, body.type().length() - "[]".length());
            TypeDeclaration itemsType = DeclaredTypeRegistry.declaredTypes().get(itemsTypeName);

            ClassName className;
            if(itemsType != null && this.naming.isAlreadyDefined(itemsType)) {
                className = this.naming.alreadyDefinedReader(itemsType);
            } else {
                className = this.readerClassName(itemsTypeName);
            }

            caller.addStatement("responseBuilder.payload(new $T().readArray(parser))", className);
        } else if(body instanceof ArrayTypeDeclaration) {
            String itemsTypeName =  ((ArrayTypeDeclaration)body).items().type();
            TypeDeclaration itemsType = DeclaredTypeRegistry.declaredTypes().get(itemsTypeName);

            ClassName className;
            if(itemsType != null && this.naming.isAlreadyDefined(itemsType)) {
                className = this.naming.alreadyDefinedReader(itemsType);
            } else {
                className = this.readerClassName(itemsTypeName);
            }

            caller.addStatement("responseBuilder.payload(new $T().readArray(parser))", className);
        } else {
            ClassName className;
            if((! body.parentTypes().isEmpty()) && this.naming.isAlreadyDefined(body.parentTypes().get(0))) {
                className = this.naming.alreadyDefinedReader(body.parentTypes().get(0));
            } else {
                className = this.readerClassName(body.type());
            }

            caller.addStatement("responseBuilder.payload(new $T().read(parser))", className);
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
