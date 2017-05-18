package org.codingmatters.rest.undertow;

import io.undertow.server.HttpServerExchange;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.internal.UriParameterProcessor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nelt on 4/27/17.
 */
public class UndertowRequestDelegate implements RequestDelegate {

    private final HttpServerExchange exchange;
    private Map<String, List<String>> uriParamsCache = null;
    private Map<String, List<String>> queryParamsCache = null;

    public UndertowRequestDelegate(HttpServerExchange exchange) {
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

    @Override
    public Map<String, List<String>> uriParameters(String pathExpression) {
        if(this.uriParamsCache == null) {
            this.uriParamsCache = new UriParameterProcessor(this).process(pathExpression);
        }
        return this.uriParamsCache;
    }

    @Override
    public synchronized Map<String, List<String>> queryParameters() {
        if(this.queryParamsCache == null) {
            this.queryParamsCache = new HashMap<>();
            for (String name : this.exchange.getQueryParameters().keySet()) {
                if (this.exchange.getQueryParameters().get(name) != null) {
                    this.queryParamsCache.put(name, new LinkedList<>(this.exchange.getQueryParameters().get(name)));
                }
            }
        }
        return this.queryParamsCache;
    }
}
