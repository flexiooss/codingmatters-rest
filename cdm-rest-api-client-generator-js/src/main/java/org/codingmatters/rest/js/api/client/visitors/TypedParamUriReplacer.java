package org.codingmatters.rest.js.api.client.visitors;

import org.codingmatters.rest.parser.model.typed.TypedUriParams;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.parser.model.ParsedEnum;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeExternalValue;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeInSpecValueObject;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeNested;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypeExternalType;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypeList;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType;
import org.codingmatters.value.objects.js.parser.model.types.YamlEnumExternalEnum;
import org.codingmatters.value.objects.js.parser.model.types.YamlEnumInSpecEnum;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.io.IOException;

/**
 * Created by nico on 18/03/19.
 */
public class TypedParamUriReplacer implements ParsedYamlProcessor {

    private final JsFileWriter write;
    private final TypedUriParams uriParams;
    private String varName;

    public TypedParamUriReplacer( TypedUriParams uriParams, JsFileWriter write, String requestVarName ) {
        this.write = write;
        this.uriParams = uriParams;
        this.varName = requestVarName + "." + NamingUtility.propertyName( uriParams.name() ) + "()";
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

    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {

    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {
            write.line( varName + ".forEach(function(element) {" );
//            write.string( "path.replace( '{" + uriParams.name() + "}', " );
            this.varName = "element";
            list.type().process( this );
//            write.string( " );" );
            write.newLine();
            write.unindent();
            write.line( "})" );
        } catch( Exception e ) {
            throw new ProcessingException( "Error processing uri param", e );
        }
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        try {
            write.indent();
            write.string( "path = path.replace('{" + uriParams.name() + "}', " );
            switch( primitiveType.type() ) {
                case BOOL:
                    write.string( varName + " ? 'true' : 'false'" );
                    break;
                case DATE:
                case DATE_TIME:
                case TIME:
                case TZ_DATE_TIME:
                    write.string( varName + "->toJSON()" );
                    break;
                default:
                    write.string("encodeURIComponent(" + varName + ")");
                    break;
            }
            write.string( " );" );
            write.newLine();
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing uri parameter", e );
        }

    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {

    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {

    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {

    }
}
