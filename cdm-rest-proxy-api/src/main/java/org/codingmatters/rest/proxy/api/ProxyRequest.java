package org.codingmatters.rest.proxy.api;

import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.ResponseDelegate;
import org.codingmatters.rest.proxy.api.utils.MapOfListAction;
import org.codingmatters.rest.proxy.api.utils.MapOfListModification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProxyRequest {

    static public ProxyRequest from(RequestDelegate request) {
        return new ProxyRequest(request);
    }

    private final RequestDelegate originalRequest;
    private final List<MapOfListModification> headerModifications = new LinkedList<>();
    private final List<MapOfListModification> parametersModifications = new LinkedList<>();


    private ProxyRequest(RequestDelegate request) {
        this.originalRequest = request;
    }

    public ProxyRequest withHeader(String name, String ... values) {
        this.headerModifications.add(new MapOfListModification(MapOfListAction.PUT, name, values));
        return this;
    }

    public ProxyRequest withoutHeader(String name) {
        this.headerModifications.add(new MapOfListModification(MapOfListAction.REMOVE, name));
        return this;
    }

    public ProxyRequest withAddedHeader(String name, String ... values) {
        this.headerModifications.add(new MapOfListModification(MapOfListAction.ADD, name, values));
        return this;
    }

    public ProxyRequest withQueryParameters(String name, String ... values) {
        this.parametersModifications.add(new MapOfListModification(MapOfListAction.PUT, name, values));
        return this;
    }

    public ProxyRequest withoutQueryParameters(String name) {
        this.parametersModifications.add(new MapOfListModification(MapOfListAction.REMOVE, name, null));
        return this;
    }

    public ProxyRequest withAddedQueryParameters(String name, String ... values) {
        this.parametersModifications.add(new MapOfListModification(MapOfListAction.ADD, name, values));
        return this;
    }



    public ResponseDelegate to(Requester requester) throws IOException {

        Map<String, List<String>> headers = this.apply(this.headerModifications, this.originalRequest.headers());
        for (String header : headers.keySet()) {
            requester.header(header, headers.get(header));
        }

        Map<String, List<String>> parameters = this.apply(this.parametersModifications, this.originalRequest.queryParameters());
        for (String param : parameters.keySet()) {
            requester.parameter(param, parameters.get(param));
        }


        switch (this.originalRequest.method()) {
            case GET:
                return requester.get();
            case POST:
                return requester.post(this.originalRequest.contentType(), this.payloadAsBytes());
            case PUT:
                return requester.put(this.originalRequest.contentType(), this.payloadAsBytes());
            case PATCH:
                return requester.patch(this.originalRequest.contentType(), this.payloadAsBytes());
            case DELETE:
                return requester.delete();
            case HEAD:
                return requester.head();
            default:
                throw new IOException("method not implemented : " + this.originalRequest.method());

        }
    }

    private Map<String, List<String>> apply(List<MapOfListModification> modifications, Map<String, List<String>> to) {
        Map<String, List<String>> result = new TreeMap<>(to);

        for (MapOfListModification modification : modifications) {
            modification.appy(result);
        }

        return result;
    }

    private byte[] payloadAsBytes() throws IOException {
        try(InputStream in = this.originalRequest.payload() ; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if(in == null) return new byte[0];

            byte[] buffer = new byte[1024];
            for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                out.write(buffer, 0, read);
            }
            out.flush();
            return out.toByteArray();
        }
    }

}
