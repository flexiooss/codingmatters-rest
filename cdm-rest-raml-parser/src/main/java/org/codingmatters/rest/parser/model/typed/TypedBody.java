package org.codingmatters.rest.parser.model.typed;

import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;

public class TypedBody extends TypedParameter {

    public TypedBody( ValueObjectType type ) {
        super( "payload", type );
    }
}
