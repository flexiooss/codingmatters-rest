package org.codingmatters.rest.api;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by nelt on 4/27/17.
 */
public interface RequestDelegate {
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
}
