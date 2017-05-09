package org.codingmatters.http.api;

import org.codingmatters.http.api.internal.UriParameterProcessor;

import java.io.InputStream;
import java.util.List;
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

    default Map<String,String> uriParameters(String pathExpression) {
        return new UriParameterProcessor(this).process(pathExpression);
    }

    Map<String,List<String>> queryParameters();

    enum Method {
        GET, POST, PUT, PATCH, DELETE, UNIMPLEMENTED;
    }
}
