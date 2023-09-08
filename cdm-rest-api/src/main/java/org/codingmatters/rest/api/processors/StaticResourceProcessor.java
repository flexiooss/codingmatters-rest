package org.codingmatters.rest.api.processors;

import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;

import java.io.IOException;
import java.io.InputStream;

public class StaticResourceProcessor implements Processor {
    private final String resource;
    private final String contentType;

    public StaticResourceProcessor(String resource, String contentType) {
        this.resource = resource;
        this.contentType = contentType;
    }

    @Override
    public void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        responseDelegate.contenType(this.contentType);
        try(InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.resource)) {
            responseDelegate.payload(in);
        }
    }
}
