package org.codingmatters.rest.js.api.client.visitors;

import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.rest.parser.model.ParsedRequest;
import org.codingmatters.rest.parser.model.ParsedResponse;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.rest.parser.model.typed.TypedHeader;
import org.codingmatters.rest.parser.model.typed.TypedParameter;
import org.codingmatters.rest.parser.model.typed.TypedQueryParam;
import org.codingmatters.rest.parser.model.typed.TypedUriParams;
import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.parser.model.ParsedType;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeExternalValue;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RamlApiPreProcessor implements ParsedRamlProcessor {

    private final String apiPackage;
    private Map<String, List<ParsedType>> processedValueObjects;

    public RamlApiPreProcessor( String apiPackage ) {
        this.apiPackage = apiPackage;
    }

    @Override
    public void process( ParsedRaml parsedRaml ) throws ProcessingException {
        this.processedValueObjects = new HashMap<>();
        for( ParsedRoute parsedRoute : parsedRaml.routes() ){
            parsedRoute.process( this );
        }
    }

    @Override
    public void process( ParsedRoute parsedRoute ) throws ProcessingException {
        for( ParsedRequest parsedRequest : parsedRoute.requests() ){
            ParsedValueObject requestValueObject = new ParsedValueObject( NamingUtility.requestName( parsedRoute.displayName(), parsedRequest.httpMethod().name() ), apiPackage );
            for( TypedUriParams typedUriParams : parsedRoute.uriParameters() ){
                requestValueObject.properties().add( parsePropertyFromTypedParam( typedUriParams ) );
            }
            for( TypedQueryParam typedQueryParam : parsedRequest.queryParameters() ){
                requestValueObject.properties().add( parsePropertyFromTypedParam( typedQueryParam ) );
            }
            for( TypedHeader typedHeader : parsedRequest.headers() ){
                requestValueObject.properties().add( parsePropertyFromTypedParam( typedHeader ) );
            }
            if( parsedRequest.body().isPresent() ){
                ValueObjectProperty body = parsePropertyFromTypedParam( parsedRequest.body().get() );
                requestValueObject.properties().add( body );
            }
            ParsedValueObject responseValueObject = new ParsedValueObject( NamingUtility.responseName( parsedRoute.displayName(), parsedRequest.httpMethod().name() ), apiPackage );
            for( ParsedResponse parsedResponse : parsedRequest.responses() ){
                String statusClass = NamingUtility.statusClassName( parsedResponse.code() );
                ParsedValueObject statusValueObject = new ParsedValueObject( statusClass, apiPackage );
                for( TypedHeader typedHeader : parsedResponse.headers() ){
                    statusValueObject.properties().add( parsePropertyFromTypedParam( typedHeader ) );
                }
                if( parsedResponse.body().isPresent() ){
                    ValueObjectProperty body = parsePropertyFromTypedParam( parsedResponse.body().get() );
                    statusValueObject.properties().add( body );
                }
                String statusPackage = apiPackage + "." + NamingUtility.className( parsedRoute.displayName() + parsedRequest.httpMethod().name() + "response" ).toLowerCase();
                responseValueObject.properties().add( new ValueObjectProperty(
                        NamingUtility.statusProperty( parsedResponse.code() ),
                        new ObjectTypeExternalValue( statusPackage + "." + statusClass )
                ) );
                addValueObject( statusValueObject, statusPackage );
            }
            addValueObject( requestValueObject, apiPackage );
            addValueObject( responseValueObject, apiPackage );
        }
        for( ParsedRoute subRoute : parsedRoute.subRoutes() ){
            this.process( subRoute );
        }
    }

    private void addValueObject( ParsedValueObject valueObject, String packageName ) {
        this.processedValueObjects.putIfAbsent( packageName, new ArrayList<>() );
        this.processedValueObjects.get( packageName ).add( valueObject );
    }


    private ValueObjectProperty parsePropertyFromTypedParam( TypedParameter typedQueryParam ) {
        return new ValueObjectProperty( typedQueryParam.name(), typedQueryParam.type() );
    }

    public Map<String, List<ParsedType>> processedValueObjects() {
        return processedValueObjects;
    }
}
