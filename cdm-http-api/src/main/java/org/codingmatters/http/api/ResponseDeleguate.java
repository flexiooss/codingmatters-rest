package org.codingmatters.http.api;

/**
 * Created by nelt on 4/27/17.
 */
public interface ResponseDeleguate {
    ResponseDeleguate contenType(String contenType);
    ResponseDeleguate status(int code);
    ResponseDeleguate addHeader(String name, String value);
    ResponseDeleguate payload(String payload, String charset);
}
