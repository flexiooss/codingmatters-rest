package org.codingmatters.rest.undertow.support;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class UndertowPongApp {
    public static void main(String[] args) {
        int port = args != null && args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        Undertow server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setHandler(new PongHandler())
                .build();
        server.start();
    }

    private static class PongHandler implements HttpHandler {
        @Override
        public void handleRequest(HttpServerExchange ex) throws Exception {
            if (ex.isInIoThread()) {
                ex.dispatch(this);
                return;
            }
            String req = "[" + ex.getRequestMethod() + "] " + ex.getRequestPath();
            System.out.println(req);
            ex.setStatusCode(200);
            ex.startBlocking();
            try(OutputStream out = ex.getOutputStream()) {
                String body = "pong : " + req;
                out.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
