package org.codingmatters.rest.undertow.support;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import org.junit.rules.ExternalResource;

import java.net.ServerSocket;

/**
 * Created by nelt on 5/11/17.
 */
public class UndertowResource extends ExternalResource {

    private final HttpHandler handler;

    private Undertow server;
    private String baseUrl;

    public UndertowResource(HttpHandler handler) {
        this.handler = handler;
    }

    public Undertow server() {
        return server;
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
                .setSocketOption(UndertowOptions.IDLE_TIMEOUT, 2000)
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
