package org.codingmatters.rest.undertow.internal;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.codingmatters.rest.io.CountedReferenceTemporaryFile;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RequestBodyTest {

    @Rule
    public UndertowResource undertow = new UndertowResource(this::process);

    private OkHttpClient client = new OkHttpClient();

    private HttpHandler handler = null;

    private void process(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this::process);
            return;
        }
        if(! exchange.isBlocking()) {
            exchange.startBlocking();
        }
        if(handler != null) handler.handleRequest(exchange);
    }

    @Test
    public void givenSmallRequestBody__whenBodyIsRead__thenByteArrayInputStreamIsUsed() throws Exception {
        AtomicReference<String> inputStreamClass = new AtomicReference<>(null);
        this.handler = (exchange) -> {
            try(InputStream inputStream = RequestBody.from(exchange).inputStream()) {
                inputStreamClass.set(inputStream.getClass().getName());
            }
        };

        this.requestWithPayload("small".getBytes());

        assertThat(inputStreamClass.get(), is(ByteArrayInputStream.class.getName()));
    }

    @Test
    public void givenBigRequestBody__whenBodyIsRead__thenCountedReferenceOutputStreamIsUsed() throws Exception {
        AtomicReference<String> inputStreamClass = new AtomicReference<>(null);
        this.handler = (exchange) -> {
            try(InputStream inputStream = RequestBody.from(exchange).inputStream()) {
                inputStreamClass.set(inputStream.getClass().getName());
            }
        };

        this.requestWithPayload(this.big());

        assertThat(inputStreamClass.get(), is(CountedReferenceTemporaryFile.CountedReferenceInputStream.class.getName()));
    }

    private byte[] big() throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        while(result.size() < 1500) {
            result.write("more content".getBytes());
        }
        return result.toByteArray();
    }

    private void requestWithPayload(byte[] bytes) throws IOException {
        this.client.newCall(
            new Request.Builder().url(this.undertow.baseUrl())
                    .post(okhttp3.RequestBody.create(MediaType.parse("application/octet-stream"), bytes)
                    ).build()
        ).execute();
    }
}