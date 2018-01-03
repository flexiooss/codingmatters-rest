package org.codingmatters.rest.api.generator.processors;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.exception.UnsupportedMediaTypeException;
import org.codingmatters.rest.api.generator.processors.responses.FileResponseStatement;
import org.codingmatters.rest.api.generator.processors.responses.JsonResponseStatement;
import org.codingmatters.rest.api.generator.processors.responses.TextResponseStatement;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.model.v10.bodies.Response;

public interface ResponseStatement {

    MethodSpec.Builder appendTo(MethodSpec.Builder method);
    MethodSpec.Builder appendContentType(MethodSpec.Builder method);

    static ResponseStatement from(Response response, String typesPackage, Naming naming) throws UnsupportedMediaTypeException {
        return SupportedMediaType.from(response).statement(response, typesPackage, naming);
    }

    enum SupportedMediaType {
        FILE {
            @Override
            public boolean matches(Response response) {
                return response.body().get(0).type().equals("file");
            }

            @Override
            public ResponseStatement statement(Response response, String typesPackage, Naming naming) {
                return new FileResponseStatement(response, typesPackage, naming);
            }
        },
        JSON {
            @Override
            public boolean matches(Response response) {
                return "application/json".equals(response.body().get(0).name());
            }

            @Override
            public ResponseStatement statement(Response response, String typesPackage, Naming naming) {
                return new JsonResponseStatement(response, typesPackage, naming);
            }
        },
        TEXT {
            @Override
            public boolean matches(Response response) {
                return response.body().get(0).name().matches("text/.*");
            }

            @Override
            public ResponseStatement statement(Response response, String typesPackage, Naming naming) {
                return new TextResponseStatement(response, typesPackage, naming);
            }
        };

        public abstract boolean matches(Response response);
        public abstract ResponseStatement statement(Response response, String typesPackage, Naming naming);

        static public SupportedMediaType from(Response response) throws UnsupportedMediaTypeException {
            for (SupportedMediaType type : SupportedMediaType.values()) {
                if(type.matches(response)) {
                    return type;
                }
            }
            throw new UnsupportedMediaTypeException("media type is not supported : " + response.body().get(0).name());
        }
    }

}
