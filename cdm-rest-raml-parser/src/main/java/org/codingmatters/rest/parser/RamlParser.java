package org.codingmatters.rest.parser;

import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.model.ParsedEnum;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;
import org.codingmatters.value.objects.js.parser.model.types.YamlEnumExternalEnum;
import org.codingmatters.value.objects.js.parser.model.types.YamlEnumInSpecEnum;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RamlParser {

    private final String typesPackage;
    private final String apiPackage;

    public RamlParser( String typesPackage, String apiPackage ) {
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
    }

    public ParsedRaml parseFile( String ramlFilePath ) throws ProcessingException {
        RamlModelResult ramlModel;
        ramlModel = new RamlModelBuilder().buildApi( ramlFilePath );
        return parseRamlModel( ramlModel );
    }

    public ParsedRaml parseRamlModel( RamlModelResult ramlModel ) throws ProcessingException {
        ParsedRaml parsedRaml = new ParsedRaml( ramlModel.getApiV10().title().value() );
        Api api = ramlModel.getApiV10();
        ParsingUtils parsingUtils = this.parseTypes( parsedRaml, api );
        this.parseApi( parsedRaml, api, parsingUtils );
        return parsedRaml;
    }

    private void parseApi( ParsedRaml parsedRaml, Api api, ParsingUtils parsingUtils ) throws ProcessingException {
        parsingUtils.typesPackage( apiPackage );
        for( Resource resource : api.resources() ){
            parsedRaml.routes().add( parsingUtils.parseRoute( resource ) );
        }
    }

    private ParsingUtils parseTypes( ParsedRaml parsedRaml, Api api ) throws ProcessingException {
        Map<String, TypeDeclaration> allTypes = new HashMap<>();
        api.types().forEach( type->allTypes.put( type.name(), type ) );
        ParsingUtils parsingUtils = new ParsingUtils( allTypes, typesPackage );

        for( TypeDeclaration typeDeclaration : api.types() ){
            if( typeDeclaration.type().equals( "object" ) ){
                parsingUtils.context().push( typeDeclaration.name() );
                if (!parsingUtils.isAnnotated(typeDeclaration, "(already-defined)").isPresent()) {
                    ParsedValueObject valueObject = new ParsedValueObject( typeDeclaration.name(), typesPackage );
                    for( TypeDeclaration property : ((ObjectTypeDeclaration) typeDeclaration).properties() ){
                        parsingUtils.context().push( property.name() );
                        ValueObjectType type = parsingUtils.parseType( typeDeclaration.name(), property );
                        parsingUtils.context().pop();
                        valueObject.properties().add( new ValueObjectProperty( property.name(), type ) );
                    }
                    parsedRaml.types().add( valueObject );
                    parsingUtils.addValueObject( valueObject );
                }
                parsingUtils.context().pop();
            } else if( parsingUtils.isFactorizedEnum( typeDeclaration ) ){
                // it's okay
                List<String> enumValues = ((StringTypeDeclaration)typeDeclaration).enumValues();
                parsedRaml.types().add( new ParsedEnum( typeDeclaration.name(), typesPackage, enumValues ) );
            } else {
                throw new ProcessingException( "Cannot parse a non object type: " + typeDeclaration.name() );
            }
        }
        return parsingUtils;
    }

}
