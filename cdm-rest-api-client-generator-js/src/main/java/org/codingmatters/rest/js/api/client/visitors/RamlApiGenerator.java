package org.codingmatters.rest.js.api.client.visitors;

import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.rest.parser.model.ParsedRequest;
import org.codingmatters.rest.parser.model.ParsedResponse;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.rest.parser.model.typed.TypedBody;
import org.codingmatters.rest.parser.model.typed.TypedHeader;
import org.codingmatters.rest.parser.model.typed.TypedQueryParam;
import org.codingmatters.rest.parser.model.typed.TypedUriParams;
import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;

import java.io.IOException;

public class RamlApiGenerator implements ParsedRamlProcessor {

    @Override
    public void process( ParsedRaml parsedRaml ) throws ProcessingException {

    }

    @Override
    public void process( ParsedRoute parsedRoute ) throws ProcessingException {
        JsFileWriter write = new JsFileWriter( "somewhere" ); // TODO path
        try {
            write.line( "class " + parsedRoute.displayName() + " {" );
            // TODO constructor
            for( ParsedRequest parsedRequest : parsedRoute.requests() ){
                write.line( NamingUtility.propertyName( parsedRoute.displayName() + parsedRequest.httpMethod() ) + "( " + "request" + " ){" );
                write.line( "}" );
            }
            write.line( "}" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing route " + parsedRoute.displayName(), e );
        }
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
}
