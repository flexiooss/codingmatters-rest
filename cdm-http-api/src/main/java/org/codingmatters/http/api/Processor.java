package org.codingmatters.http.api;

import java.io.IOException;

/**
 * Created by nelt on 4/27/17.
 */
public interface Processor {
    void process(RequestDeleguate requestDeleguate, ResponseDeleguate responseDeleguate) throws IOException;
}
