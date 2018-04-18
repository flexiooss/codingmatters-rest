package org.codingmatters.rest.undertow;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.internal.UriParameterProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nelt on 4/27/17.
 */
public class UndertowRequestDelegate implements RequestDelegate {

    static private final Logger log = LoggerFactory.getLogger(UndertowRequestDelegate.class);

    private final HttpServerExchange exchange;
    private Map<String, List<String>> uriParamsCache = null;
    private Map<String, List<String>> queryParamsCache = null;
    private Map<String, List<String>> headersCache = null;

    private final AtomicReference<byte[]> body = new AtomicReference<>(null);

    public UndertowRequestDelegate(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public String path() {
        return this.exchange.getRequestPath();
    }

    @Override
    public Matcher pathMatcher(String regex) {
        log.debug("req={} ; rel={} ; res={}",
                this.exchange.getRequestPath(),
                this.exchange.getRelativePath(),
                this.exchange.getResolvedPath()
        );
        return Pattern.compile(regex).matcher(this.path());
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
        synchronized (this.body) {
            if(this.body.get() == null) {
                if(! this.exchange.isBlocking()) {
                    this.exchange.startBlocking();
                }
                try {
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream in = this.exchange.getInputStream()) {
                        byte[] buffer = new byte[1024];
                        for (int read = in.read(buffer); read != -1; read = in.read(buffer)) {
                            out.write(buffer, 0, read);
                        }
                        out.flush();
                        out.close();
                        this.body.set(out.toByteArray());
                    }
                } catch (IOException e) {
                    log.error("failed reading body", e);
                    this.body.set(new byte[0]);
                }
            }
        }

        return new ByteArrayInputStream(this.body.get());
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
                    this.queryParamsCache.put(name, new ArrayList<>(this.exchange.getQueryParameters().get(name)));
                }
            }
        }
        return this.queryParamsCache;
    }

    @Override
    public synchronized Map<String, List<String>> headers() {
        if(this.headersCache == null) {
            this.headersCache = new HashMap<>();
            for (HeaderValues headerValues : this.exchange.getRequestHeaders()) {
                String headerName = headerValues.getHeaderName().toString();
                this.headersCache.put(headerName, new ArrayList<>(headerValues));
            }
        }
        return this.headersCache;
    }

}
