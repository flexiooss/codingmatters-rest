package org.codingmatters.rest.server.acceptance;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class BaseAcceptanceTest {
    public abstract String baseUrl();

    private Processor testProcessor;

    public void withProcessor(Processor processor) {
        this.testProcessor = processor;
    }

    protected void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        this.testProcessor.process(requestDelegate, responseDelegate);
    }


    protected Request.Builder requestBuilder() {
        return this.requestBuilder("");
    }

    protected Request.Builder requestBuilder(String path) {
        return new Request.Builder()
                .url(this.baseUrl() + path);
    }

    protected RequestBody emptyJsonBody() {
        String payload = "{}";
        return jsonBody(payload);
    }

    protected RequestBody jsonBody(String payload) {
        return RequestBody.create(MediaType.parse("application/json"), payload);
    }

    protected String readAsString(RequestDelegate requestDelegate) throws IOException {
        StringBuilder payloadAsString = new StringBuilder();
        try(InputStream in = requestDelegate.payload(); Reader reader = new InputStreamReader(in)) {
            char [] buffer = new char[1024];
            for(int read = reader.read(buffer) ; read != -1 ; read = reader.read(buffer)) {
                payloadAsString.append(buffer, 0, read);
            }
        }
        return payloadAsString.toString();
    }

}
