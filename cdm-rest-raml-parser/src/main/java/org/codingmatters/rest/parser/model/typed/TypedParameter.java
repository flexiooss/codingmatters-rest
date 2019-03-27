package org.codingmatters.rest.parser.model.typed;

import org.codingmatters.rest.parser.processing.ProcessableRaml;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;

public abstract class TypedParameter {

    private final String name;
    private final ValueObjectType type;

    public TypedParameter( String name, ValueObjectType type ) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public ValueObjectType type() {
        return type;
    }
}
