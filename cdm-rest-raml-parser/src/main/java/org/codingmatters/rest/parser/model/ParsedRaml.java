package org.codingmatters.rest.parser.model;

import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.rest.parser.processing.ProcessableRaml;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;

import java.util.ArrayList;
import java.util.List;

public class ParsedRaml implements ProcessableRaml {

    private final String apiName;
    private final List<ParsedValueObject> types;
    private final List<ParsedRoute> routes;

    public ParsedRaml( String apiName ) {
        this.apiName = apiName;
        this.types = new ArrayList<>();
        this.routes = new ArrayList<>();
    }

    public List<ParsedValueObject> types() {
        return types;
    }

    public List<ParsedRoute> routes() {
        return routes;
    }

    @Override
    public void process( ParsedRamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }

    public String apiName() {
        return apiName;
    }
}
