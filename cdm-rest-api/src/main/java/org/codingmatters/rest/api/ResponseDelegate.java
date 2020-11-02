package org.codingmatters.rest.api;

import java.io.InputStream;

/**
 * Created by nelt on 4/27/17.
 */
public interface ResponseDelegate extends AutoCloseable {
    ResponseDelegate contenType(String contenType);
    ResponseDelegate status(int code);
    ResponseDelegate addHeader(String name, String ... value);
    ResponseDelegate addHeaderIfNot(String name, String ... value);
    ResponseDelegate payload(String payload, String charset);
    ResponseDelegate payload(byte[] bytes);
    ResponseDelegate payload(InputStream in);
}
