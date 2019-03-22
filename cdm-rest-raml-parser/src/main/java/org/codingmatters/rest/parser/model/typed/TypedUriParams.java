package org.codingmatters.rest.parser.model.typed;

import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;

public class TypedUriParams extends TypedParameter {
    public TypedUriParams( String name, ValueObjectType type ) {
        super( name, type );
    }


}
