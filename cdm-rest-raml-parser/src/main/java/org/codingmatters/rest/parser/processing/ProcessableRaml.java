package org.codingmatters.rest.parser.processing;

import org.codingmatters.value.objects.js.error.ProcessingException;

public interface ProcessableRaml {
    public void process( ParsedRamlProcessor processor ) throws ProcessingException;

}
