package org.codingmatters.rest.parser.processing;

import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.value.objects.js.error.ProcessingException;

public interface ParsedRamlProcessor {

    void process( ParsedRaml parsedRaml ) throws ProcessingException;

    void process( ParsedRoute parsedRoute ) throws ProcessingException;

}
