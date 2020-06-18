package org.codingmatters.rest.js.api.client.visitors;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.parser.model.ParsedEnum;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.io.IOException;

public class TypedParamStringifier implements ParsedYamlProcessor {

    private String varName;
    private final JsFileWriter write;

    public TypedParamStringifier( JsFileWriter write, String varName ) {
        this.write = write;
        this.varName = varName;
    }

    @Override
    public void process( ParsedYAMLSpec spec ) throws ProcessingException {

    }

    @Override
    public void process( ParsedValueObject valueObject ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectProperty property ) throws ProcessingException {

    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {
        throw new ProcessingException( "Not implemented" );
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        throw new ProcessingException( "Not implemented" );
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        throw new ProcessingException( "Not implemented" );
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {
            write.string( varName + ".mapToArray(element => " );
            this.varName = "element";
            list.type().process( this );
            write.string( ")" );
        } catch( IOException e ){
            throw new ProcessingException( "Error stringify list", e );
        }
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        try {
            switch( primitiveType.type() ){
                case BOOL:
                    write.string( varName + " ? 'true' : 'false'" );
                    break;
                case TZ_DATE_TIME:
                case DATE_TIME:
                case DATE:
                case TIME:
                    write.string( varName + ".toJSON()" );
                    break;
                case OBJECT:
                    write.string( "JSON.stringify(" + varName + ")" );
                    break;
                case STRING:
                case BYTES:
                    write.string( varName );
                    break;
                case DOUBLE:
                case LONG:
                case FLOAT:
                case INT:
                    write.string( varName + ".toString()" );
                    break;
                default:
                    break;
            }
        } catch( IOException e ){
            throw new ProcessingException( "Error stringify primitive type", e );
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {
        try {
            write.string( varName + ".name()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error stringifying enum type", e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try {
            write.string( varName + ".name()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error stringifying enum type", e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        throw new ProcessingException( "Not implemented" );
    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {
        try {
            write.string( varName + ".name()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error stringifying enum type", e );
        }
    }
}
