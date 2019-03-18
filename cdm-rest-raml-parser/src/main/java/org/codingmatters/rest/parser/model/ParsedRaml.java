package org.codingmatters.rest.parser.model;

import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;

import java.util.ArrayList;
import java.util.List;

public class ParsedRaml {

    private final List<ParsedValueObject> types;
    private final List<ParsedRoute> routes;

    public ParsedRaml() {
        this.types = new ArrayList<>();
        this.routes = new ArrayList<>();
    }

    public List<ParsedValueObject> types() {
        return types;
    }

    public List<ParsedRoute> routes() {
        return routes;
    }
}
