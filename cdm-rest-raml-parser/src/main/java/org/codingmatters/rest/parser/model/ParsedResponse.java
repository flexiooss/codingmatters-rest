package org.codingmatters.rest.parser.model;

import org.codingmatters.rest.parser.model.typed.TypedBody;
import org.codingmatters.rest.parser.model.typed.TypedHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParsedResponse {

    private final int code;
    private final List<TypedHeader> headers;
    private final Optional<TypedBody> body;

    public ParsedResponse( int code ) {
        this( code, Optional.empty() );
    }

    public ParsedResponse( int code, TypedBody body ) {
        this( code, Optional.of( body ) );
    }

    public ParsedResponse( int code, Optional<TypedBody> body ) {
        this.code = code;
        this.headers = new ArrayList<>();
        this.body = body;
    }


    public int code() {
        return code;
    }

    public List<TypedHeader> headers() {
        return headers;
    }

    public Optional<TypedBody> body() {
        return body;
    }
}

