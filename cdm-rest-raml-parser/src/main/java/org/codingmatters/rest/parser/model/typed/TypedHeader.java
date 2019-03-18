package org.codingmatters.rest.parser.model.typed;

import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;

public class TypedHeader extends TypedParameter {

    public TypedHeader( String name, ValueObjectType type ) {
        super( name, type );
    }
}
