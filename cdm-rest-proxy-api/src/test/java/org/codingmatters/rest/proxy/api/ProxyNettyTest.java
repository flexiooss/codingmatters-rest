package org.codingmatters.rest.proxy.api;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.okhttp.OkHttpClientWrapper;
import org.codingmatters.rest.api.client.okhttp.OkHttpRequester;
import org.codingmatters.rest.netty.utils.DynamicByteBuffer;
import org.codingmatters.rest.netty.utils.HttpRequestHandler;
import org.codingmatters.rest.netty.utils.HttpServer;
import org.codingmatters.rest.server.netty.ProcessorRequestHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ProxyNettyTest {
    private HttpServer service;
    private HttpServer proxy;
    private Requester proxyRequester;
    private Requester clientRequester;

    private final AtomicReference<HttpRequestHandler> handler = new AtomicReference<>();
    private final AtomicReference<Processor> proc = new AtomicReference<>();

    @Before
    public void setUp() throws Exception {
        this.service = HttpServer.testServer((host, port) -> new ProcessorRequestHandler(proc.get(), host, port));
        this.service.start();
        System.out.println("1111111111111111111111111111111111111111111111");
        this.proxy = HttpServer.testServer((host, port) -> new ProcessorRequestHandler((requestDelegate, responseDelegate) -> proxy(requestDelegate, responseDelegate), host, port));
        this.proxy.start();
        System.out.println("2222222222222222222222222222222222222222222222");
        this.proxyRequester = new OkHttpRequester(OkHttpClientWrapper.build(), this.serviceBaseUrl());
        this.clientRequester = new OkHttpRequester(OkHttpClientWrapper.build(), this.proxyBaseUrl());
        System.out.println("33333333333333333333333333333333333333333333333");
    }

    @After
    public void tearDown() throws Exception {
        this.service.shutdown();
        this.proxy.shutdown();
    }


    private void proxy(RequestDelegate req, ResponseDelegate resp) {
        try {
            org.codingmatters.rest.api.client.ResponseDelegate serviceResponse = ProxyRequest.from(req).to(this.proxyRequester);
            ProxyResponse.from(serviceResponse).to(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String proxyBaseUrl() {
        return "http://" + this.proxy.host() + ":" + this.proxy.port();
    }

    public String serviceBaseUrl() {
        return "http://" + this.service.host() + ":" + this.service.port();
    }

    @Test
    public void responseHeaders() throws Exception {
        this.proc.set((request, response) -> {
            response.addHeader("h1", "v1");
        });

        org.codingmatters.rest.api.client.ResponseDelegate response = this.clientRequester.get();

        assertThat(response.code(), is(200));
        assertThat(response.header("h1"), is(arrayContaining("v1")));
    }
}
