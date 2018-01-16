package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;

public class JsonClientRequestBodyWriterStatement implements ClientRequestBodyWriterStatement {
    private final Method method;
    private final String typesPackage;
    private final ResourceNaming naming;

    public JsonClientRequestBodyWriterStatement(Method method, String typesPackage, ResourceNaming naming) {
        this.method = method;
        this.typesPackage = typesPackage;
        this.naming = naming;
    }

    @Override
    public void append(MethodSpec.Builder caller) {
        TypeDeclaration body = this.method.body().get(0);
        caller.beginControlFlow("try($T generator = this.jsonFactory.createGenerator(out))", JsonGenerator.class);
        if(body instanceof ArrayTypeDeclaration) {
            // TODO replace with list writer
            String elementType = ((ArrayTypeDeclaration) body).items().name();
            caller.addStatement("generator.writeStartArray()");
            caller.beginControlFlow("for ($T element : request.payload())", ClassName.get(this.typesPackage, this.naming.type(elementType)))
                    .beginControlFlow("if(element != null)")
                    .addStatement("new $T().write(generator, element)", ClassName.get(this.typesPackage + ".json", this.naming.type(elementType, "Writer")))
                    .nextControlFlow("else")
                    .addStatement("generator.writeNull()")
                    .endControlFlow()
                    .endControlFlow();
            caller.addStatement("generator.writeEndArray()");
        } else {
            caller.addStatement(
                    "new $T().write(generator, request.payload())",
                    ClassName.get(this.typesPackage + ".json", this.naming.type(body.type(), "Writer"))
            );
        }
        caller.endControlFlow();
    }
}
