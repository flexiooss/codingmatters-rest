package org.codingmatters.http.api;

import org.codingmatters.http.api.internal.PathParameterProcessor;

import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by nelt on 4/27/17.
 */
public interface RequestDeleguate {
    Matcher pathMatcher(String regex);
    Method method();
    InputStream payload();

    String absolutePath(String relative);

    default Map<String,String> pathParameters(String pathExpression) {
        return new PathParameterProcessor(this).process(pathExpression);
    }

    enum Method {
        GET, POST, PUT, PATCH, DELETE, UNIMPLEMENTED;
    }
}
