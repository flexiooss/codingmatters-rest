package org.codingmatters.rest.parser;

import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.rest.parser.model.ParsedRequest;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.rest.parser.model.RequestMethod;
import org.codingmatters.rest.parser.model.typed.TypedParameter;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypeList;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RamlParserClientTest {

    @Test
    public void testParameters() throws Exception {
        ParsedRaml raml = new RamlParser( "org.generated.types", "org.generated.api" ).parseFile( getRaml( "parameters.raml" ) );
        assertThat( raml.types().size(), is( 0 ) );
        assertThat( raml.routes().size(), is( 1 ) );

        ParsedRoute route = raml.routes().get( 0 );
        assertThat( route.displayName(), is( "HeaderParams" ) );
        assertThat( route.path(), is( "/header-params/{uriParams}" ) );
        assertThat( route.uriParameters().size(), is( 1 ) );
        assertThat( ((ValueObjectTypePrimitiveType) route.uriParameters().get( 0 ).type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );

        assertThat( route.requests().size(), is( 1 ) );
        ParsedRequest request = route.requests().get( 0 );

        assertThat( request.body(), is( Optional.empty() ) );
        assertThat( request.httpMethod(), is( RequestMethod.GET ) );
        assertThat( request.headers().size(), is( 14 ) );
        assertParams( new ArrayList<>( request.headers() ), "Request" );
        assertThat( request.queryParameters().size(), is( 14 ) );
        assertParams( new ArrayList<>( request.queryParameters() ), "Request" );
        assertThat( request.responses().size(), is( 1 ) );
        assertThat( request.responses().get( 0 ).code(), is( 200 ) );
        assertThat( request.responses().get( 0 ).body(), is( Optional.empty() ) );
        assertThat( request.responses().get( 0 ).headers().size(), is( 14 ) );
        assertParams( new ArrayList<>( request.responses().get( 0 ).headers() ), "Status200Response" );

        assertThat( route.subRoutes().size(), is( 1 ) );
        ParsedRoute subRoute = route.subRoutes().get( 0 );
        assertThat( subRoute.displayName(), is( "ParamsArray" ) );
        assertThat( subRoute.path(), is( "/header-params/{uriParams}/{uriParams}" ) );
        assertThat( subRoute.subRoutes().size(), is( 0 ) );
        assertThat( subRoute.requests().size(), is( 1 ) );
        assertThat( subRoute.requests().get( 0 ).headers().size(), is( 0 ) );
        assertThat( subRoute.requests().get( 0 ).queryParameters().size(), is( 0 ) );
        assertThat( subRoute.requests().get( 0 ).httpMethod(), is( RequestMethod.GET ) );
        assertThat( subRoute.requests().get( 0 ).body(), is( Optional.empty() ) );
        assertThat( subRoute.requests().get( 0 ).responses().size(), is( 1 ) );
        assertThat( subRoute.requests().get( 0 ).responses().get( 0 ).body(), is( Optional.empty() ) );
        assertThat( subRoute.requests().get( 0 ).responses().get( 0 ).headers().size(), is( 0 ) );
        assertThat( subRoute.requests().get( 0 ).responses().get( 0 ).code(), is( 200 ) );

        assertThat( subRoute.uriParameters().size(), is( 1 ) );
        assertThat( subRoute.uriParameters().get( 0 ).name(), is( "uriParams" ) );
        assertThat( ((ValueObjectTypeList) subRoute.uriParameters().get( 0 ).type()).name(), is( "ParamsArrayUriParamsList" ) );
        assertThat( ((ValueObjectTypeList) subRoute.uriParameters().get( 0 ).type()).packageName(), is( "org.generated.api.paramsarray" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) subRoute.uriParameters().get( 0 ).type()).type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );
    }

    private void assertParams( List<TypedParameter> headers, String request ) {
        String requestNamespace = request.toLowerCase();
        TypedParameter header;
        header = headers.get( 0 );
        assertThat( header.name(), is( "stringParam" ) );
        assertThat( ((ValueObjectTypePrimitiveType) header.type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );
        header = headers.get( 1 );
        assertThat( header.name(), is( "stringArrayParam" ) );
        assertThat( ((ValueObjectTypeList) header.type()).name(), is( "HeaderParamsGet" + request + "StringArrayParamList" ) );
        assertThat( ((ValueObjectTypeList) header.type()).packageName(), is( "org.generated.api.headerparamsget" + requestNamespace ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) header.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );
        header = headers.get( 2 );
        assertThat( header.name(), is( "intParam" ) );
        assertThat( ((ValueObjectTypePrimitiveType) header.type()).type(), is( YAML_PRIMITIVE_TYPES.LONG ) );
        header = headers.get( 3 );
        assertThat( header.name(), is( "intArrayParam" ) );
        assertThat( ((ValueObjectTypeList) header.type()).name(), is( "HeaderParamsGet" + request + "IntArrayParamList" ) );
        assertThat( ((ValueObjectTypeList) header.type()).packageName(), is( "org.generated.api.headerparamsget" + requestNamespace ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) header.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.LONG ) );
        header = headers.get( 4 );
        assertThat( header.name(), is( "floatParam" ) );
        assertThat( ((ValueObjectTypePrimitiveType) header.type()).type(), is( YAML_PRIMITIVE_TYPES.FLOAT ) );
        header = headers.get( 5 );
        assertThat( header.name(), is( "floatArrayParam" ) );
        assertThat( ((ValueObjectTypeList) header.type()).name(), is( "HeaderParamsGet" + request + "FloatArrayParamList" ) );
        assertThat( ((ValueObjectTypeList) header.type()).packageName(), is( "org.generated.api.headerparamsget" + requestNamespace ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) header.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.FLOAT ) );
        header = headers.get( 6 );
        assertThat( header.name(), is( "dateParam" ) );
        assertThat( ((ValueObjectTypePrimitiveType) header.type()).type(), is( YAML_PRIMITIVE_TYPES.DATE ) );
        header = headers.get( 7 );
        assertThat( header.name(), is( "dateArrayParam" ) );
        assertThat( ((ValueObjectTypeList) header.type()).name(), is( "HeaderParamsGet" + request + "DateArrayParamList" ) );
        assertThat( ((ValueObjectTypeList) header.type()).packageName(), is( "org.generated.api.headerparamsget" + requestNamespace ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) header.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.DATE ) );
        header = headers.get( 8 );
        assertThat( header.name(), is( "datetimeParam" ) );
        assertThat( ((ValueObjectTypePrimitiveType) header.type()).type(), is( YAML_PRIMITIVE_TYPES.DATE_TIME ) );
        header = headers.get( 9 );
        assertThat( header.name(), is( "datetimeArrayParam" ) );
        assertThat( ((ValueObjectTypeList) header.type()).name(), is( "HeaderParamsGet" + request + "DatetimeArrayParamList" ) );
        assertThat( ((ValueObjectTypeList) header.type()).packageName(), is( "org.generated.api.headerparamsget" + requestNamespace ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) header.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.DATE_TIME ) );
        header = headers.get( 10 );
        assertThat( header.name(), is( "timeParam" ) );
        assertThat( ((ValueObjectTypePrimitiveType) header.type()).type(), is( YAML_PRIMITIVE_TYPES.TIME ) );
        header = headers.get( 11 );
        assertThat( header.name(), is( "timeArrayParam" ) );
        assertThat( ((ValueObjectTypeList) header.type()).name(), is( "HeaderParamsGet" + request + "TimeArrayParamList" ) );
        assertThat( ((ValueObjectTypeList) header.type()).packageName(), is( "org.generated.api.headerparamsget" + requestNamespace ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) header.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.TIME ) );
        header = headers.get( 12 );
        assertThat( header.name(), is( "boolParam" ) );
        assertThat( ((ValueObjectTypePrimitiveType) header.type()).type(), is( YAML_PRIMITIVE_TYPES.BOOL ) );
        header = headers.get( 13 );
        assertThat( header.name(), is( "boolArrayParam" ) );
        assertThat( ((ValueObjectTypeList) header.type()).name(), is( "HeaderParamsGet" + request + "BoolArrayParamList" ) );
        assertThat( ((ValueObjectTypeList) header.type()).packageName(), is( "org.generated.api.headerparamsget" + requestNamespace ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) header.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.BOOL ) );
    }


    private String getRaml( String ramlFile ) {
        return Thread.currentThread().getContextClassLoader().getResource( ramlFile ).getPath();
    }
}
