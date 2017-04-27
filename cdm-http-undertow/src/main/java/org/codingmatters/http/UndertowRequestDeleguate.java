package org.codingmatters.http;

import io.undertow.server.HttpServerExchange;
import org.codingmatters.http.api.RequestDeleguate;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nelt on 4/27/17.
 */
public class UndertowRequestDeleguate implements RequestDeleguate {

    private final HttpServerExchange exchange;

    public UndertowRequestDeleguate(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public Matcher pathMatcher(String regex) {
        return Pattern.compile(regex).matcher(this.exchange.getRelativePath());
    }

    @Override
    public Method method() {
        String methodString = exchange.getRequestMethod().toString().toUpperCase();
        for (Method method : Method.values()) {
            if(method.name().equals(methodString)) {
                return method;
            }
        }

        return Method.UNIMPLEMENTED;
    }

    @Override
    public InputStream payload() {
        if(! this.exchange.isBlocking()) {
            this.exchange.startBlocking();
        }
        return this.exchange.getInputStream();
    }

    @Override
    public String absolutePath(String relative) {
        return String.format("%s://%s/%s",
                exchange.getRequestScheme(),
                exchange.getHostAndPort(),
                relative
        );
    }
}
