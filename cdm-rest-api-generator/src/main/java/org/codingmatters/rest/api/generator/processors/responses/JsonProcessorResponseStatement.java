package org.codingmatters.rest.api.generator.processors.responses;

import com.fasterxml.jackson.core.JsonGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.processors.ProcessorResponseBodyWriterStatement;
import org.codingmatters.rest.api.generator.utils.DeclaredTypeRegistry;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.json.ObjectValueWriter;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

public class JsonProcessorResponseStatement implements ProcessorResponseBodyWriterStatement {
    static private final Logger log = LoggerFactory.getLogger(JsonProcessorResponseStatement.class);

    private final Response response;
    private final TypeDeclaration body;

    private final Naming naming;
    private final String typesPackage;

    public JsonProcessorResponseStatement(Response response, String typesPackage, Naming naming) {
        this.response = response;
        this.body = response.body().get(0);
        this.typesPackage = typesPackage;
        this.naming = naming;
    }

    @Override
    public MethodSpec.Builder appendContentType(MethodSpec.Builder method) {
        return method.addStatement("responseDelegate.contenType($S)", body.name() + "; charset=utf-8");
    }

    @Override
    public MethodSpec.Builder appendTo(MethodSpec.Builder method) {
        method.beginControlFlow("if(response.status$L().payload() != null)", response.code().value());
        method.beginControlFlow("try($T out = new $T())", ByteArrayOutputStream.class, ByteArrayOutputStream.class);

        this.applicationJsonResponsePayload(response, method, body);

        method.addStatement("responseDelegate.payload(out.toString(), $S)", "utf-8");
        method.endControlFlow();
        method.endControlFlow();

        return method;
    }

    private void applicationJsonResponsePayload(Response response, MethodSpec.Builder method, TypeDeclaration body) {
        method.beginControlFlow("try($T generator = this.factory.createGenerator(out))", JsonGenerator.class);

        if(body instanceof ArrayTypeDeclaration || body.type().endsWith("[]")) {
            // TODO replace with list writer

            ClassName elementClassName;
            ClassName elementTypeWriter;

            if(body.type().endsWith("[]")) {
                String itemsTypeName =  body.type().substring(0, body.type().length() - "[]".length());
                TypeDeclaration itemsType = DeclaredTypeRegistry.declaredTypes().get(itemsTypeName);

                if(itemsType != null && this.naming.isAlreadyDefined(itemsType)) {
                    elementClassName = this.naming.alreadyDefinedClass(itemsType);
                    elementTypeWriter = this.naming.alreadyDefinedWriter(itemsType);
                } else {
                    elementClassName = this.elementClassName(itemsTypeName);
                    elementTypeWriter = this.writerClassName(itemsTypeName);
                }

            } else if((! ((ArrayTypeDeclaration) body).items().parentTypes().isEmpty()) && this.naming.isAlreadyDefined(((ArrayTypeDeclaration) body).items().parentTypes().get(0))) {
                elementClassName = this.naming.alreadyDefinedClass(((ArrayTypeDeclaration) body).items().parentTypes().get(0));
                elementTypeWriter = this.naming.alreadyDefinedWriter(((ArrayTypeDeclaration) body).items().parentTypes().get(0));
            } else {
                String elementType = ((ArrayTypeDeclaration) body).items().name();
                elementClassName = this.elementClassName(elementType);
                elementTypeWriter = this.writerClassName(elementType);
            }

            method.addStatement("generator.writeStartArray()");
            method.beginControlFlow("for ($T element : response.status$L().payload())", elementClassName, response.code().value())
                    .beginControlFlow("if(element != null)")
                    .addStatement("new $T().write(generator, element)", elementTypeWriter)
                    .nextControlFlow("else")
                    .addStatement("generator.writeNull()")
                    .endControlFlow()
                    .endControlFlow();
            method.addStatement("generator.writeEndArray()");
        } else {
            ClassName writerName;
            if((! body.parentTypes().isEmpty()) && this.naming.isAlreadyDefined(body.parentTypes().get(0))) {
                writerName = this.naming.alreadyDefinedWriter(body.parentTypes().get(0));
            } else {
                writerName = this.writerClassName(body.type());
            }

            method.addStatement(
                    "new $T().write(generator, response.status$L().payload())",
                    writerName,
                    response.code().value()
            );
        }
        method.endControlFlow();
    }

    private ClassName elementClassName(String elementType) {
        if("object".equals(elementType) || "object[]".equals(elementType)) {
            return ClassName.get(ObjectValue.class);
        } else {
            return ClassName.get(this.typesPackage, this.naming.type(elementType));
        }
    }

    private ClassName writerClassName(String type) {
        if("object".equals(type) || "object[]".equals(type)) {
            return ClassName.get(ObjectValueWriter.class);
        } else {
            return ClassName.get(this.typesPackage + ".json", this.naming.type(type, "Writer"));
        }
    }

}
