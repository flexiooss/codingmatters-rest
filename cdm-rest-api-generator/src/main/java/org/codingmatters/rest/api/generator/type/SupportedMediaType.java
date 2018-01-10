package org.codingmatters.rest.api.generator.type;

import org.codingmatters.rest.api.generator.client.*;
import org.codingmatters.rest.api.generator.exception.UnsupportedMediaTypeException;
import org.codingmatters.rest.api.generator.processors.ResponseStatement;
import org.codingmatters.rest.api.generator.processors.responses.FileResponseStatement;
import org.codingmatters.rest.api.generator.processors.responses.JsonResponseStatement;
import org.codingmatters.rest.api.generator.processors.responses.TextResponseStatement;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.model.v10.bodies.Response;

public enum SupportedMediaType {
    FILE {
        @Override
        public boolean matches(Response response) {
            return response.body().get(0).type().equals("file");
        }

        @Override
        public ResponseStatement responseStatement(Response response, String typesPackage, Naming naming) {
            return new FileResponseStatement(response, typesPackage, naming);
        }

        @Override
        public BodyReaderStatement bodyReaderStatement(Response response, String typesPackage, ResourceNaming naming) {
            return new FileRequesterBodyReaderStatement(response, typesPackage, naming);
        }
    },
    JSON {
        @Override
        public boolean matches(Response response) {
            return "application/json".equals(response.body().get(0).name());
        }

        @Override
        public ResponseStatement responseStatement(Response response, String typesPackage, Naming naming) {
            return new JsonResponseStatement(response, typesPackage, naming);
        }

        @Override
        public BodyReaderStatement bodyReaderStatement(Response response, String typesPackage, ResourceNaming naming) {
            return new JsonRequesterBodyReaderStatement(response, typesPackage, naming);
        }
    },
    TEXT {
        @Override
        public boolean matches(Response response) {
            return response.body().get(0).name().matches("text/.*");
        }

        @Override
        public ResponseStatement responseStatement(Response response, String typesPackage, Naming naming) {
            return new TextResponseStatement(response, typesPackage, naming);
        }

        @Override
        public BodyReaderStatement bodyReaderStatement(Response response, String typesPackage, ResourceNaming naming) {
            return new TextRequesterBodyReaderStatement(response, typesPackage, naming);
        }
    };

    public abstract boolean matches(Response response);
    public abstract ResponseStatement responseStatement(Response response, String typesPackage, Naming naming);
    public abstract BodyReaderStatement bodyReaderStatement(Response response, String typesPackage, ResourceNaming naming);

    static public SupportedMediaType from(Response response) throws UnsupportedMediaTypeException {
        for (SupportedMediaType type : SupportedMediaType.values()) {
            if(type.matches(response)) {
                return type;
            }
        }
        throw new UnsupportedMediaTypeException("media type is not supported : " + response.body().get(0).name());
    }
}
