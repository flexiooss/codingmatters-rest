package org.codingmatters.rest.api;

import org.codingmatters.rest.api.internal.HeaderMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nelt on 4/27/17.
 */
public interface RequestDelegate extends AutoCloseable {
    String path();
    default Matcher pathMatcher(String regex) {
        return Pattern.compile(regex).matcher(this.path());
    }
    Method method();
    InputStream payload() throws IOException;
    String contentType();

    Map<String,List<String>> uriParameters(String pathExpression);
    Map<String,List<String>> queryParameters();
    Map<String,List<String>> headers();

    String absolutePath(String relative);

    enum Method {
        GET, POST, PUT, PATCH, DELETE, HEAD, UNIMPLEMENTED;

        static public Method from(String methodString) {
            for (Method method : Method.values()) {
                if(method.name().equals(methodString.toUpperCase())) {
                    return method;
                }
            }
            return Method.UNIMPLEMENTED;
        }
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
