package org.codingmatters.rest.parser.model.typed;

import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;

public class TypedQueryParam extends TypedParameter {

    public TypedQueryParam( String name, ValueObjectType type ) {
        super( name, type );
    }
}
