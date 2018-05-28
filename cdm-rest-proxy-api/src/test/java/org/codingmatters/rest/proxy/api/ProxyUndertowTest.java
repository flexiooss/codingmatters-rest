package org.codingmatters.rest.proxy.api;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.okhttp.OkHttpClientWrapper;
import org.codingmatters.rest.api.client.okhttp.OkHttpRequester;
import org.codingmatters.rest.undertow.CdmHttpUndertowHandler;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ProxyUndertowTest {


    @Rule
    public UndertowResource proxy = new UndertowResource(new CdmHttpUndertowHandler(this::proxy));

    private Requester proxyRequester;
    private Requester clientRequester;

    private void proxy(RequestDelegate req, ResponseDelegate resp) {
        try {
            org.codingmatters.rest.api.client.ResponseDelegate serviceResponse = ProxyRequest.from(req).to(this.proxyRequester);
            ProxyResponse.from(serviceResponse).to(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Rule
    public UndertowResource service = new UndertowResource(this::serve);


    @Before
    public void setUp() throws Exception {
        this.proxyRequester = new OkHttpRequester(OkHttpClientWrapper.build(), this.service.baseUrl());
        this.clientRequester = new OkHttpRequester(OkHttpClientWrapper.build(), this.proxy.baseUrl());
    }

    private Consumer<HttpServerExchange> serviceResponse = null;

    private void serve(HttpServerExchange exchange) {
        exchange.setStatusCode(200);
        if(serviceResponse != null) {
            serviceResponse.accept(exchange);
        }
    }

    @Test
    public void responseHeaders() throws Exception {
        this.serviceResponse = exchange -> {
            exchange.getResponseHeaders().put(HttpString.tryFromString("h1"), "v1");
        };

        org.codingmatters.rest.api.client.ResponseDelegate response = this.clientRequester.get();

        assertThat(response.code(), is(200));
        assertThat(response.header("h1"), is(arrayContaining("v1")));
    }
}
