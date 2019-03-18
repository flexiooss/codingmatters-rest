package org.codingmatters.rest.parser.model.typed;

import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;

public class TypedBody extends TypedParameter {

    public TypedBody( ValueObjectType type ) {
        super( "payload", type );
    }

    @Override
    public void process( ParsedRamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }
}
