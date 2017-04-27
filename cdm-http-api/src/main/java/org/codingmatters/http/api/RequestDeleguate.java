package org.codingmatters.http.api;

import java.io.InputStream;
import java.util.regex.Matcher;

/**
 * Created by nelt on 4/27/17.
 */
public interface RequestDeleguate {
    Matcher pathMatcher(String regex);
    Method method();
    InputStream payload();

    String absolutePath(String relative);

    enum Method {
        GET, POST, PUT, PATCH, DELETE, UNIMPLEMENTED;
    }
}
