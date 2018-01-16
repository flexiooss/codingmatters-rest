package org.codingmatters.rest.api.generator.type;

import org.codingmatters.rest.api.generator.client.*;
import org.codingmatters.rest.api.generator.exception.UnsupportedMediaTypeException;
import org.codingmatters.rest.api.generator.processors.ProcessorResponseBodyWriterStatement;
import org.codingmatters.rest.api.generator.processors.requests.FileProcessorRequestBodyReaderStatement;
import org.codingmatters.rest.api.generator.processors.requests.JsonProcessorRequestBodyReaderStatement;
import org.codingmatters.rest.api.generator.processors.requests.ProcessorRequestBodyReaderStatement;
import org.codingmatters.rest.api.generator.processors.requests.TextProcessorRequestBodyReaderStatement;
import org.codingmatters.rest.api.generator.processors.responses.FileProcessorResponseStatement;
import org.codingmatters.rest.api.generator.processors.responses.JsonProcessorResponseStatement;
import org.codingmatters.rest.api.generator.processors.responses.TextProcessorResponseStatement;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.methods.Method;

public enum SupportedMediaType {
    FILE {
        @Override
        public boolean matches(Response response) {
            return response.body().get(0).type().equals("file");
        }

        @Override
        protected boolean matches(Method method) {
            return method.body().get(0).type().equals("file");
        }

        @Override
        public ProcessorResponseBodyWriterStatement processorResponseBodyWriterStatement(Response response, String typesPackage, Naming naming) {
            return new FileProcessorResponseStatement(response, typesPackage, naming);
        }

        @Override
        public ClientResponseBodyReaderStatement clientBodyReaderStatement(Response response, String typesPackage, ResourceNaming naming) {
            return new FileRequesterClientResponseBodyReaderStatement(response, typesPackage, naming);
        }

        @Override
        public ClientRequestBodyWriterStatement clientBodyWriterStatement(Method method, String typesPackage, ResourceNaming naming) {
            return new FileClientRequestBodyWriterStatement(method, typesPackage, naming);
        }

        @Override
        public ProcessorRequestBodyReaderStatement processorBodyReaderStatement(Method method, String typesPackage, Naming naming) {
            return new FileProcessorRequestBodyReaderStatement(method, typesPackage, naming);
        }
    },
    JSON {
        @Override
        public boolean matches(Response response) {
            return "application/json".equals(response.body().get(0).name());
        }

        @Override
        protected boolean matches(Method method) {
            return "application/json".equals(method.body().get(0).name());
        }

        @Override
        public ProcessorResponseBodyWriterStatement processorResponseBodyWriterStatement(Response response, String typesPackage, Naming naming) {
            return new JsonProcessorResponseStatement(response, typesPackage, naming);
        }

        @Override
        public ClientResponseBodyReaderStatement clientBodyReaderStatement(Response response, String typesPackage, ResourceNaming naming) {
            return new JsonRequesterClientResponseBodyReaderStatement(response, typesPackage, naming);
        }

        @Override
        public ClientRequestBodyWriterStatement clientBodyWriterStatement(Method method, String typesPackage, ResourceNaming naming) {
            return new JsonClientRequestBodyWriterStatement(method, typesPackage, naming);
        }

        @Override
        public ProcessorRequestBodyReaderStatement processorBodyReaderStatement(Method method, String typesPackage, Naming naming) {
            return new JsonProcessorRequestBodyReaderStatement(method, typesPackage, naming);
        }
    },
    TEXT {
        @Override
        public boolean matches(Response response) {
            return response.body().get(0).name().matches("text/.*");
        }@Override

        public boolean matches(Method method) {
            return method.body().get(0).name().matches("text/.*");
        }

        @Override
        public ProcessorResponseBodyWriterStatement processorResponseBodyWriterStatement(Response response, String typesPackage, Naming naming) {
            return new TextProcessorResponseStatement(response, typesPackage, naming);
        }

        @Override
        public ClientResponseBodyReaderStatement clientBodyReaderStatement(Response response, String typesPackage, ResourceNaming naming) {
            return new TextRequesterClientResponseBodyReaderStatement(response, typesPackage, naming);
        }

        @Override
        public ClientRequestBodyWriterStatement clientBodyWriterStatement(Method method, String typesPackage, ResourceNaming naming) {
            return new TextClientRequestBodyWriterStatement(method, typesPackage, naming);
        }

        @Override
        public ProcessorRequestBodyReaderStatement processorBodyReaderStatement(Method method, String typesPackage, Naming naming) {
            return new TextProcessorRequestBodyReaderStatement(method, typesPackage, naming);
        }
    };

    public abstract boolean matches(Response response);
    protected abstract boolean matches(Method method);

    public abstract ProcessorResponseBodyWriterStatement processorResponseBodyWriterStatement(Response response, String typesPackage, Naming naming);
    public abstract ProcessorRequestBodyReaderStatement processorBodyReaderStatement(Method method, String typesPackage, Naming naming);

    public abstract ClientResponseBodyReaderStatement clientBodyReaderStatement(Response response, String typesPackage, ResourceNaming naming);
    public abstract ClientRequestBodyWriterStatement clientBodyWriterStatement(Method method, String typesPackage, ResourceNaming naming);

    static public SupportedMediaType from(Response response) throws UnsupportedMediaTypeException {
        for (SupportedMediaType type : SupportedMediaType.values()) {
            if(type.matches(response)) {
                return type;
            }
        }
        throw new UnsupportedMediaTypeException("media type is not supported : " + response.body().get(0).name());
    }

    static public SupportedMediaType from(Method method) throws UnsupportedMediaTypeException {
        for (SupportedMediaType type : SupportedMediaType.values()) {
            if(type.matches(method)) {
                return type;
            }
        }
        throw new UnsupportedMediaTypeException("media type is not supported : " + method.body().get(0).name());
    }


}
