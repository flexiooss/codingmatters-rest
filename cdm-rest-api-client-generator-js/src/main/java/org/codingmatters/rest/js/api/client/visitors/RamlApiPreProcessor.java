package org.codingmatters.rest.js.api.client.visitors;

import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.rest.parser.model.ParsedRequest;
import org.codingmatters.rest.parser.model.ParsedResponse;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.rest.parser.model.typed.*;
import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeExternalValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RamlApiPreProcessor implements ParsedRamlProcessor {

    private final String apiPackage;
    private Map<String, List<ParsedValueObject>> processedValueObjects;

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
            ParsedValueObject requestValueObject = new ParsedValueObject( NamingUtility.requestName( parsedRoute.displayName(), parsedRequest.httpMethod().name() ) );
            for( TypedUriParams typedUriParams : parsedRoute.uriParameters() ){
                requestValueObject.properties().add( parsePropertyFromTypedParam( typedUriParams ) );
            }
            for( TypedQueryParam typedQueryParam : parsedRequest.queryParameters() ){
                requestValueObject.properties().add( parsePropertyFromTypedParam( typedQueryParam ) );
            }
            for( TypedHeader typedHeader : parsedRequest.headers() ){
                requestValueObject.properties().add( parsePropertyFromTypedParam( typedHeader ) );
            }
            ParsedValueObject responseValueObject = new ParsedValueObject( NamingUtility.responseName( parsedRoute.displayName(), parsedRequest.httpMethod().name() ) );
            for( ParsedResponse parsedResponse : parsedRequest.responses() ){
                String statusClass = NamingUtility.statusClassName( parsedResponse.code() );
                ParsedValueObject statusValueObject = new ParsedValueObject( statusClass );
                for( TypedHeader typedHeader : parsedResponse.headers() ){
                    statusValueObject.properties().add( parsePropertyFromTypedParam( typedHeader ) );
                }
                if( parsedResponse.body().isPresent() ){
                    statusValueObject.properties().add( parsePropertyFromTypedParam( parsedResponse.body().get() ) );
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
            for( ParsedRoute subRoute : parsedRoute.subRoutes() ){
                this.process( subRoute );
            }
        }
    }

    private void addValueObject( ParsedValueObject valueObject, String packageName ) {
        this.processedValueObjects.putIfAbsent( packageName, new ArrayList<>() );
        this.processedValueObjects.get( packageName ).add( valueObject );
    }


    private ValueObjectProperty parsePropertyFromTypedParam( TypedParameter typedQueryParam ) {
        return new ValueObjectProperty( typedQueryParam.name(), typedQueryParam.type() );
    }

    @Override
    public void process( ParsedRequest parsedRequest ) {

    }

    @Override
    public void process( ParsedResponse parsedResponse ) {

    }


    @Override
    public void process( TypedBody typedBody ) {

    }

    @Override
    public void process( TypedHeader typedHeader ) {

    }

    @Override
    public void process( TypedQueryParam typedQueryParam ) {

    }

    @Override
    public void process( TypedUriParams typedUriParams ) {

    }

    public Map<String, List<ParsedValueObject>> processedValueObjects() {
        return processedValueObjects;
    }
}
