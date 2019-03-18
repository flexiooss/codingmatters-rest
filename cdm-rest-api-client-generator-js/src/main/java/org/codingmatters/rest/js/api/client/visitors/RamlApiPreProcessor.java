package org.codingmatters.rest.js.api.client.visitors;

import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.rest.parser.model.ParsedRequest;
import org.codingmatters.rest.parser.model.ParsedResponse;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.rest.parser.model.typed.TypedBody;
import org.codingmatters.rest.parser.model.typed.TypedHeader;
import org.codingmatters.rest.parser.model.typed.TypedParameter;
import org.codingmatters.rest.parser.model.typed.TypedQueryParam;
import org.codingmatters.rest.parser.model.typed.TypedUriParams;
import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeInSpecValueObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RamlApiPreProcessor implements ParsedRamlProcessor {


    private List<ParsedValueObject> processedValueObjects;

    public RamlApiPreProcessor( File rootDirectory, String typesPackage, PackageFilesBuilder packageBuilder ) {

    }

    @Override
    public void process( ParsedRaml parsedRaml ) throws ProcessingException {
        this.processedValueObjects = new ArrayList<>();
        for( ParsedRoute parsedRoute : parsedRaml.routes() ) {
            parsedRoute.process( this );
        }
    }

    @Override
    public void process( ParsedRoute parsedRoute ) throws ProcessingException {
        for( ParsedRequest parsedRequest : parsedRoute.requests() ) {
            ParsedValueObject requestValueObject = new ParsedValueObject( NamingUtility.requestName( parsedRoute.displayName(), parsedRequest.httpMethod().name() ) );
            for( TypedQueryParam typedQueryParam : parsedRequest.queryParameters() ) {
                requestValueObject.properties().add( parsePropertyFromTypedParam( typedQueryParam ) );
            }
            for( TypedHeader typedHeader : parsedRequest.headers() ) {
                requestValueObject.properties().add( parsePropertyFromTypedParam( typedHeader ) );
            }
            ParsedValueObject responseValueObject = new ParsedValueObject( NamingUtility.responseName( parsedRoute.displayName(), parsedRequest.httpMethod().name() ) );
            for( ParsedResponse parsedResponse : parsedRequest.responses() ) {
                String statusClass = NamingUtility.statusClassName( parsedResponse.code() );
                ParsedValueObject statusValueObject = new ParsedValueObject( statusClass );
                for( TypedHeader typedHeader : parsedResponse.headers() ) {
                    statusValueObject.properties().add( parsePropertyFromTypedParam( typedHeader ) );
                }
                if( parsedResponse.body().isPresent() ) {
                    statusValueObject.properties().add( parsePropertyFromTypedParam( parsedResponse.body().get() ) );
                }
                responseValueObject.properties().add( new ValueObjectProperty(
                        NamingUtility.statusProperty( parsedResponse.code() ),
                        new ObjectTypeInSpecValueObject( statusClass ) // todo pb with that ? use external + full name ?
                ) );
            }
            processedValueObjects.add( requestValueObject );
            processedValueObjects.add( responseValueObject );
            for( ParsedRoute subRoute : parsedRoute.subRoutes() ) {
                this.process( subRoute );
            }
        }
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

    public List<ParsedValueObject> processedValueObjects( ) {
        return processedValueObjects;
    }
}
