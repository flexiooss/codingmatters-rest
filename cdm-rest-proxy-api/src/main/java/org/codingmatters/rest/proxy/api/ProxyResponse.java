package org.codingmatters.rest.proxy.api;

import org.codingmatters.rest.api.client.ResponseDelegate;
import org.codingmatters.rest.proxy.api.utils.MapOfListAction;
import org.codingmatters.rest.proxy.api.utils.MapOfListModification;

import java.io.IOException;
import java.util.*;

public class ProxyResponse {

    public static ProxyResponse from(ResponseDelegate serviceResponse) {
        return new ProxyResponse(serviceResponse);
    }

    private final ResponseDelegate originalResponse;

    private final List<MapOfListModification> headerModifications = new LinkedList<>();

    public ProxyResponse(ResponseDelegate serviceResponse) {
        this.originalResponse = serviceResponse;
    }

    public void to(org.codingmatters.rest.api.ResponseDelegate response) throws IOException {
        try {
            response.status(this.originalResponse.code());
            if (this.modifiedCode != null) {
                response.status(this.modifiedCode.get());
            }
            response.contenType(this.originalResponse.contentType());
            if (this.modifiedContentType != null) {
                response.contenType(this.modifiedContentType.orElse(null));
            }

            HashMap<String, List<String>> headers = new HashMap<>();
            for (String header : this.originalResponse.headerNames()) {
                String[] headerValues = this.originalResponse.header(header);
                headers.put(header, Arrays.asList(headerValues));
            }

            for (MapOfListModification headerModification : this.headerModifications) {
                headerModification.appy(headers);
            }

            for (String header : headers.keySet()) {
                List<String> headerValues = headers.get(header);
                String[] values = headerValues.toArray(new String[headerValues.size()]);

                response.addHeader(header, values);
            }

            response.payload(this.originalResponse.body());
            if (this.modifiedBody != null) {
                response.payload(this.modifiedBody.orElse(null));
            }
        } finally {
            try {
                this.originalResponse.close();
            } catch (Exception e) {
                throw new IOException("error closing original response delegate while proxying it", e);
            }
        }
    }

    private Optional<Integer> modifiedCode = null;
    private Optional<byte[]> modifiedBody = null;
    private Optional<String> modifiedContentType = null;


    public ProxyResponse withStatus(int code) {
        this.modifiedCode = Optional.of(code);
        return this;
    }

    public ProxyResponse withBody(byte[] bytes) {
        this.modifiedBody = Optional.ofNullable(bytes);
        return this;
    }

    public ProxyResponse withContentType(String contentType) {
        this.modifiedContentType = Optional.ofNullable(contentType);
        return this;
    }

    public ProxyResponse withHeader(String name, String ... values) {
        this.headerModifications.add(new MapOfListModification(MapOfListAction.PUT, name, values));
        return this;
    }

    public ProxyResponse addHeaderValues(String name, String ... values) {
        this.headerModifications.add(new MapOfListModification(MapOfListAction.ADD, name, values));
        return this;
    }

    public ProxyResponse removeHeaders(String name) {
        this.headerModifications.add(new MapOfListModification(MapOfListAction.REMOVE, name));
        return this;
    }


}
