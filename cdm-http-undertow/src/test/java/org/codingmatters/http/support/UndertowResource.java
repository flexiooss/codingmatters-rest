package org.codingmatters.http.support;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import org.codingmatters.http.api.Processor;
import org.junit.rules.ExternalResource;

import java.net.ServerSocket;

/**
 * Created by nelt on 5/11/17.
 */
public class UndertowResource extends ExternalResource {

    private final HttpHandler handler;

    private Undertow server;
    private Processor testProcessor = null;
    private String baseUrl;

    public UndertowResource(HttpHandler handler) {
        this.handler = handler;
    }

    public String baseUrl() {
        return baseUrl;
    }

    @Override
    protected void before() throws Throwable {
        int port;
        try(ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        this.server = Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(this.handler)
                .build();
        this.server.start();

        this.baseUrl = "http://localhost:" + port;
    }

    @Override
    protected void after() {
        this.server.stop();
    }
}
