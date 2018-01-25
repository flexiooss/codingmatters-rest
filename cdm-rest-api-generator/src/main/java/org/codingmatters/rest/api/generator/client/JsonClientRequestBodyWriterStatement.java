package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.json.ObjectValueWriter;
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
            caller.beginControlFlow("for ($T element : request.payload())", this.elementClassName(elementType))
                    .beginControlFlow("if(element != null)")
                    .addStatement("new $T().write(generator, element)", this.writerClassName(elementType))
                    .nextControlFlow("else")
                    .addStatement("generator.writeNull()")
                    .endControlFlow()
                    .endControlFlow();
            caller.addStatement("generator.writeEndArray()");
        } else {
            caller.addStatement(
                    "new $T().write(generator, request.payload())",
                    this.writerClassName(body.type())
            );
        }
        caller.endControlFlow();
    }

    @Override
    public void appendContentTypeVariableCreate(MethodSpec.Builder caller) {
        caller.addStatement("String contentType = $S", "application/json");
    }


    private ClassName elementClassName(String elementType) {
        if(elementType.equals("object")) {
            return ClassName.get(ObjectValue.class);
        } else {
            return ClassName.get(this.typesPackage, this.naming.type(elementType));
        }
    }

    private ClassName writerClassName(String type) {
        if(type.equals("object")) {
            return ClassName.get(ObjectValueWriter.class);
        } else {
            return ClassName.get(this.typesPackage + ".json", this.naming.type(type, "Writer"));
        }
    }
}
