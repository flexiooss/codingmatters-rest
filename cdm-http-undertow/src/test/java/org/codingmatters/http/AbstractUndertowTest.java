package org.codingmatters.http;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.codingmatters.http.api.Processor;
import org.codingmatters.http.api.RequestDeleguate;
import org.codingmatters.http.api.ResponseDeleguate;
import org.codingmatters.http.support.UndertowResource;
import org.junit.Rule;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by nelt on 5/7/17.
 */
public class AbstractUndertowTest {

    @Rule
    public UndertowResource undertow = new UndertowResource(new CdmHttpUndertowHandler(this::process));

    private Processor testProcessor;

    public String baseUrl() {
        return this.undertow.baseUrl();
    }

    private void process(RequestDeleguate requestDeleguate, ResponseDeleguate responseDeleguate) throws IOException {
        this.testProcessor.process(requestDeleguate, responseDeleguate);
    }

    protected void withProcessor(Processor processor) {
        this.testProcessor = processor;
    }


    protected Request.Builder requestBuilder() {
        return this.requestBuilder("");
    }

    protected Request.Builder requestBuilder(String path) {
        return new Request.Builder()
                .url(this.undertow.baseUrl() + path);
    }

    protected RequestBody emptyJsonBody() {
        String payload = "{}";
        return jsonBody(payload);
    }

    protected RequestBody jsonBody(String payload) {
        return RequestBody.create(MediaType.parse("application/json"), payload);
    }

    protected String readAsString(RequestDeleguate requestDeleguate) throws IOException {
        StringBuilder payloadAsString = new StringBuilder();
        try(InputStream in = requestDeleguate.payload(); Reader reader = new InputStreamReader(in)) {
            char [] buffer = new char[1024];
            for(int read = reader.read(buffer) ; read != -1 ; read = reader.read(buffer)) {
                payloadAsString.append(buffer, 0, read);
            }
        }
        return payloadAsString.toString();
    }
}
