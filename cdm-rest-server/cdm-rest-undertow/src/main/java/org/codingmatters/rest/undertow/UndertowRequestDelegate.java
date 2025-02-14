package org.codingmatters.rest.undertow;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.internal.UriParameterProcessor;
import org.codingmatters.rest.io.headers.HeaderEncodingHandler;
import org.codingmatters.rest.undertow.internal.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by nelt on 4/27/17.
 */
public class UndertowRequestDelegate implements RequestDelegate {

    static private final Logger log = LoggerFactory.getLogger(UndertowRequestDelegate.class);

    private final HttpServerExchange exchange;

    private Map<String, Map<String, List<String>>> uriParamsCache = new HashMap<>();
    private Map<String, List<String>> queryParamsCache = null;
    private Map<String, List<String>> headersCache = null;

    private final AtomicReference<RequestBody> body = new AtomicReference<>(null);

    public UndertowRequestDelegate(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public String path() {
        return this.exchange.getRequestURI();
    }

    @Override
    public Method method() {
        String methodString = exchange.getRequestMethod().toString();
        return Method.from(methodString);
    }

    @Override
    public InputStream payload() throws IOException {
        synchronized (this.body) {
            if(this.body.get() == null) {
                if(! this.exchange.isBlocking()) {
                    this.exchange.startBlocking();
                }
                this.body.set(RequestBody.from(exchange));
            }
        }

        return this.body.get().inputStream();
    }

    @Override
    public String contentType() {
        return this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
    }

    @Override
    public String absolutePath(String relative) {
        if(relative == null) {
            relative = "";
        }
        while(relative.startsWith("/")) {
            relative = relative.substring(1);
        }
        return String.format("%s://%s/%s",
                exchange.getRequestScheme(),
                exchange.getHostAndPort(),
                relative
        );
    }

    @Override
    public Map<String, List<String>> uriParameters(String pathExpression) {
        if(! this.uriParamsCache.containsKey(pathExpression)) {
            this.uriParamsCache.put(pathExpression, new UriParameterProcessor(this).process(pathExpression));
        }
        return this.uriParamsCache.get(pathExpression);
    }

    @Override
    public synchronized Map<String, List<String>> queryParameters() {
        if(this.queryParamsCache == null) {
            this.queryParamsCache = RequestDelegate.createHeaderMap();
            for (String name : this.exchange.getQueryParameters().keySet()) {
                if (this.exchange.getQueryParameters().get(name) != null) {
                    this.queryParamsCache.put(name, new ArrayList<>(this.exchange.getQueryParameters().get(name)));
                }
            }
        }
        return this.queryParamsCache;
    }

    @Override
    public synchronized Map<String, List<String>> headers() {
        if(this.headersCache == null) {
            this.headersCache = RequestDelegate.createHeaderMap();
            for (HeaderValues headerValues : this.exchange.getRequestHeaders()) {
                String headerName = headerValues.getHeaderName().toString();
                List<String> collect = new ArrayList<>();
                if (headerName.endsWith("*")) {
                    headerName = headerName.substring(0, headerName.length() - 1);
                }
                for (String headerValue : headerValues) {
                    if(HeaderEncodingHandler.isEncoded(headerValue)) {
                        collect.add(HeaderEncodingHandler.decodeHeader(headerValue));
                    } else {
                        collect.add(headerValue);
                    }
                }

                this.headersCache.putIfAbsent( headerName, new ArrayList<>() );
                this.headersCache.get( headerName ).addAll( collect );
            }
        }
        return this.headersCache;
    }

    @Override
    public void close() throws Exception {
        if(this.body.get() != null) {
            this.body.get().close();
        }
    }
}
