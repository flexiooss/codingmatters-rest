package org.codingmatters.rest.api.generator.processors.responses;

import com.fasterxml.jackson.core.JsonGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.processors.ProcessorResponseBodyWriterStatement;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.json.ObjectValueWriter;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.ByteArrayOutputStream;

public class JsonProcessorResponseStatement implements ProcessorResponseBodyWriterStatement {

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
        if(body instanceof ArrayTypeDeclaration) {
            // TODO replace with list writer
            String elementType = ((ArrayTypeDeclaration) body).items().name();
            method.addStatement("generator.writeStartArray()");
            method.beginControlFlow("for ($T element : response.status$L().payload())", this.elementClassName(elementType), response.code().value())
                    .beginControlFlow("if(element != null)")
                    .addStatement("new $T().write(generator, element)", this.writerClassName(elementType))
                    .nextControlFlow("else")
                    .addStatement("generator.writeNull()")
                    .endControlFlow()
                    .endControlFlow();
            method.addStatement("generator.writeEndArray()");
        } else {
            method.addStatement(
                    "new $T().write(generator, response.status$L().payload())",
                    this.writerClassName(body.type()),
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
