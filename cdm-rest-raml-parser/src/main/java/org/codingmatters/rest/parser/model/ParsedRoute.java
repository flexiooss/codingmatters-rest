package org.codingmatters.rest.parser.model;

import org.codingmatters.rest.parser.model.typed.TypedUriParams;
import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.rest.parser.processing.ProcessableRaml;
import org.codingmatters.value.objects.js.error.ProcessingException;

import java.util.ArrayList;
import java.util.List;

public class ParsedRoute implements ProcessableRaml {

    private final List<TypedUriParams> uriParameters;
    private final List<ParsedRequest> requests;
    private final String path;
    private final String displayName;
    private final ArrayList<ParsedRoute> subRoutes;

    public ParsedRoute( String path, String displayName ) {
        this.path = path;
        this.displayName = displayName;
        this.uriParameters = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.subRoutes = new ArrayList<>();
    }

    public List<TypedUriParams> uriParameters() {
        return uriParameters;
    }

    public List<ParsedRequest> requests() {
        return requests;
    }

    public String path() {
        return path;
    }

    public String displayName() {
        return displayName;
    }

    public ArrayList<ParsedRoute> subRoutes() {
        return subRoutes;
    }

    @Override
    public void process( ParsedRamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }
}



