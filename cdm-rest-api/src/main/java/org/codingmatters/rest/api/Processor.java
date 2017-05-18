package org.codingmatters.rest.api;

import java.io.IOException;

/**
 * Created by nelt on 4/27/17.
 */
public interface Processor {
    void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException;
}
