package org.codingmatters.rest.server.netty;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.internal.UriParameterProcessor;
import org.codingmatters.rest.io.headers.HeaderEncodingHandler;
import org.codingmatters.rest.netty.utils.DynamicByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

public class NettyHttpRequestDeleguate implements RequestDelegate {
    static private final Logger log = LoggerFactory.getLogger(NettyHttpRequestDeleguate.class);

    private final HttpRequest request;
    private final DynamicByteBuffer body;

    private final URL url;
    private Map<String, Map<String, List<String>>> uriParamsCache = new HashMap<>();
    private Map<String, List<String>> queryParamsCache = null;
    private Map<String, List<String>> headersCache = null;

    public NettyHttpRequestDeleguate(String host, int port, HttpRequest request, DynamicByteBuffer body) throws IOException {
        this.request = request;
        try {
            this.url = new URL(request.protocolVersion().protocolName(), host, port, request.uri());
        } catch (MalformedURLException e) {
            throw new IOException("failed creating request deleguate", e);
        }
        this.body = body;
    }

    @Override
    public String path() {
        return this.url.getPath();
    }

    @Override
    public Method method() {
        String methodString = this.request.method().name();
        return Method.from(methodString);
    }

    @Override
    public InputStream payload() throws IOException {
        return this.body.stream();
    }

    @Override
    public String contentType() {
        return this.request.headers().getAsString(HttpHeaderNames.CONTENT_TYPE);
    }

    @Override
    public Map<String, List<String>> uriParameters(String pathExpression) {
        if(! this.uriParamsCache.containsKey(pathExpression)) {
            this.uriParamsCache.put(pathExpression, new UriParameterProcessor(this).process(pathExpression));
        }
        return this.uriParamsCache.get(pathExpression);
    }

    @Override
    public Map<String, List<String>> queryParameters() {
        if(this.queryParamsCache == null) {
            this.queryParamsCache = RequestDelegate.createHeaderMap();

            final String[] pairs = this.url.getQuery().split("&");
            for (String pair : pairs) {
                String key;
                String value = null;

                int idx = pair.indexOf("=");
                if(idx > 0) {
                    try {
                        key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                        if (pair.length() > idx + 1) {
                            value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                        } else {
                            value = "";
                        }
                    } catch (UnsupportedEncodingException e) {
                        log.error("error reading query parameter : " + pair, e);
                        break;
                    }
                } else {
                    key = pair;
                }
                if(! this.queryParamsCache.containsKey(key)) {
                    this.queryParamsCache.put(key, new ArrayList<>());
                }
                this.queryParamsCache.get(key).add(value);
            }
        }
        return this.queryParamsCache;
    }

    @Override
    public Map<String, List<String>> headers() {
        if(this.headersCache == null) {
            this.headersCache = RequestDelegate.createHeaderMap();
            for (String headerName : this.request.headers().names()) {
                List<String> headerValues = this.request.headers().getAll(headerName);
                List<String> collect = new ArrayList<>( headerValues );
                if( headerName.endsWith( "*" )){
                    headerName = headerName.substring( 0, headerName.length()-1 );
                    collect = headerValues.stream().map( HeaderEncodingHandler::decodeHeader ).collect( Collectors.toList() );
                }
                this.headersCache.putIfAbsent( headerName, new ArrayList<>() );
                this.headersCache.get( headerName ).addAll( collect );
            }
        }
        return this.headersCache;
    }

    @Override
    public String absolutePath(String relative) {
        if(relative == null) {
            relative = "";
        }
        while(relative.startsWith("/")) {
            relative = relative.substring(1);
        }

        return String.format("%s://%s:%s/%s",
                this.url.getProtocol(),
                this.url.getHost(), this.url.getPort(),
                relative
        );
    }

    @Override
    public void close() throws Exception {
        this.body.release();
    }
}
