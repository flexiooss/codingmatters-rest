package org.codingmatters.rest.parser;

import org.codingmatters.rest.parser.model.ParsedRequest;
import org.codingmatters.rest.parser.model.ParsedResponse;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.rest.parser.model.RequestMethod;
import org.codingmatters.rest.parser.model.typed.TypedBody;
import org.codingmatters.rest.parser.model.typed.TypedHeader;
import org.codingmatters.rest.parser.model.typed.TypedQueryParam;
import org.codingmatters.rest.parser.model.typed.TypedUriParams;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.*;

public class ParsingUtils {

    private final Map<String, TypeDeclaration> allTypes;
    private final List<ParsedValueObject> valueObjects;
    private String typesPackage;
    private Stack<String> context;

    public ParsingUtils( Map<String, TypeDeclaration> allTypes, String typesPackage ) {
        this.allTypes = allTypes;
        this.context = new Stack<>();
        this.typesPackage = typesPackage;
        this.valueObjects = new ArrayList<>();

    }

    public boolean isArray( TypeDeclaration property ) {
        return property instanceof ArrayTypeDeclaration;
    }

    public Optional<String> isAlreadyDefined( TypeDeclaration typeDeclaration ) {
        if( typeDeclaration.annotations() != null ){
            for( AnnotationRef annotation : typeDeclaration.annotations() ){
                if( annotation.name().equalsIgnoreCase( "(already-defined)" ) ){
                    return Optional.of( annotation.structuredValue().value().toString() );
                }
            }
        }
        Optional<String> inSpecValueObjectType = isInSpecValueObject( typeDeclaration );
        if( inSpecValueObjectType.isPresent() ){ // check only once in parent ( produce infinite loop with recursion )
            if( allTypes.get( inSpecValueObjectType.get() ).annotations() != null ){
                for( AnnotationRef annotation : allTypes.get( inSpecValueObjectType.get() ).annotations() ){
                    if( annotation.name().equalsIgnoreCase( "(already-defined)" ) ){
                        return Optional.of( annotation.structuredValue().value().toString() );
                    }
                }
            }
        }
        return Optional.empty();
    }

    public Optional<RAML_PRIMITIVE_TYPES> isSinglePrimitiveType( TypeDeclaration property ) {
        String type = getPropertyType( property );
        Optional<RAML_PRIMITIVE_TYPES> ramlType = RAML_PRIMITIVE_TYPES.fromRaml( type );
        return ramlType;
    }

    private Optional<String> isInSpecValueObject( TypeDeclaration property ) {
        String type = getPropertyType( property );
        if( allTypes.containsKey( type ) ){
            return Optional.of( type );
        }
        return Optional.empty();
    }

    private boolean isNested( TypeDeclaration property ) {
        return property instanceof ObjectTypeDeclaration && !((ObjectTypeDeclaration) property).properties().isEmpty();
    }

    private boolean isEnum( TypeDeclaration property ) {
        return property instanceof StringTypeDeclaration && !((StringTypeDeclaration) property).enumValues().isEmpty();
    }

    private String getPropertyType( TypeDeclaration property ) {
        String type;
        if( property instanceof ArrayTypeDeclaration ){
            ArrayTypeDeclaration arrayTypeDeclaration = (ArrayTypeDeclaration) property;
            if( arrayTypeDeclaration.type().endsWith( "[]" ) ){
                type = arrayTypeDeclaration.type();
            } else {
                type = arrayTypeDeclaration.items().type();
            }
        } else {
            type = property.type();
            if( type == null || (type.equals( "object" ) && property instanceof ObjectTypeDeclaration && !((ObjectTypeDeclaration) property).properties().isEmpty()) ){
                type = property.name();
            }
        }
        return type.replace( "[]", "" );
    }

    public ValueObjectType parseListType( String typeDeclarationName, ArrayTypeDeclaration property ) throws ProcessingException {
        ValueObjectType valueObjectType = parseType( typeDeclarationName, property.items() );
        return valueObjectType;
    }


    public ValueObjectType parseType( String typeDeclarationName, TypeDeclaration property ) throws ProcessingException {
        if( isArray( property ) ){
            String propName = property.name();
            if( propName.equals( "application/json" ) ){
                propName = "payload";
            }
            String name = NamingUtility.className( typeDeclarationName, propName, "List" );
            ValueObjectType type = parseListType( typeDeclarationName, (ArrayTypeDeclaration) property );
            String namespace = NamingUtility.namespace( typeDeclarationName );
            return new ValueObjectTypeList( name, type, typesPackage + "." + namespace );

        } else if( isEnum( property ) ){
            return new YamlEnumInSpecEnum(
                    NamingUtility.className( context.toArray( new String[0] ) ),
                    NamingUtility.namespace( typeDeclarationName ),
                    ((StringTypeDeclaration) property).enumValues()
            );
        } else if( isAlreadyDefined( property ).isPresent() ){
            return new ObjectTypeExternalValue( isAlreadyDefined( property ).get() );
        } else if( isInSpecValueObject( property ).isPresent() ){
            String propertyType = getPropertyType( property );
            Optional<ParsedValueObject> valueObject = valueObjects.stream().filter( vo->vo.name().equals( propertyType ) ).findFirst();
            if( valueObject.isPresent() ){
                return new ObjectTypeInSpecValueObject( propertyType, valueObject.get().packageName() );
            }
            return new ObjectTypeInSpecValueObject( propertyType, typesPackage );
        } else if( isSinglePrimitiveType( property ).isPresent() ){
            return new ValueObjectTypePrimitiveType( isSinglePrimitiveType( property ).get().toYaml().name() );
        } else if( isNested( property ) ){
            return new ObjectTypeNested( parseNested( typeDeclarationName, (ObjectTypeDeclaration) property ), NamingUtility.namespace( typeDeclarationName ) );
        }
        throw new ProcessingException( "Cannot parse this declaration" );
    }

    private ParsedValueObject parseNested( String typeDeclarationName, ObjectTypeDeclaration property ) throws ProcessingException {
        ParsedValueObject parsedValueObject = new ParsedValueObject( NamingUtility.className( typeDeclarationName, property.name() ), typesPackage );
        for( TypeDeclaration typeDeclaration : property.properties() ){
            ValueObjectType type = this.parseType( property.name(), typeDeclaration );
            parsedValueObject.properties().add( new ValueObjectProperty( typeDeclaration.name(), type ) );
        }
        return parsedValueObject;
    }

    public Stack<String> context() {
        return context;
    }

    public ParsedRoute parseRoute( Resource resource ) throws ProcessingException {
        String displayName = NamingUtility.getJoinedName( resource.displayName().value() );
        ParsedRoute route = new ParsedRoute( resource.resourcePath(), displayName );
        if( resource.uriParameters() != null ){
            for( TypeDeclaration parameters : resource.uriParameters() ){
                route.uriParameters().add( new TypedUriParams( parameters.name(), parseType( displayName, parameters ) ) );
            }
        }
        for( Method request : resource.methods() ){
            ParsedRequest parsedRequest;
            Optional<TypedBody> requestBody = Optional.empty();
            String method = NamingUtility.firstLetterUpperCase( request.method().toLowerCase() );
            RequestMethod httpMethod = RequestMethod.valueOf( request.method().toUpperCase() );
            String requestClassName = NamingUtility.requestName( displayName, method );
            if( hasBody( request ) ){
                requestBody = Optional.of( new TypedBody( parseType(
                        requestClassName,
                        request.body().get( 0 )
                ) ) );
            }
            parsedRequest = new ParsedRequest( httpMethod, requestBody );
            for( TypeDeclaration header : request.headers() ){
                parsedRequest.headers().add( new TypedHeader( header.name(), parseType( requestClassName, header ) ) );
            }
            for( TypeDeclaration queryParam : request.queryParameters() ){
                parsedRequest.queryParameters().add( new TypedQueryParam( queryParam.name(), parseType( requestClassName, queryParam ) ) );
            }
            for( Response response : request.responses() ){
                Optional<TypedBody> responseBody = Optional.empty();
                String responseClassName = NamingUtility.responseName( displayName, method, "Status", response.code().value() );
                if( hasBody( response ) ){
                    responseBody = Optional.of( new TypedBody( parseType(
                            responseClassName,
                            response.body().get( 0 )
                    ) ) );
                }
                ParsedResponse parsedResponse = new ParsedResponse( Integer.valueOf( response.code().value() ), responseBody );
                for( TypeDeclaration typeDeclaration : response.headers() ){
                    parsedResponse.headers().add( new TypedHeader( typeDeclaration.name(), parseType( responseClassName, typeDeclaration ) ) );
                }
                parsedRequest.responses().add( parsedResponse );
            }
            route.requests().add( parsedRequest );
        }
        if( resource.resources() != null ){
            for( Resource subRresource : resource.resources() ){
                route.subRoutes().add( parseRoute( subRresource ) );
            }
        }
        return route;
    }

    private boolean hasBody( Response response ) {
        return response.body() != null && !response.body().isEmpty();
    }

    private boolean hasBody( Method method ) {
        return method.body() != null && !method.body().isEmpty();
    }

    public void typesPackage( String typesPackage ) {
        this.typesPackage = typesPackage;
    }

    public void addValueObject( ParsedValueObject valueObject ) {
        this.valueObjects.add( valueObject );
    }
}
