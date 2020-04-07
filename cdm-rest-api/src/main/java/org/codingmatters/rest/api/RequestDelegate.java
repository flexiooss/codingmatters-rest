package org.codingmatters.rest.api;

import org.codingmatters.rest.api.internal.HeaderMap;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by nelt on 4/27/17.
 */
public interface RequestDelegate extends AutoCloseable {
    String path();
    Matcher pathMatcher(String regex);
    Method method();
    InputStream payload();
    String contentType();

    Map<String,List<String>> uriParameters(String pathExpression);
    Map<String,List<String>> queryParameters();
    Map<String,List<String>> headers();

    String absolutePath(String relative);

    enum Method {
        GET, POST, PUT, PATCH, DELETE, HEAD, UNIMPLEMENTED;
    }

    static Map<String,List<String>> createHeaderMap() {
        return new HeaderMap();
    }
    static Map<String, List<String>> createHeaderMap(Map<String, List<String>> from) {
        Map<String, List<String>> result = createHeaderMap();
        result.putAll(from);
        return result;
    }
}
