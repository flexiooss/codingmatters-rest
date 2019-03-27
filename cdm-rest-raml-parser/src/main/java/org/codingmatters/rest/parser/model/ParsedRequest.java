package org.codingmatters.rest.parser.model;

import org.codingmatters.rest.parser.model.typed.TypedBody;
import org.codingmatters.rest.parser.model.typed.TypedHeader;
import org.codingmatters.rest.parser.model.typed.TypedQueryParam;
import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.rest.parser.processing.ProcessableRaml;
import org.codingmatters.value.objects.js.error.ProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParsedRequest {

    private final RequestMethod httpMethod;
    private final List<ParsedResponse> responses;
    private final List<TypedHeader> headers;
    private final List<TypedQueryParam> queryParameters;
    private final Optional<TypedBody> body;

    public ParsedRequest( RequestMethod httpMethod ) {
        this( httpMethod, Optional.empty() );
    }

    public ParsedRequest( RequestMethod httpMethod, TypedBody body ) {
        this( httpMethod, Optional.of( body ) );
    }

    public ParsedRequest( RequestMethod httpMethod, Optional<TypedBody> body ) {
        this.httpMethod = httpMethod;
        this.headers = new ArrayList<>();
        this.responses = new ArrayList<>();
        this.queryParameters = new ArrayList<>();
        this.body = body;
    }

    public RequestMethod httpMethod() {
        return httpMethod;
    }

    public List<ParsedResponse> responses() {
        return responses;
    }

    public List<TypedHeader> headers() {
        return headers;
    }

    public List<TypedQueryParam> queryParameters() {
        return queryParameters;
    }

    public Optional<TypedBody> body() {
        return body;
    }


}
