package org.codingmatters.rest.parser.model.typed;

import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;

public class TypedHeader extends TypedParameter {

    public TypedHeader( String name, ValueObjectType type ) {
        super( name, type );
    }

    @Override
    public void process( ParsedRamlProcessor processor ) throws ProcessingException {
        processor.process( this  );
    }
}
