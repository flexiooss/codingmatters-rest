package org.codingmatters.rest.api;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by nelt on 4/27/17.
 */
public interface RequestDelegate {
    Matcher pathMatcher(String regex);
    Method method();
    InputStream payload();

    String absolutePath(String relative);

    Map<String,List<String>> uriParameters(String pathExpression);

    Map<String,List<String>> queryParameters();

    enum Method {
        GET, POST, PUT, PATCH, DELETE, UNIMPLEMENTED;
    }
}
