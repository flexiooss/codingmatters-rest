package org.codingmatters.rest.tests.api;

import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.internal.UriParameterProcessor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRequestDeleguate implements RequestDelegate {

    static public Builder request(Method method, String url) {
        return new Builder(method, url);
    }

    static public class Builder {
        private final Method method;
        private final String requestPath;
        private String contentType = "aplication/json";
        private ByteArrayInputStream payload = null;

        private Map<String, List<String>> queryParamsCache = new TreeMap<>();
        private Map<String, List<String>> headersCache = new TreeMap<>();

        public Builder(Method method, String requestPath) {
            this.method = method;
            this.requestPath = requestPath;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder payload(ByteArrayInputStream payload) {
            this.payload = payload;
            return this;
        }

        public Builder addQueryParam(String name, String ... values) {
            if(values != null) {
                this.queryParamsCache.put(name, Arrays.asList(values));
            } else {
                this.queryParamsCache.put(name, null);
            }
            return this;
        }

        public Builder addHeader(String name, String ... values) {
            if(values != null) {
                this.headersCache.put(name, Arrays.asList(values));
            } else {
                this.headersCache.put(name, null);
            }
            return this;
        }

        public RequestDelegate build() {
            return new TestRequestDeleguate(this.method, this.requestPath, this.contentType, this.payload, this.queryParamsCache, this.headersCache);
        }
    }

    private final Method method;
    private final String requestPath;
    private final String contentType;
    private final ByteArrayInputStream payload;

    private final Map<String, List<String>> queryParamsCache;
    private final Map<String, List<String>> headersCache;

    public TestRequestDeleguate(
            Method method, String requestPath, String contentType, ByteArrayInputStream payload,
            Map<String, List<String>> queryParamsCache,
            Map<String, List<String>> headersCache) {
        this.method = method;
        this.requestPath = requestPath;
        this.contentType = contentType;
        this.payload = payload;
        this.queryParamsCache = queryParamsCache;
        this.headersCache = headersCache;
    }

    @Override
    public String path() {
        return this.requestPath;
    }

    @Override
    public Matcher pathMatcher(String regex) {
        return Pattern.compile(regex).matcher(this.requestPath);
    }

    @Override
    public Method method() {
        return this.method;
    }

    @Override
    public InputStream payload() {
        return this.payload;
    }

    @Override
    public String contentType() {
        return this.contentType;
    }

    @Override
    public Map<String, List<String>> uriParameters(String pathExpression) {
        return new UriParameterProcessor(this).process(pathExpression);
    }

    @Override
    public Map<String, List<String>> queryParameters() {
        return queryParamsCache;
    }

    @Override
    public Map<String, List<String>> headers() {
        return headersCache;
    }

    @Override
    public String absolutePath(String relative) {
        if(relative == null) {
            relative = "";
        }
        while(relative.startsWith("/")) {
            relative = relative.substring(1);
        }

        String scheme = this.requestPath.split("://")[0];
        String host = this.requestPath.split("://")[1];
        if(host.indexOf('/') != -1) {
            host = host.substring(0, host.indexOf('/'));
        }

        return String.format("%s://%s/%s",
                scheme,
                host,
                relative
        );
    }
}
