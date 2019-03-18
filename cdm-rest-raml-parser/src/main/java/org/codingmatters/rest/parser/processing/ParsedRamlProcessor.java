package org.codingmatters.rest.parser.processing;

import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.rest.parser.model.ParsedRequest;
import org.codingmatters.rest.parser.model.ParsedResponse;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.rest.parser.model.typed.TypedBody;
import org.codingmatters.rest.parser.model.typed.TypedHeader;
import org.codingmatters.rest.parser.model.typed.TypedQueryParam;
import org.codingmatters.rest.parser.model.typed.TypedUriParams;
import org.codingmatters.value.objects.js.error.ProcessingException;

public interface ParsedRamlProcessor {

    void process( ParsedRaml parsedRaml ) throws ProcessingException;

    void process( ParsedRoute parsedRoute ) throws ProcessingException;

    void process( ParsedRequest parsedRequest ) throws ProcessingException;

    void process( ParsedResponse parsedResponse );

    void process( TypedBody typedBody );

    void process( TypedHeader typedHeader );

    void process( TypedQueryParam typedQueryParam );

    void process( TypedUriParams typedUriParams );
}
