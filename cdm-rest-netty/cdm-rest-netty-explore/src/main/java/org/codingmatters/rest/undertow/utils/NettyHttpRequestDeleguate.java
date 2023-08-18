package org.codingmatters.rest.undertow.utils;

import io.netty.handler.codec.http.HttpRequest;
import org.codingmatters.rest.api.RequestDelegate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class NettyHttpRequestDeleguate implements RequestDelegate {
    public NettyHttpRequestDeleguate(HttpRequest request, DynamicByteBuffer body) {
    }

    @Override
    public String path() {
        return null;
    }

    @Override
    public Matcher pathMatcher(String regex) {
        return null;
    }

    @Override
    public Method method() {
        return null;
    }

    @Override
    public InputStream payload() throws IOException {
        return null;
    }

    @Override
    public String contentType() {
        return null;
    }

    @Override
    public Map<String, List<String>> uriParameters(String pathExpression) {
        return null;
    }

    @Override
    public Map<String, List<String>> queryParameters() {
        return null;
    }

    @Override
    public Map<String, List<String>> headers() {
        return null;
    }

    @Override
    public String absolutePath(String relative) {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
