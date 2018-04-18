package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.utils.DeclaredTypeRegistry;
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


        if(body instanceof ArrayTypeDeclaration || body.type().endsWith("[]")) {
            ClassName elementClassName;
            ClassName elementTypeWriter;

            if(body.type().endsWith("[]")) {
                String itemsTypeName = body.type().substring(0, body.type().length() - "[]".length());
                TypeDeclaration itemsType = DeclaredTypeRegistry.declaredTypes().get(itemsTypeName);

                if (itemsType != null && this.naming.isAlreadyDefined(itemsType)) {
                    elementClassName = this.naming.alreadyDefinedClass(itemsType);
                    elementTypeWriter = this.naming.alreadyDefinedWriter(itemsType);
                } else {
                    elementClassName = this.elementClassName(itemsTypeName);
                    elementTypeWriter = this.writerClassName(itemsTypeName);
                }

            } else if ((!((ArrayTypeDeclaration) body).items().parentTypes().isEmpty()) && this.naming.isAlreadyDefined(((ArrayTypeDeclaration) body).items().parentTypes().get(0))) {
                elementClassName = this.naming.alreadyDefinedClass(((ArrayTypeDeclaration) body).items().parentTypes().get(0));
                elementTypeWriter = this.naming.alreadyDefinedWriter(((ArrayTypeDeclaration) body).items().parentTypes().get(0));
            } else {
                String elementType = ((ArrayTypeDeclaration) body).items().name();
                elementClassName = this.elementClassName(elementType);
                elementTypeWriter = this.writerClassName(elementType);
            }


            caller.addStatement("new $T().writeArray(generator, request.payload().toArray(new $T[request.payload().size()]))",
                    elementTypeWriter,
                    elementClassName
            );
        } else {
            ClassName writerClass;
            if((! body.parentTypes().isEmpty()) && this.naming.isAlreadyDefined(body.parentTypes().get(0))) {
                writerClass = this.naming.alreadyDefinedWriter(body.parentTypes().get(0));
            } else {
                writerClass = this.writerClassName(body.type());
            }

            caller.addStatement(
                    "new $T().write(generator, request.payload())",
                    writerClass
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
