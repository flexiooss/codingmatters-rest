package org.codingmatters.rest.php.api.client.model;

import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;

public class HttpMethodDescriptor {

    private final String httpMethodName;
    private String requestType;
    private String requestPackage;
    private String responsePackage;
    private String responseType;
    private Method method;
    private String path;
    private PropertyTypeSpec payload;


    public HttpMethodDescriptor( String httpMethodName ) {
        this.httpMethodName = httpMethodName;
    }

    public HttpMethodDescriptor withRequestType( String requestType, String requestPackage ) {
        this.requestType = requestType;
        this.requestPackage = requestPackage;
        return this;
    }

    public HttpMethodDescriptor withResponseType( String responseType, String responsePackage ) {
        this.responseType = responseType;
        this.responsePackage = responsePackage;
        return this;
    }

    public HttpMethodDescriptor withPayload( PropertyTypeSpec typeDeclaration ) {
        this.payload = typeDeclaration;
        return this;
    }

    public String getHttpMethodName() {
        return httpMethodName;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getRequestPackage() {
        return requestPackage;
    }

    public String getResponsePackage() {
        return responsePackage;
    }

    public String getResponseType() {
        return responseType;
    }

    public PropertyTypeSpec payload() {
        return payload;
    }

    public HttpMethodDescriptor withMethod( Method method ) {
        this.method = method;
        return this;
    }

    public Method method() {
        return method;
    }

    public HttpMethodDescriptor withPath( String path ) {
        this.path = path;
        return this;
    }

    public String path() {
        return path;
    }
}
