package org.codingmatters.rest.js.api.client.visitors;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.visitor.JsValueListDeserializationProcessor;
import org.codingmatters.value.objects.js.generator.visitor.PropertiesDeserializationProcessor;
import org.codingmatters.value.objects.js.parser.model.ParsedEnum;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

public class TypedParamUnStringifier implements ParsedYamlProcessor {

    protected final JsFileWriter write;
    private String propertyName;
    protected String currentVariable;
    private char currentIndex = 'a';
    protected final String typesPackage;
    private String deserializeFunction;

    public TypedParamUnStringifier( JsFileWriter jsClassGenerator, String typesPackage ) {
        this.write = jsClassGenerator;
        this.typesPackage = typesPackage;
        this.deserializeFunction = "fromJson";
    }

    public void currentVariable( String currentVariable ) {
        this.currentVariable = currentVariable;
    }

    @Override
    public void process( ParsedYAMLSpec spec ) throws ProcessingException {

    }

    @Override
    public void process( ParsedValueObject valueObject ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectProperty property ) throws ProcessingException {
        try {
            this.propertyName = property.name();
            write.indent();
            write.string( "builder." + NamingUtility.propertyName( property.name() ) + "(" );
            currentVariable = "jsonObject['" + propertyName + "']";
            property.type().process( new PropertiesDeserializationProcessor( write, typesPackage ) );
            write.string( ")" );
            write.newLine();
        } catch( IOException e ){
            throw new ProcessingException( "Error processing property " + property.name(), e );
        }
    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {
        try {
            String reference = externalValueObject.objectReference();
            String builderName = NamingUtility.builderFullName( reference );
            write.string( builderName + "." + deserializeFunction + "(" + currentVariable + ").build()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try {
            String builderName = NamingUtility.builderFullName( typesPackage + "." + inSpecValueObject.inSpecValueObjectName() );
            write.string( builderName + "." + deserializeFunction + "(" + currentVariable + ").build()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try {
            String builderName = NamingUtility.builderFullName( typesPackage + "." + nestedValueObject.namespace() + "." + nestedValueObject.nestValueObject().name() );
            write.string( builderName + "." + deserializeFunction + "(" + currentVariable + ").build()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {
            String var = generateVarName();
            if( deserializeFunction.equals( "fromJson" ) && isObjectList( list ) ){
                currentVariable = "JSON.parse(" + currentVariable + ")";
                deserializeFunction = "fromObject";
            }
            new JsValueListDeserializationProcessor( write, currentVariable, var, typesPackage ).process( list );
            currentVariable = var;
            list.type().process( this );
            write.string( "))" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    private boolean isObjectList( ValueObjectTypeList list ) {
        if( list.type() instanceof ValueObjectTypeList ){
            return isObjectList( (ValueObjectTypeList) list.type() );
        }
        return list.type() instanceof ObjectType ||
                ((list.type() instanceof ValueObjectTypePrimitiveType) &&
                        (((ValueObjectTypePrimitiveType) list.type()).type() == ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.OBJECT));
    }

    private String generateVarName() {
        return String.valueOf( currentIndex++ );
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        try {
            switch( primitiveType.type() ){
                case INT:
                case LONG:
                    write.string( "parseInt(" + currentVariable + ")" );
                    break;
                case FLOAT:
                case DOUBLE:
                    write.string( "parseFloat(" + currentVariable + ")" );
                    break;
                case DATE:
                    write.string( "new FlexDate(" + currentVariable + ")" );
                    break;
                case TIME:
                    write.string( "new FlexTime(" + currentVariable + ")" );
                    break;
                case DATE_TIME:
                    write.string( "new FlexDateTime(" + currentVariable + ")" );
                    break;
                case TZ_DATE_TIME:
                    write.string( "new FlexZonedDateTime(" + currentVariable + ")" );
                    break;
                case OBJECT:
                    if( dataUnstringified() ){
                        write.string( currentVariable );
                    } else {
                        write.string( "JSON.parse(" + currentVariable + ")" );
                    }
                    break;
                case BOOL:
                    write.string( currentVariable + " === 'true' ? true : false" );
                    break;
                default:
                    write.string( currentVariable );
                    break;
            }
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    private boolean dataUnstringified() {
        return deserializeFunction.equals( "fromObject" );
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {
        try {
            String className = NamingUtility.className( externalEnum.enumReference() );
            write.string( className + ".enumValueOf(" + currentVariable + ")" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try {
            String className = NamingUtility.classFullName( typesPackage + "." + inSpecEnum.namespace() + "." + inSpecEnum.name() );
            write.string( className + ".enumValueOf(" + currentVariable + ")" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        throw new NotImplementedException();
    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {
        try {
            String className = NamingUtility.classFullName( parsedEnum.packageName() + "." + parsedEnum.name() );
            write.string( className + ".enumValueOf(" + currentVariable + ")" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }


}
