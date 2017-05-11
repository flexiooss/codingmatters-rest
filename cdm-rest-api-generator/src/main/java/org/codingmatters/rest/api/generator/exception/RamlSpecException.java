package org.codingmatters.rest.api.generator.exception;

/**
 * Created by nelt on 5/2/17.
 */
public class RamlSpecException extends Exception {
    public RamlSpecException(String s) {
        super(s);
    }

    public RamlSpecException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
