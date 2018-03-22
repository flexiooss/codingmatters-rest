package org.codingmatters.rest.api;

/**
 * Created by nelt on 4/27/17.
 */
public interface ResponseDelegate {
    ResponseDelegate contenType(String contenType);
    ResponseDelegate status(int code);
    ResponseDelegate addHeader(String name, String ... value);
    ResponseDelegate payload(String payload, String charset);
    ResponseDelegate payload(byte[] bytes);
}
