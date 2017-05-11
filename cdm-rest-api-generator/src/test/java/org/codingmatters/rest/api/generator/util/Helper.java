package org.codingmatters.rest.api.generator.util;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by nelt on 5/2/17.
 */
public class Helper {
    static public File fileResource(String resource) throws URISyntaxException {
        return new File(Thread.currentThread().getContextClassLoader().getResource(resource).toURI());
    }
}
