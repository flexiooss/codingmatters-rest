package org.codingmatters.rest.api.generator.processors;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.exception.UnsupportedMediaTypeException;
import org.codingmatters.rest.api.generator.type.SupportedMediaType;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.model.v10.bodies.Response;

public interface ProcessorResponseBodyWriterStatement {

    MethodSpec.Builder appendTo(MethodSpec.Builder method);
    MethodSpec.Builder appendContentType(MethodSpec.Builder method);

    static ProcessorResponseBodyWriterStatement from(Response response, String typesPackage, Naming naming) throws UnsupportedMediaTypeException {
        return SupportedMediaType.from(response).processorResponseBodyWriterStatement(response, typesPackage, naming);
    }

}
