package org.codingmatters.rest.api.client.okhttp.exception;

import java.io.IOException;

public class ConnectionTimeoutException extends IOException {
    public ConnectionTimeoutException(String msg, Exception cause) {
        super(msg, cause);
    }
}
